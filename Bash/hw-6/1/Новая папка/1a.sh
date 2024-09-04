#!/bin/bash

n=$1
for ((i=0; i<$n; i++));do
    ./1.sh $i & 
done
wait
