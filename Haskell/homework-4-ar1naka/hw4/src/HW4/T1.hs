module HW4.T1
  ( EvaluationError (..)
  , ExceptState (..)
  , mapExceptState
  , wrapExceptState
  , joinExceptState
  , modifyExceptState
  , throwExceptState
  , eval
  ) where

import qualified Control.Monad (ap)
import HW4.Types

data ExceptState e s a = ES { runES :: s -> Except e (Annotated s a) }

mapAnnotated :: (a -> b) -> (Annotated e a -> Annotated e b)
mapAnnotated f (a :# e) = f a :# e

mapExcept :: (a -> b) -> (Except e a -> Except e b)
mapExcept _ (Error e)   = Error e
mapExcept f (Success a) = Success (f a)

mapExceptState :: (a -> b) -> ExceptState e s a -> ExceptState e s b
mapExceptState f x = ES (mapExcept (mapAnnotated f) . runES x)

wrapExceptState :: a -> ExceptState e s a
wrapExceptState x = ES (\s -> Success (x :# s))

joinExceptState :: ExceptState e s (ExceptState e s a) -> ExceptState e s a
joinExceptState es = ES (\s ->
  case runES es s of
    Success (f :# y) -> runES f y
    Error e          -> Error e)

modifyExceptState :: (s -> s) -> ExceptState e s ()
modifyExceptState f = ES (\s -> Success (() :# f s))

throwExceptState :: e -> ExceptState e s a
throwExceptState e = ES (\_ -> Error e)

instance Functor (ExceptState e s) where
  fmap = mapExceptState

instance Applicative (ExceptState e s) where
  pure = wrapExceptState
  (<*>) = Control.Monad.ap

instance Monad (ExceptState e s) where
  (>>=) x f = joinExceptState (mapExceptState f x)

data EvaluationError = DivideByZero
  deriving Show

modifyEval :: a -> ExceptState e [a] ()
modifyEval = modifyExceptState . (:)

eval2 :: (Double -> Double -> Prim Double) -> Expr -> Expr -> (Double -> Double -> Double) -> ExceptState EvaluationError [Prim Double] Double
eval2 e a b f = do
  x <- eval a
  y <- eval b
  modifyEval (e x y)
  return $ f x y

eval1 :: (Double -> Prim Double) -> Expr -> (Double -> Double) -> ExceptState EvaluationError [Prim Double] Double
eval1 e a f = do
  x <- eval a
  modifyEval (e x)
  return $ f x

eval :: Expr -> ExceptState EvaluationError [Prim Double] Double
eval (Op (Add a b)) = eval2 Add a b (+)
eval (Op (Sub a b)) = eval2 Sub a b (-)
eval (Op (Mul a b)) = eval2 Mul a b (*)
eval (Op (Abs a))   = eval1 Abs a abs
eval (Op (Sgn a))   = eval1 Sgn a signum
eval (Op (Div a b)) = do
  x <- eval a
  y <- eval b
  modifyExceptState (Div x y :)
  if y == 0
    then throwExceptState DivideByZero
    else return (x / y)
eval (Val a)        = pure a
