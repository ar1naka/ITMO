#!/bin/bash

cnt=0
array=()

while true; do
   cnt=$(($cnt + 1))
   for x in {1..10}; do
       array+=($x)
   done 

   if [[ $cnt -eq 100000 ]]; then
      echo ${#array[@]} >> report2.log
      cnt=0
   fi
done