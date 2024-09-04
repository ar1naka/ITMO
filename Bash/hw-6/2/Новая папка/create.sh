#!/bin/bash

N=$1
for ((i=1; i<=$N; i++)); do
  seq 1 90000 >> "file_${i}.txt"
done

