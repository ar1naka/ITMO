#!/bin/bash

n=$1
for ((i=1; i<=$n; i++));do
    ./main.sh $i
done
