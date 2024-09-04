#!/bin/bash

for ((N=1; N<=20; N++)); do
    echo "N = $N" >> 3.log
    for ((i=0; i<10; i++)); do
        d=$( { time -p ./1b.sh $N ;} 2>&1 | awk '/real/ {print $2}' )
        echo $d >> 3.log
    done
done
