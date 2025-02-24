-- | This module defines 'Grid' datatype.
-- Feel free to define additional helper functions to work
-- with this datatype in this module.
module Data.Grid
  ( Grid (..)
  ) where

import Control.Comonad (Comonad (..))

import Data.ListZipper (ListZipper (..))

newtype Grid a = Grid { unGrid :: ListZipper (ListZipper a) }

instance Functor Grid where
  fmap = undefined

instance Comonad Grid where
  extract = undefined

  extend = undefined
