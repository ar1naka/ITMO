
module HW5.Parser
  ( parse
  ) where

import Control.Monad.Combinators.Expr (Operator (InfixL, InfixN, InfixR), makeExprParser)
import Data.ByteString (pack)
import Data.Functor (void, ($>))
import Data.Text (pack)
import Data.Void (Void)
import Data.Word (Word8)
import HW5.Base (HiExpr (..), HiFun (..), HiValue (..))
import Text.Megaparsec (MonadParsec (eof), Parsec, between, choice, manyTill, notFollowedBy,
                        runParser, sepBy, sepEndBy, try, (<|>))
import Text.Megaparsec.Char (char, hexDigitChar, space1, string)
import qualified Text.Megaparsec.Char.Lexer as L
import Text.Megaparsec.Error (ParseErrorBundle)

parse :: String -> Either (ParseErrorBundle String Void) HiExpr
parse = runParser (parse' <* eof)  ""

type Parser = Parsec Void String

skip :: Parser ()
skip = L.space space1 (L.skipLineComment "//") (L.skipBlockComment "/*" "*/")

parseNumber :: Parser HiExpr
parseNumber = skip *> (do
  num <- L.signed skip L.scientific
  return $ HiExprValue $ HiValueNumber $ toRational num) <* skip

parseBool :: Parser HiExpr
parseBool = skip *> choice
  [ string "true" $> HiExprValue (HiValueBool True)
  , string "false" $> HiExprValue (HiValueBool False)
  ] <* skip

parseByteParens :: Parser a -> Parser a
parseByteParens = between (skip *> string "[#" <* skip) (string "#]" <* skip)

parseBetween :: Parser open -> Parser close -> Parser a -> Parser a
parseBetween open close = between (skip *> open) (close <* skip)

parsepParens :: Parser a -> Parser a
parsepParens = parseBetween (char '(') (char ')')

parseListParens :: Parser a -> Parser a
parseListParens = parseBetween (char '[') (char ']')

parseValue :: Parser HiExpr
parseValue = choice
  [ parseNumber
  , parseFun
  ,parseBool
  , parseNull
  , parseBytes
  , parseString
  , parseList
  ]

parseBytes :: Parser HiExpr
parseBytes = do
  vals <- parseByteParens $ parseByte `sepEndBy` (char ' ' <* skip)
  return $ HiExprValue $ HiValueBytes (Data.ByteString.pack vals)

parseByte :: Parser Word8
parseByte = do
    a <- hexDigitChar
    b <- hexDigitChar
    return $ read ['0','x', a, b]

parseList :: Parser HiExpr
parseList = do
  vals <- parseListParens $ sepBy (skip *> parse') (char ',')
  return $ HiExprApply (HiExprValue (HiValueFunction HiFunList)) vals

parseNull :: Parser HiExpr
parseNull = skip *> string "null" $> HiExprValue HiValueNull <* skip

parseString :: Parser HiExpr
parseString =  do
  str <- skip *> char '"' *> manyTill L.charLiteral (char '"') <* skip
  return $ HiExprValue (HiValueString (Data.Text.pack str))

parseFun :: Parser HiExpr
parseFun = do
  opName <- choice
    [ string "add" >> return HiFunAdd
    , string "sub" >> return HiFunSub
    , string "mul" >> return HiFunMul
    , string "div" >> return HiFunDiv
    , string "and" >> return HiFunAnd
    , string "or" >> return HiFunOr
    , string "if" >> return HiFunIf
    , string "less-than" >> return HiFunLessThan
    , string "greater-than" >> return HiFunGreaterThan
    , string "equals" >> return HiFunEquals
    , string "not-less-than" >> return HiFunNotLessThan
    , string "not-greater-than" >> return HiFunNotGreaterThan
    , string "not-equals" >> return HiFunNotEquals
    , string "not" >> return HiFunNot
    , string "length" >> return HiFunLength
    , string "to-upper" >> return HiFunToUpper
    , string "to-lower" >> return HiFunToLower
    , string "reverse" >> return HiFunReverse
    , string "trim" >> return HiFunTrim
    , string "list" >> return HiFunList
    , string "range" >> return HiFunRange
    , string "fold" >> return HiFunFold
    , string "pack-bytes" >> return HiFunPackBytes
    , string "unpack-bytes" >> return HiFunUnpackBytes
    , string "encode-utf8" >> return HiFunEncodeUtf8
    , string "decode-utf8" >> return HiFunDecodeUtf8
    , string "zip" >> return HiFunZip
    , string "unzip" >> return HiFunUnzip
    , string "serialise" >> return HiFunSerialise
    , string "deserialise" >> return HiFunDeserialise
    ]
  return $ HiExprValue (HiValueFunction opName)

parseOperation :: Parser HiExpr
parseOperation = do
  op <- parseValue
  HiExprApply op <$> parseArgs

parseArgs :: Parser [HiExpr]
parseArgs = parsepParens $ sepBy (skip *> parse') (char ',')

parseSimple :: Parser HiExpr
parseSimple = try parseOperation <|> try parseValue

parseNext :: HiExpr -> Parser HiExpr
parseNext s = (parsepParens (sepBy parse' (skip *> char ',')) >>= \args -> parseNext (HiExprApply s args)) <|> return s

parse' :: Parser HiExpr
parse' = do
  s <- parseOperators <|> parseSimple
  parseNext s

parseOperators :: Parser HiExpr
parseOperators = makeExprParser (parseSimple <|> parsepParens parseOperators) operatorTable

wrap :: HiFun -> HiExpr -> HiExpr -> HiExpr
wrap f x y = HiExprApply (HiExprValue (HiValueFunction f)) [x, y]

parseOperator :: String -> Parser ()
parseOperator op = void (skip *> (L.lexeme skip . try) (string op <* notFollowedBy (string "=") <* skip))

parseBin :: String -> HiFun -> Operator Parser HiExpr
parseBin op hiFun = InfixL (wrap hiFun <$ parseOperator op)

parseBinR :: String -> HiFun -> Operator Parser HiExpr
parseBinR op hiFun = InfixR (wrap hiFun <$ parseOperator op)

parseBinN :: String -> HiFun -> Operator Parser HiExpr
parseBinN op hiFun = InfixN (wrap hiFun <$ parseOperator op)

operatorTable :: [[Operator Parser HiExpr]]
operatorTable = [
  [ parseBin "*" HiFunMul
  , parseBin "/" HiFunDiv
  ],
  [ parseBin "+" HiFunAdd
  , parseBin "-" HiFunSub
  ],
  [ parseBinN "<" HiFunLessThan
  , parseBinN ">" HiFunGreaterThan
  , parseBinN "==" HiFunEquals
  , parseBinN ">=" HiFunNotLessThan
  , parseBinN "<=" HiFunNotGreaterThan
  , parseBinN "/=" HiFunNotEquals
  ],
  [ parseBinR "&&" HiFunAnd],
  [ parseBinR "||" HiFunOr ]
  ]
