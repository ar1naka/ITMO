#!/bin/bash

sum=0
x="$(bc <<< "scale=20; 1/$1")"
pow=1
for ((i=1; i<=2000; i++));
do
    tmp="$(bc <<< "scale=20; $i * $pow")"
    sum="$(bc <<< "scale=20; $sum + $tmp")"
    pow="$(bc <<< "scale=20; $x * $pow")"
done
echo $sum
