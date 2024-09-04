#!/bin/bash

man bash | grep -o -E "[a-z]{4,}" | sort | uniq -c | sort -nr | sed 3q 

