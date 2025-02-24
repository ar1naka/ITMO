{-# LANGUAGE TypeOperators #-}

module HW0.T1
  ( type (<->) (Iso)
  , assocEither
  , assocPair
  , distrib
  , flipIso
  , runIso
  ) where

data a <-> b = Iso (a -> b) (b -> a)

distrib :: Either a (b, c) -> (Either a b, Either a c)
distrib (Left a)       = (Left a, Left a)
distrib (Right (b, c)) = (Right b, Right c)

flipIso :: (a <-> b) -> (b <-> a)
flipIso (Iso f g) = Iso g f

runIso :: (a <-> b) -> (a -> b)
runIso (Iso f _) = f

assocPair :: (a, (b, c)) <-> ((a, b), c)
assocPair = let
              f (a, (b, c)) = ((a, b), c)
              g ((a, b), c) = (a, (b, c))
            in Iso f g

assocEither :: Either a (Either b c) <-> Either (Either a b) c
assocEither = let
                f (Left a)          = Left (Left a)
                f (Right (Left b))  = Left (Right b)
                f (Right (Right c)) = Right c
                g (Left (Left a))   = Left a
                g (Left (Right b))  = Right (Left b)
                g (Right c)         = Right (Right c)
              in Iso f g
