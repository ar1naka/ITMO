module HW2.T3
  ( epart
  , mcat
  ) where

-- You may add necessary constraints here
mcat :: Monoid a => [Maybe a] -> a
mcat = foldMap f where
    f (Just x) = x
    f Nothing  = mempty

-- You may add necessary constraints here
epart :: (Monoid a, Monoid b) => [Either a b] -> (a, b)
epart = foldMap f where
    f x = case x of
      Left y  -> (y, mempty)
      Right y -> (mempty, y)
