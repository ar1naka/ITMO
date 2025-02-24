{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}

module HW6.T1
  ( BucketsArray
  , CHT (..)

  , newCHT
  , getCHT
  , putCHT
  , sizeCHT

  , initCapacity
  , loadFactor
  ) where

import Control.Concurrent.Classy (MonadConc, MonadSTM (writeTVar), STM, atomically)
import Control.Concurrent.Classy.STM (TArray, TVar, newTVar, readTVar, readTVarConc)
import Control.Monad (when)
import Data.Array.Base (MArray (getNumElements))
import Data.Array.MArray (getElems, newArray, readArray, writeArray)
import Data.Hashable (Hashable, hash)

initCapacity :: Int
initCapacity = 16

loadFactor :: Double
loadFactor = 0.75

type Bucket k v = [(k, v)]
type BucketsArray stm k v = TArray stm Int (Bucket k v)

data CHT stm k v = CHT
  { chtBuckets :: TVar stm (BucketsArray stm k v)
  , chtSize    :: TVar stm Int
  }

newCHT :: MonadConc m => m (CHT (STM m) k v)
newCHT = atomically $ do
  buckets <- createArray initCapacity
  bucketsVar <- newTVar buckets
  size <- newTVar 0
  return $ CHT
    { chtBuckets = bucketsVar
    , chtSize    = size
    }

getCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> CHT (STM m) k v
  -> m (Maybe v)
getCHT key cht = atomically $ do
    (bucket, _, _) <- readBucket key cht
    return (lookup key bucket)

readBucket
  :: ( MonadSTM m
     , Hashable k
     )
  => k
  -> CHT m k v
  -> m (Bucket k v, TArray m Int (Bucket k v), Int)
readBucket key cht =  do
    (buckets, capacity) <- bucketsWithCapacity cht
    let index = getHash key capacity
    bucket <- readArray buckets index
    return (bucket, buckets, index)

bucketsWithCapacity
  :: ( MonadSTM m)
  => CHT m k v
  -> m (TArray m Int (Bucket k v), Int)
bucketsWithCapacity cht = do
  buckets  <- readTVar (chtBuckets cht)
  capacity <- getNumElements buckets
  return (buckets, capacity)

putCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> v
  -> CHT (STM m) k v
  -> m ()
putCHT key val cht = atomically $ do
  checkNewPair <- do
    (bucket, buckets, index) <- readBucket key cht
    case checkKey key bucket of
      [] -> addKey buckets index bucket cht key val
      _  -> updateKey buckets index bucket key val

  when checkNewPair $ do
    size     <- do
      let size' = chtSize cht
      readTVar size'
    capacity <- do
      (_, cap) <- bucketsWithCapacity cht
      pure cap

    when (fromIntegral size >= fromIntegral capacity * loadFactor) $
      resize cht

sizeCHT :: MonadConc m => CHT (STM m) k v -> m Int
sizeCHT cht = readTVarConc (chtSize cht)

updateKey
  :: (MonadSTM m, Eq k)
  => TArray m Int [(k, v)]
  -> Int -> [(k, v)]
  -> k
  -> v
  -> m Bool
updateKey buckets index bucket key val = do
  let bucket' = (key, val) : findByKey key bucket
  writeArray buckets index bucket'
  return False

addKey
  :: MonadSTM m
  => TArray m Int [(k, v)]
  -> Int
  -> [(k, v)]
  -> CHT m k v
  -> k
  -> v
  -> m Bool
addKey buckets index bucket cht key val = do
  let bucket' = (key, val) : bucket
  writeArray buckets index bucket'
  size <- readTVar (chtSize cht)
  writeTVar (chtSize cht) (size + 1)
  return True

getHash :: Hashable a => a -> Int -> Int
getHash key cap = hash key `mod` cap

findByKey :: Eq k => k -> [(k, v)] -> [(k, v)]
findByKey key = filter ((/= key) . fst)

checkKey :: Eq k => k -> [(k, v)] -> [(k, v)]
checkKey key = filter (\(k, _) -> k == key)

createArray :: MonadSTM stm => Int -> stm (BucketsArray stm k v)
createArray cap = newArray (0, cap - 1) []

resize
  :: ( MonadSTM m
     , Hashable k
     )
  => CHT m k v
  -> m ()
resize cht = do
  (buckets', capacity') <- bucketsWithCapacity cht
  let capacity = capacity' * 2
  buckets  <- createArray capacity
  tmp      <- getElems buckets'

  mapM_( mapM_ $ \(key, val) -> do
        let index = getHash key capacity
        bucket <- readArray buckets index
        writeArray buckets index ((key, val) : bucket)) tmp
  writeTVar (chtBuckets cht) buckets
