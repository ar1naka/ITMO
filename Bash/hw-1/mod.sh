#!/bin/bash

awk -F ", " -v vuz=$1 -v min=$((2**63-1)) '(vuz==$2 && $3<min) {min=$3} END {print min}' students.csv