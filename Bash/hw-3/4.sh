#!/bin/bash

./multiplication.sh&pid1=$!
./multiplication.sh&pid2=$!
./multiplication.sh&pid3=$!

cpulimit -l 10 -p $pid1 -b
kill $pid3
