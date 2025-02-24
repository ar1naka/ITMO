module HW2.T2
  ( joinWith
  , splitOn
  ) where

import Data.List.NonEmpty as NonEmpty

-- You may add necessary constraints here
splitOn :: Eq a => a -> [a] -> NonEmpty [a]
splitOn _ [] = [] :| []
splitOn sep (cur : xs) =
  let (head' :| tail') = splitOn sep xs
  in if cur == sep
    then [] :| (head' : tail')
    else (cur : head') :| tail'

joinWith :: a -> NonEmpty [a] -> [a]
joinWith sep (x :| xs) = x ++ concatMap (sep :) xs
