module HW1.T3
  ( Tree (..)
  , tsize
  , tdepth
  , tmember
  , tinsert
  , tFromList
  ) where

data Meta = M Int Int deriving (Show)

data Tree a = Leaf | Branch Meta (Tree a) a (Tree a)
  deriving (Show)

tsize :: Tree a -> Int
tsize Leaf                      = 0
tsize (Branch (M size _) _ _ _) = size

tdepth :: Tree a -> Int
tdepth Leaf                       = 0
tdepth (Branch (M _ depth) _ _ _) = depth

tmember :: Ord a => a -> Tree a -> Bool
tmember _ Leaf = False
tmember x (Branch _ l y r)
  | x == y = True
  | x < y = tmember x l
  | otherwise = tmember x r

tinsert :: Ord a => a -> Tree a -> Tree a
tinsert x Leaf = Branch (M 1 1) Leaf x Leaf
tinsert x (Branch (M m1 m2) l y r)
  | x == y    = Branch (M m1 m2) l y r
  | x < y     = let left = tinsert x l
                in Branch (M (tsize left + tsize r + 1) (max (tdepth left) (tdepth r) + 1)) left y r
  | otherwise = let right = tinsert x r
                in Branch (M (tsize right + tsize l + 1) (max (tdepth l) (tdepth right) + 1)) l y right


tFromList ::  Ord a => [a] -> Tree a
tFromList [] = Leaf
tFromList xs = foldr tinsert Leaf xs
