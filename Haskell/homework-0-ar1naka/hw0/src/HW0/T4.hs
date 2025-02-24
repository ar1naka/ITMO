module HW0.T4
  ( fac
  , fib
  , map'
  , repeat'
  ) where

import Data.Function (fix)
import Numeric.Natural (Natural)

repeat' :: a -> [a]
repeat' x = fix (x:)

map' :: (a -> b) -> [a] -> [b]
map' f = fix (\rec xs' ->
    case xs' of
        []     -> []
        (x:ys) -> f x : rec ys)

fib :: Natural -> Natural
fib = fix (\rec x y i ->
               if i == 0
               then x
               else rec y (x + y) (i - 1)) 0 1

fac :: Natural -> Natural
fac = fix (\rec n ->
             if n <= 1
             then 1
             else n * rec (n - 1))
