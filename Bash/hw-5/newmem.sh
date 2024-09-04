#!/bin/bash

cnt=0
array=()

while [[ $cnt -le $1 ]]; do
   cnt=$(($cnt + 10))
   for x in {1..10}; do
       array+=($x)
   done 
done