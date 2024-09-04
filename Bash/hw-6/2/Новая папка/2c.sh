#!/bin/bash

for ((N=1; N<=20; N++)); do
    echo "N = $N" >> 4.log
    ./create.sh $N
    for ((i=0; i<10; i++)); do
        d=$( { time -p ./2_1_a.sh $N ;} 2>&1 | awk '/real/ {print $2}' )
        echo $d >> 4.log
    done
done

