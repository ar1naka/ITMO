#!/bin/bash

k=30
for (( i=0; i<k; i++ )); do
  ./newmem.sh 6750000 &
  sleep 1
done
