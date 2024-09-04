#!/bin/bash

N=$1
for ((j=1; j<=80000; j++)); do
   read value 
   new_value=$(($value * 2))  
   echo $new_value >> "file_${N}.txt"  
done < "file_${N}.txt" 

