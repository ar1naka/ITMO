{-# LANGUAGE BlockArguments #-}
module HW3.T3
  ( joinOption
  , joinExcept
  , joinAnnotated
  , joinList
  , joinFun
  ) where

import HW3.T1

joinOption :: Option (Option a) -> Option a
joinOption (Some a) = a
joinOption _        = None

joinExcept :: Except e (Except e a) -> Except e a
joinExcept (Success a) = a
joinExcept (Error e)   = Error e

-- You may add necessary constraints here
joinAnnotated :: Semigroup e => Annotated e (Annotated e a) -> Annotated e a
joinAnnotated ((a :# b) :# c) = a :# c <> b

joinList :: List (List a) -> List a
joinList Nil = Nil
joinList (x :. xs) =
  let f Nil         = joinList xs
      f (x' :. xs') = x' :. f xs'
  in f x

joinFun :: Fun i (Fun i a) -> Fun i a
joinFun (F f) = F (\i -> let F g = f i in g i)
