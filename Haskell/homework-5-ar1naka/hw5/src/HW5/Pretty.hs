module HW5.Pretty
  ( prettyValue
  ) where

import Data.ByteString (ByteString, unpack)
import Data.Foldable (Foldable (toList))
import Data.List (intercalate, intersperse)
import Data.Ratio (denominator, numerator)
import Data.Scientific (fromRationalRepetendUnlimited)
import Data.Sequence (Seq)
import Data.Word ()
import HW5.Base (HiFun (..), HiValue (..))
import Prettyprinter (Doc, pretty)
import Prettyprinter.Render.Terminal (AnsiStyle)
import Text.Printf (printf)

prettyValue :: HiValue -> Doc AnsiStyle
prettyValue (HiValueNumber r)    = prettyNumber r
prettyValue (HiValueBool b)      = prettyBool b
prettyValue (HiValueFunction f)  = prettyFun f
prettyValue HiValueNull          = pretty "null"
prettyValue (HiValueString str)  = pretty $ show str
prettyValue (HiValueList list)   = prettyList list
prettyValue (HiValueBytes bytes) = prettyBytes bytes

prettyNumber :: Rational -> Doc AnsiStyle
prettyNumber n
  | denominator n == 1 = pretty (numerator n)
  | isLimited n = pretty (fromRational n :: Double)
  | abs numer >= denominator n =
      let
        (whole, frac) = numer `quotRem` denominator n
        sign = if numerator n < 0 then "-" else "+"
      in
        if frac == 0
          then pretty (show whole)
          else pretty (show whole)
          <> pretty sign
          <> pretty (abs frac)
          <> printFrac n
  | otherwise =
      pretty (numerator n) <> printFrac n
  where
    numer = numerator n

isLimited :: Rational -> Bool
isLimited r =
  case fromRationalRepetendUnlimited r of
    (_, Nothing) -> True
    _            -> False

printFrac :: Rational -> Doc ann
printFrac n = pretty "/" <> pretty (denominator n)

prettyBool :: Bool -> Doc AnsiStyle
prettyBool bool =
  if bool then pretty "true" else pretty "false"

prettyFun :: HiFun -> Doc AnsiStyle
prettyFun HiFunAdd            = pretty "add"
prettyFun HiFunSub            = pretty "sub"
prettyFun HiFunMul            = pretty "mul"
prettyFun HiFunDiv            = pretty "div"
prettyFun HiFunLength         = pretty "length"
prettyFun HiFunToUpper        = pretty "to-upper"
prettyFun HiFunToLower        = pretty "to-lower"
prettyFun HiFunReverse        = pretty "reverse"
prettyFun HiFunTrim           = pretty "trim"
prettyFun HiFunOr             = pretty "or"
prettyFun HiFunNot            = pretty "not"
prettyFun HiFunIf             = pretty "if"
prettyFun HiFunAnd            = pretty "and"
prettyFun HiFunLessThan       = pretty "less-than"
prettyFun HiFunGreaterThan    = pretty "greater-than"
prettyFun HiFunEquals         = pretty "equals"
prettyFun HiFunNotLessThan    = pretty "not-less-than"
prettyFun HiFunNotGreaterThan = pretty "not-greater-than"
prettyFun HiFunNotEquals      = pretty "not-equals"
prettyFun HiFunList           = pretty "list"
prettyFun HiFunRange          = pretty "range"
prettyFun HiFunFold           = pretty "fold"
prettyFun HiFunPackBytes      = pretty "pack-bytes"
prettyFun HiFunUnpackBytes    = pretty "unpack-bytes"
prettyFun HiFunEncodeUtf8     = pretty "encode-utf8"
prettyFun HiFunDecodeUtf8     = pretty "decode-utf8"
prettyFun HiFunZip            = pretty "zip"
prettyFun HiFunUnzip          = pretty "unzip"
prettyFun HiFunSerialise      = pretty "serialise"
prettyFun HiFunDeserialise    = pretty "deserialise"

prettyList :: Seq HiValue -> Doc AnsiStyle
prettyList values = pretty "["
      <> mconcat (intersperse (pretty ", " ) (map prettyValue (toList values)))
      <> pretty "]"

prettyBytes :: ByteString -> Doc AnsiStyle
prettyBytes b =
    pretty "[#"
    <> pretty (intercalate " " (map (printf "%02x") (Data.ByteString.unpack b)))
    <> pretty "#]"
