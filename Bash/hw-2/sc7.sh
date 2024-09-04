#!/bin/bash

all=$(ps -ax -o pid --no-headers )
for line in $all; do 
     pid=`echo $line | awk '{print $1}'`
     bytes=$(grep -Es "read_bytes" /proc/$pid/io | awk '{print $2}')
     echo "$pid,$bytes"
done > tmp

sleep 5s

for LINE in `cat tmp`
do
   pid=`echo $LINE | awk -F ',' '{print $1}'`
   bytes1=`echo $LINE | awk -F ',' '{print $2}'`
   cmd1=$(grep -s $ /proc/$pid/comm)
   bytes2=$(grep -Es "read_bytes" /proc/$pid/io | awk '{print $2}') 
   res=`echo "$bytes2" "$bytes1" | awk '{print $1 - $2}'`
   if [[ res -lt 0 ]]; then 
      res=0
   fi   
   echo $pid : $cmd1 : $res
done | sort -n -k3 | tail -3