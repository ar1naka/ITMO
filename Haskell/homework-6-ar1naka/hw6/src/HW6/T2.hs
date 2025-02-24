{-# LANGUAGE DataKinds            #-}
{-# LANGUAGE KindSignatures       #-}
{-# LANGUAGE TypeFamilies         #-}
{-# LANGUAGE TypeOperators        #-}
{-# LANGUAGE UndecidableInstances #-}

module HW6.T2
  ( TSet

  , Contains
  , Add
  , Delete
  ) where

import GHC.TypeLits

type TSet = [Symbol]

type family Contains (name :: Symbol) (set :: TSet) :: Bool where
  Contains _    '[]           = 'False
  Contains h (h ': rest) = 'True
  Contains h (_    ': rest) = Contains h rest

type family Delete (name :: Symbol) (set :: TSet) :: TSet where
  Delete _    '[]           = '[]
  Delete h (h ': rest) = rest
  Delete h (x    ': rest) = x ': Delete h rest

type family Add (v :: Symbol) (set :: TSet) :: TSet where
  Add v set = v ': Delete v set

