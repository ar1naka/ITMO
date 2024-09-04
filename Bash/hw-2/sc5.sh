#!/bin/bash

current=$(head -n 1 res4 | cut -d: -f2 | cut -d= -f2)
sum=$(head -n 1 res4 | cut -d: -f3 | cut -d= -f2)
cnt=1
while read -r line; do
  new=$(echo $line | cut -d: -f2 | cut -d= -f2)
  tmp=$(echo $line | cut -d: -f3 | cut -d= -f2)
  if [[ $current != $new ]]; then
    avg=$(awk "BEGIN{printf \"%.2f\", $sum / $cnt}")
    echo "Average_Running_Children_of_ParentId=$current is $avg" >> res5
    current=$new
    sum=$tmp
    cnt=1
  else
    sum=$(awk "BEGIN{printf \"%.2f\", $sum + $tmp}")
    cnt=$(( cnt + 1 ))
  fi
  echo "$line" >> res5
  avg=$(awk "BEGIN{printf \"%.2f\", $sum / $cnt}")
  echo "Average_Running_Children_of_ParentId=$current is $avg" >> res5
done < res4


