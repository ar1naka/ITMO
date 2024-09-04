#!/bin/bash

res=""
while [ "$a" != "q" ]
do
    read a
    if [[ "$a" == "q" ]];then 
    	break
    fi
    res+="$a"
done
echo $res