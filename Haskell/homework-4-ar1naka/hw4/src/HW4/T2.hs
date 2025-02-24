{-# LANGUAGE DerivingStrategies         #-}
{-# LANGUAGE GeneralisedNewtypeDeriving #-}

module HW4.T2
  ( ParseError (..)
  , runP
  , pChar
  , parseError
  , parseExpr
  ) where

import Control.Applicative
import Control.Monad
import Data.Char
import Data.Functor (($>))
import Numeric.Natural (Natural)

import HW4.T1 (ExceptState (..))
import HW4.Types

data ParseError = ErrorAtPos Natural
  deriving Show

newtype Parser a = P (ExceptState ParseError (Natural, String) a)
  deriving newtype (Functor, Applicative, Monad)

runP :: Parser a -> String -> Except ParseError a
runP (P parser) str = case runES parser (0, str) of
  Success (a :# _) -> Success a
  Error e          -> Error e

-- Just an example of parser that may be useful
-- in the implementation of 'parseExpr'
pChar :: Parser Char
pChar = P $ ES $ \(pos, s) ->
  case s of
    []     -> Error (ErrorAtPos pos)
    (c:cs) -> Success (c :# (pos + 1, cs))

parseError :: Parser a
parseError = P $ ES $ \(pos, _)  -> Error (ErrorAtPos pos)

instance Alternative Parser where
  empty = parseError
  (<|>) (P x) (P y) = P $ ES $ \q ->
    case runES x q of
      Success a -> Success a
      Error _   -> runES y q

-- No metohds
instance MonadPlus Parser

pEof :: Parser ()
pEof =  P $ ES $ \x@(pos, s) ->
    case s of
      [] -> Success (() :# x)
      _  -> Error(ErrorAtPos pos)

pChar1 :: Char -> Parser Char
pChar1 ch = mfilter (== ch) pChar

skipSpaces :: Parser String
skipSpaces = many (mfilter isSpace pChar)

parseNum :: Parser Expr
parseNum = parseDouble <|> parseInt

parseDigits :: Parser String
parseDigits = some (mfilter isDigit pChar)

toNumber :: String -> Integer
toNumber = foldl (\acc x -> acc * 10 + toInteger (digitToInt x)) 0

parseInt :: Parser Expr
parseInt =  do Val . (fromIntegral . toNumber) <$> parseDigits

parseDouble :: Parser Expr
parseDouble = do
  intPart <- parseDigits
  _ <- pChar1 '.'
  fracPart <- parseDigits
  pure (Val $ fromRational $ fromIntegral (toNumber (intPart ++ fracPart)) / (10 ^ length fracPart))

parseExpr :: String -> Except ParseError Expr
parseExpr = runP parseTmp

parseTmp :: Parser Expr
parseTmp = do
  _ <- skipSpaces
  expr <- parse2
  _ <- skipSpaces
  pEof
  pure expr

parse0 :: Parser Expr
parse0 = parseNum <|> do
    _ <- pChar1 '('
    _ <- skipSpaces
    expr <- parse2
    _ <- skipSpaces
    _ <- pChar1 ')'
    pure expr

parseAdd :: Parser (Expr -> Expr -> Expr)
parseAdd = parseOperator (+) '+'

parseSub :: Parser (Expr -> Expr -> Expr)
parseSub = parseOperator (-) '-'

parseMul :: Parser (Expr -> Expr -> Expr)
parseMul = parseOperator (*) '*'

parseDiv :: Parser (Expr -> Expr -> Expr)
parseDiv = parseOperator (/) '/'

parse2 :: Parser Expr
parse2 = parseOperation (parseAdd <|> parseSub) parse1

parse1 :: Parser Expr
parse1 = parseOperation (parseMul<|> parseDiv) parse0

parseOperation :: Parser (Expr -> Expr -> Expr) -> Parser Expr -> Parser Expr
parseOperation pOs expr = do
    x <- expr
    parseNext x
  where
    parseNext :: Expr -> Parser Expr
    parseNext x' =
        (do
            _ <- skipSpaces
            o <- pOs
            _ <- skipSpaces
            y <- expr
            parseNext (o x' y))
        <|> pure x'

parseOperator :: (Expr -> Expr -> Expr) -> Char -> Parser (Expr -> Expr -> Expr)
parseOperator op ch = pChar1 ch $> op
