module HW3.T4
  ( State (..)
  , Prim (..)
  , Expr (..)
  , mapState
  , wrapState
  , joinState
  , modifyState
  , eval
  ) where

import qualified Control.Monad
import HW3.T1

newtype State s a = S { runS :: s -> Annotated s a }

mapState :: (a -> b) -> State s a -> State s b
mapState f x = S (mapAnnotated f . runS x)

wrapState :: a -> State s a
wrapState x = S (x :#)

joinState :: State s (State s a) -> State s a
joinState x = S(\s -> let y :# ys = runS x s in runS y ys)

modifyState :: (s -> s) -> State s ()
modifyState f = S (\s -> () :# f s)

instance Functor (State s) where
  fmap = mapState

instance Applicative (State s) where
  pure = wrapState
  (<*>) = Control.Monad.ap

instance Monad (State s) where
  (>>=) x f = joinState (mapState f x)

data Prim a =
    Add a a
  | Sub a a
  | Mul a a
  | Div a a
  | Abs a
  | Sgn a
  deriving Show

data Expr = Val Double | Op (Prim Expr)
  deriving Show

instance Num Expr where
  (+) a b = Op (Add a b)
  (-) a b = Op (Sub a b)
  (*) a b = Op (Mul a b)
  abs a = Op (Abs a)
  signum a = Op (Sgn a)
  fromInteger a = Val (fromInteger a)

instance Fractional Expr where
  (/) a b = Op (Div a b)
  fromRational a = Val (fromRational a)

modifyEval :: a -> State [a] ()
modifyEval = modifyState . (:)

eval2 :: (Double -> Double -> Prim Double) -> Expr -> Expr -> (Double -> Double -> Double) -> State [Prim Double] Double
eval2 e a b f = do
  x <- eval a
  y <- eval b
  modifyEval (e x y)
  return $ f x y

eval1 :: (Double -> Prim Double) -> Expr -> (Double -> Double) -> State [Prim Double] Double
eval1 e a f = do
  x <- eval a
  modifyEval (e x)
  return $ f x

eval :: Expr -> State [Prim Double] Double
eval (Op (Add a b)) = eval2 Add a b (+)
eval (Op (Sub a b)) = eval2 Sub a b (-)
eval (Op (Mul a b)) = eval2 Mul a b (*)
eval (Op (Abs a))   = eval1 Abs a abs
eval (Op (Sgn a))   = eval1 Sgn a signum
eval (Op (Div a b)) = eval2 Div a b (/)
eval (Val a)        = pure a
