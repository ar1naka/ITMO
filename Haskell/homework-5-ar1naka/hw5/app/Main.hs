module Main (main) where

import Control.Monad.IO.Class (liftIO)
import HW5.Evaluator (eval)
import HW5.Parser (parse)
import HW5.Pretty (prettyValue)
import Prettyprinter.Render.Terminal (putDoc)
import System.Console.Haskeline (InputT, defaultSettings, getInputLine, outputStrLn, runInputT)
import Text.Megaparsec (errorBundlePretty)

main :: IO ()
main = runInputT defaultSettings loop
   where
       loop :: InputT IO ()
       loop = do
           minput <- getInputLine "hi> "
           case minput of
               Nothing -> return ()
               Just "quit" -> return ()
               Just input -> do
                   let p = parse input
                   case p of
                       Left err -> outputStrLn (errorBundlePretty err)
                       Right expr -> do
                           x <- eval expr
                           case x of
                               Left _ -> outputStrLn "Eval error"
                               Right res ->  do
                                liftIO $ putDoc $ prettyValue res
                                liftIO $ putStrLn ""
                   loop
