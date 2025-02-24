module HW5.Evaluator
  ( eval
  ) where

import Codec.Compression.Zlib (bestCompression, compressLevel, compressWith, decompress,
                               defaultCompressParams)
import Codec.Serialise (deserialiseOrFail, serialise)
import Control.Monad (foldM)
import Control.Monad.Except (throwError)
import Control.Monad.Trans.Except (ExceptT, runExceptT)
import Data.ByteString (ByteString, pack, unpack)
import qualified Data.ByteString as B
import Data.ByteString.Lazy (fromStrict, toStrict)
import Data.Foldable (toList)
import Data.Maybe (fromMaybe)
import Data.Ratio (denominator, numerator)
import Data.Semigroup (Semigroup (stimes))
import qualified Data.Semigroup as S
import Data.Sequence (Seq)
import qualified Data.Sequence as Seq
import qualified Data.Text (length, reverse, strip, toLower, toUpper)
import qualified Data.Text as D
import Data.Text.Encoding (decodeUtf8', encodeUtf8)
import Data.Word (Word8)
import HW5.Base (HiError (..), HiExpr (..), HiFun (..), HiValue (..))


-- The line below is commented to prevent build error
-- in project template because of 'redundant constraint' warning.
-- Uncomment this line and use correct function signature
-- while doing the homework.

eval :: Monad m => HiExpr -> m (Either HiError HiValue)
eval expr = runExceptT (eval_ expr)

unars :: [ HiFun ]
unars = [ HiFunNot, HiFunLength, HiFunToUpper, HiFunToLower, HiFunTrim, HiFunReverse, HiFunPackBytes, HiFunEncodeUtf8,
 HiFunDecodeUtf8, HiFunUnpackBytes, HiFunZip, HiFunUnzip, HiFunSerialise, HiFunDeserialise]

binars :: [ HiFun ]
binars = [ HiFunDiv, HiFunMul, HiFunAdd, HiFunSub, HiFunAnd, HiFunOr, HiFunLessThan
  ,HiFunGreaterThan, HiFunEquals, HiFunNotLessThan, HiFunNotGreaterThan, HiFunNotEquals]

ternars :: [ HiFun ]
ternars = [ HiFunIf ]

evalArgs :: Monad m => [HiExpr] -> ExceptT HiError m [HiValue]
evalArgs = mapM eval_

eval_ :: Monad m => HiExpr -> ExceptT HiError m HiValue
eval_ (HiExprValue val) = return val
eval_ (HiExprApply expr args) = do
  exprVal <- eval_ expr
  argVal <- evalArgs args
  case exprVal of
    HiValueFunction HiFunFold -> evalFold HiFunFold argVal
    HiValueFunction HiFunList -> return $ HiValueList (Seq.fromList argVal)
    HiValueFunction HiFunRange -> evalRange HiFunRange argVal
    HiValueFunction f -> 
      case length argVal of
        1 | f `elem` unars   -> evalFunctionUnary f argVal
        2 | f `elem` binars  -> evalFunctionBinary f argVal
        3 | f `elem` ternars -> evalFunctionTernars f argVal
        _                    -> throwError HiErrorArityMismatch
    HiValueString f -> evalString f argVal
    HiValueList f -> evalList f argVal
    HiValueBytes f -> evalBytes f argVal
    _ -> throwError HiErrorInvalidFunction
    
evalRange :: Monad m => HiFun -> [HiValue] -> ExceptT HiError m HiValue
evalRange _ args = case args of
  [HiValueNumber start, HiValueNumber end] -> do
    return $ HiValueList (Seq.fromList $ map HiValueNumber [start .. end])
  _ -> throwError HiErrorArityMismatch

evalFold :: Monad m => HiFun -> [HiValue] -> ExceptT HiError m HiValue
evalFold HiFunFold [HiValueFunction f, HiValueList list] =
  case Data.Foldable.toList list of
    []       -> throwError HiErrorInvalidArgument
    [a]      -> return a
    (a:b:bs) -> foldM fold_ a (b:bs)
  where
    fold_ acc next = do
      result <- evalFunctionBinary f [acc, next]
      case result of
        HiValueNumber _ -> return result
        _               -> throwError HiErrorInvalidArgument
evalFold _ _ = throwError HiErrorInvalidArgument

evalString :: Monad m => D.Text -> [HiValue] -> ExceptT HiError m HiValue
evalString fun args = case (fun, args) of
    (str, [HiValueNumber index]) ->
        case parseInt index of
          Right i  -> parseStrAndNumber i str
          Left err -> throwError err

    (str, [HiValueNumber startIndex, HiValueNumber endIndex]) ->
        case (parseInt startIndex, parseInt endIndex) of
          (Right startI, Right endI) -> parseStrAndNumbers startI endI str
          (Left err, _)              -> throwError err
          (_, Left err)              -> throwError err

    (str, [HiValueNull, HiValueNumber endIndex]) ->
        case parseInt endIndex of
          Right endI -> parseStrAndNumbers 0 endI str
          Left err   -> throwError err

    (str, [HiValueNumber startIndex, HiValueNull]) ->
        case parseInt startIndex of
          Right startI -> parseStrAndNumbers startI (D.length str) str
          Left err     -> throwError err      
    _ -> throwError HiErrorArityMismatch

parseStrAndNumber :: Monad m => Int -> D.Text -> ExceptT HiError m HiValue
parseStrAndNumber i str =
  if i >= 0 && i < D.length str
    then return (HiValueString (D.pack [D.unpack str !! i]))
    else return HiValueNull

updateIndex :: Int -> Int -> Int
updateIndex len index
  | index < 0 = len + index 
  | otherwise = index

parseStrAndNumbers :: Monad m => Int -> Int -> D.Text -> ExceptT HiError m HiValue
parseStrAndNumbers startIndex endIndex str =
  let len = D.length str 
      newStart = updateIndex len startIndex
      newdEnd = updateIndex len endIndex
  in if newStart >= 0 && newdEnd >= newStart
       then return (HiValueString (D.take (newdEnd - newStart) (D.drop newStart str)))
       else return $ HiValueString $ D.pack ""

evalBytes :: Monad m => ByteString -> [HiValue] -> ExceptT HiError m HiValue
evalBytes fun args = case (fun, args) of
    (bytes, [HiValueNumber index]) ->
        case parseInt index of
          Right i  -> parseByteAndNumber i bytes
          Left err -> throwError err

    (bytes, [HiValueNumber startIndex, HiValueNumber endIndex]) ->
        case (parseInt startIndex, parseInt endIndex) of
          (Right startI, Right endI) -> parseByteAndNumbers startI endI bytes
          (Left err, _)              -> throwError err
          (_, Left err)              -> throwError err
    _ -> throwError HiErrorArityMismatch

parseByteAndNumber :: Monad m => Int -> ByteString -> ExceptT HiError m HiValue
parseByteAndNumber i bytes =
  if i >= 0 && i < B.length bytes
    then return $ HiValueNumber (fromIntegral (B.index bytes i))
    else return HiValueNull

parseByteAndNumbers :: Monad m => Int -> Int -> ByteString -> ExceptT HiError m HiValue
parseByteAndNumbers startI endI bytes =
  if startI >= 0 && startI <= endI
    then return $ HiValueBytes (B.take (endI - startI) (B.drop startI bytes))
    else return HiValueNull

parseInt :: Rational -> Either HiError Int
parseInt number
    | denominator number == 1 = Right (fromIntegral (numerator number))
    | otherwise = Left HiErrorInvalidArgument

evalList :: Monad m => Seq HiValue -> [HiValue] -> ExceptT HiError m HiValue
evalList fun args = case (fun, args) of
    (list, [HiValueNumber index]) -> do
        case parseInt index of
          Right i  -> parseListAndNumber i list
          Left err -> throwError err

    (list, [HiValueNumber startIndex, HiValueNumber endIndex]) -> do
        case (parseInt startIndex, parseInt endIndex) of
          (Right startI, Right endI) -> parseListAndNumbers startI endI list
          (Left err, _)              -> throwError err
          (_, Left err)              -> throwError err
    (_, _) -> throwError HiErrorArityMismatch

parseListAndNumber :: Monad m => Int -> Seq HiValue -> ExceptT HiError m HiValue
parseListAndNumber i list =
  if i >= 0 && i < Seq.length list
      then return $ Seq.index list i
      else return HiValueNull

parseListAndNumbers :: Monad m => Int -> Int -> Seq HiValue -> ExceptT HiError m HiValue
parseListAndNumbers startI endI list =
    if startI >= 0 && startI <= endI
          then return $ HiValueList (Seq.take (endI - startI) (Seq.drop startI list))
          else return HiValueNull

evalFunctionBinary :: Monad m => HiFun -> [HiValue] -> ExceptT HiError m HiValue
evalFunctionBinary fun args = case (fun, args) of
    (HiFunDiv, [HiValueNumber _, HiValueNumber 0]) -> throwError HiErrorDivideByZero
    _ -> fromMaybe (throwError HiErrorInvalidArgument) (applyFunction fun args)

applyFunction :: Monad m => HiFun -> [HiValue] -> Maybe (ExceptT HiError m HiValue)
applyFunction fun args = case args of
    [HiValueNumber a, HiValueNumber b]     -> handleNumbers fun a b
    [HiValueString a, HiValueString b]     -> handleStrings fun a b
    [HiValueBool a, HiValueBool b]         -> handleBools fun a b
    [HiValueString a, HiValueNumber b]     -> handleStringAndNumber fun a b
    [HiValueList a, HiValueNumber b]       -> handleListAndNumber fun a b
    [HiValueBool a, HiValueNumber b]       -> handleNumberAndBool fun a b
    [HiValueNumber a, HiValueBool b]       -> handleBoolAndNumber fun a b
    [HiValueFunction a, HiValueFunction b] -> handleFunction fun a b
    [HiValueList a, HiValueList b]         -> handleList fun a b
    [HiValueBytes a, HiValueBytes b]       -> handleBytes fun a b
    [HiValueBytes a, HiValueNumber b]      -> handleBytesAndNumber fun a b
    _                                      -> Nothing

handleBytes :: Monad m => HiFun -> ByteString -> ByteString -> Maybe (ExceptT HiError m HiValue)
handleBytes fun a b = case fun of
    HiFunAdd -> Just $ return (HiValueBytes (a <> b))
    _        -> Nothing

handleBytesAndNumber :: Monad m => HiFun -> ByteString -> Rational -> Maybe (ExceptT HiError m HiValue)
handleBytesAndNumber fun a b = case fun of
    HiFunMul -> Just $ return (HiValueBytes (stimes (numerator b) a))
    _        -> Nothing

handleBoolAndNumber :: Monad m => HiFun -> Rational -> Bool -> Maybe (ExceptT HiError m HiValue)
handleBoolAndNumber fun _ _ = case fun of
    HiFunLessThan       -> Just $ return (HiValueBool False)
    HiFunGreaterThan    -> Just $ return (HiValueBool True)
    HiFunEquals         -> Just $ return (HiValueBool False)
    HiFunNotEquals      -> Just $ return  (HiValueBool True)
    HiFunNotLessThan    -> Just $ return  (HiValueBool True)
    HiFunNotGreaterThan -> Just $ return (HiValueBool False)
    _                   -> Nothing


handleFunction :: Monad m => HiFun -> HiFun -> HiFun -> Maybe (ExceptT HiError m HiValue)
handleFunction fun a b = case fun of
    HiFunEquals -> Just $ return  (HiValueBool (a == b))
    _           -> Nothing

handleBinaryOp :: Monad m => (a -> HiValue) -> a -> Maybe (ExceptT HiError m HiValue)
handleBinaryOp op result = Just $ return (op result)

handleComparison :: (Ord a, Monad m) => HiFun -> a -> a -> Maybe (ExceptT HiError m HiValue)
handleComparison fun a b = case fun of
    HiFunLessThan       -> handleBinaryOp HiValueBool (a < b)
    HiFunGreaterThan    -> handleBinaryOp HiValueBool (a > b)
    HiFunEquals         -> handleBinaryOp HiValueBool (a == b)
    HiFunNotEquals      -> handleBinaryOp HiValueBool (a /= b)
    HiFunNotLessThan    -> handleBinaryOp HiValueBool (a >= b)
    HiFunNotGreaterThan -> handleBinaryOp HiValueBool (a <= b)
    _                   -> Nothing

handleNumbers :: Monad m => HiFun -> Rational -> Rational -> Maybe (ExceptT HiError m HiValue)
handleNumbers fun a b = case fun of
    HiFunAdd -> handleBinaryOp HiValueNumber (a + b)
    HiFunSub -> handleBinaryOp HiValueNumber (a - b)
    HiFunMul -> handleBinaryOp HiValueNumber (a * b)
    HiFunDiv -> handleBinaryOp HiValueNumber (a / b)
    _        -> handleComparison fun a b

handleBools :: Monad m => HiFun -> Bool -> Bool -> Maybe (ExceptT HiError m HiValue)
handleBools fun a b = case fun of
    HiFunAnd -> handleBinaryOp HiValueBool (a && b)
    HiFunOr  -> handleBinaryOp HiValueBool (a || b)
    _        -> handleComparison fun a b

handleNumberAndBool :: Monad m => HiFun -> Bool -> Rational -> Maybe (ExceptT HiError m HiValue)
handleNumberAndBool fun _ _ = case fun of
    HiFunLessThan       -> Just $ return (HiValueBool True)
    HiFunGreaterThan    -> Just $ return (HiValueBool False)
    HiFunEquals         -> Just $ return (HiValueBool False)
    HiFunNotEquals      -> Just $ return  (HiValueBool True)
    HiFunNotLessThan    -> Just $ return  (HiValueBool False)
    HiFunNotGreaterThan -> Just $ return (HiValueBool True)
    _                   -> Nothing


handleStrings :: Monad m => HiFun -> D.Text -> D.Text -> Maybe (ExceptT HiError m HiValue)
handleStrings fun a b = case fun of
    HiFunAdd       -> Just $ return (HiValueString (a <> b))
    HiFunDiv       -> Just $ return (HiValueString (a <> D.pack "/" <> b))
    HiFunEquals    -> Just $ return (HiValueBool (a == b))
    HiFunNotEquals -> Just $ return (HiValueBool (a /= b))
    _              -> Nothing


handleList :: Monad m => HiFun -> Seq HiValue -> Seq HiValue -> Maybe (ExceptT HiError m HiValue)
handleList fun a b = case fun of
    HiFunAdd -> Just $ return (HiValueList (a Seq.>< b))
    _        -> Nothing

handleListAndNumber :: Monad m => HiFun -> Seq HiValue -> Rational -> Maybe (ExceptT HiError m HiValue)
handleListAndNumber fun a b = case fun of
    HiFunMul -> Just $ return (HiValueList (Data.Semigroup.stimes  (numerator b) a))
    _        -> Nothing

handleStringAndNumber :: Monad m => HiFun -> D.Text -> Rational -> Maybe (ExceptT HiError m HiValue)
handleStringAndNumber fun a b = case fun of
    HiFunMul -> Just $ return (HiValueString (S.stimes (numerator b) a))
    _        -> Nothing

evalFunctionUnary :: Monad m => HiFun -> [HiValue] -> ExceptT HiError m HiValue
evalFunctionUnary fun arg = case (fun, arg) of
    (HiFunNot, [HiValueBool True])           -> return (HiValueBool False)
    (HiFunNot, [HiValueBool False])          -> return (HiValueBool True)
    (HiFunLength, [HiValueString str])       -> return (HiValueNumber (fromIntegral (Data.Text.length str)))
    (HiFunLength, [HiValueList list])        -> return (HiValueNumber (fromIntegral (Seq.length list)))
    (HiFunToUpper, [HiValueString str])      -> return (HiValueString (Data.Text.toUpper str))
    (HiFunToLower, [HiValueString str])      -> return (HiValueString (Data.Text.toLower str))
    (HiFunReverse, [HiValueString str])      -> return (HiValueString (Data.Text.reverse str))
    (HiFunReverse, [HiValueList list])       -> return (HiValueList (Seq.reverse list))
    (HiFunTrim, [HiValueString str])         -> return (HiValueString (Data.Text.strip str))
    (HiFunPackBytes, [HiValueList bytes])    -> packBytes bytes
    (HiFunEncodeUtf8, [HiValueString bytes]) -> return (HiValueBytes (encodeUtf8 bytes))
    (HiFunDecodeUtf8, [HiValueBytes bytes])  -> decodeBytes bytes
    (HiFunUnpackBytes, [HiValueBytes bytes]) -> unpackBytes bytes
    (HiFunZip, [HiValueBytes bytes])         -> zipBytes bytes
    (HiFunUnzip, [HiValueBytes bytes])       -> unzipBytes bytes
    (HiFunSerialise, [bytes])                -> serialiseBytes bytes
    (HiFunDeserialise, [HiValueBytes bytes]) -> deserialiseBytes bytes
    _                                        -> throwError HiErrorInvalidArgument

decodeBytes :: Monad m => ByteString -> ExceptT HiError m HiValue
decodeBytes bytes = case decodeUtf8' bytes of
    Right text -> return $ HiValueString text
    Left _     -> return HiValueNull

unpackBytes :: Monad m => ByteString -> m HiValue
unpackBytes bytes = return $ HiValueList $ Seq.fromList $ map (HiValueNumber . fromIntegral) (unpack bytes)

serialiseBytes :: Monad m => HiValue -> m HiValue
serialiseBytes bytes = return $ HiValueBytes $ toStrict (serialise bytes)

deserialiseBytes :: Monad m => ByteString -> m HiValue
deserialiseBytes bytes = return $ either (return HiValueNull) id (deserialiseOrFail (fromStrict bytes))

zipBytes :: Monad m => ByteString -> m HiValue
zipBytes = return . HiValueBytes . toStrict . compressWith defaultCompressParams {compressLevel = bestCompression} . fromStrict

unzipBytes :: Monad m => ByteString -> m HiValue
unzipBytes bytes = return (HiValueBytes $ toStrict $ decompress $ fromStrict bytes)

toByte :: Monad m => Int ->  ExceptT HiError m Word8
toByte i
  | 0 <= i && i <= 255 = pure (fromIntegral i)
  | otherwise = throwError HiErrorInvalidArgument

packBytes ::  Monad m => Seq HiValue -> ExceptT HiError m HiValue
packBytes seqBytes = do
  bytes <- traverse extractByte seqBytes
  return $ HiValueBytes (Data.ByteString.pack (toList bytes))

extractByte :: Monad m => HiValue ->  ExceptT HiError m Word8
extractByte (HiValueNumber byte) =
  case parseInt byte of
    Right b  -> toByte b
    Left err -> throwError err
extractByte _ = throwError HiErrorInvalidArgument

evalFunctionTernars :: Monad m => HiFun -> [HiValue] -> ExceptT HiError m HiValue
evalFunctionTernars fun args = case (fun, args) of
    (HiFunIf, [HiValueBool cond, HiValueFunction a, HiValueFunction b]) ->
        return $ if cond then HiValueFunction a else HiValueFunction b
    (HiFunIf, [HiValueBool cond, a, b]) ->
        return $ if cond then a else b
    _ -> throwError HiErrorInvalidArgument
