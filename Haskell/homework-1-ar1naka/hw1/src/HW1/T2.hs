module HW1.T2
  ( N (..)
  , nplus
  , nmult
  , nsub
  , nFromNatural
  , nToNum
  , ncmp
  , nEven
  , nOdd
  , ndiv
  , nmod
  ) where

import Numeric.Natural

data N = Z | S N

nplus :: N -> N -> N
nplus Z b     = b
nplus (S a) b = S (nplus a b)

nmult :: N -> N -> N
nmult Z _     = Z
nmult _ Z     = Z
nmult (S a) b = nplus b (nmult a b)

nsub :: N -> N -> Maybe N
nsub a Z         = Just a
nsub (S a) (S b) = nsub a b
nsub _ _         = Nothing

ncmp :: N -> N -> Ordering
ncmp Z Z         = EQ
ncmp Z (S _)     = LT
ncmp (S _) Z     = GT
ncmp (S a) (S b) = ncmp a b

nFromNatural :: Natural -> N
nFromNatural 0 = Z
nFromNatural a = S (nFromNatural (a - 1))

nToNum :: Num a => N -> a
nToNum Z     = 0
nToNum (S a) = 1 + nToNum a

nEven :: N -> Bool
nEven = undefined

nOdd :: N -> Bool
nOdd = undefined

ndiv :: N -> N -> N
ndiv = undefined

nmod :: N -> N -> N
nmod = undefined
