#!/bin/bash
all=$(ps -A -o pid )
for pid in $all; do
  if ! [[ -d "/proc/$pid" ]]; then
    continue
  fi  
  ppid=$(grep -s "PPid:" "/proc/$pid/status" | awk '{print $2}')
  sum_exec_runtime=$(grep -s "se.sum_exec_runtime" "/proc/$pid/sched" | awk '{print $3}')
  nr_switches=$(grep -s "nr_switches" "/proc/$pid/sched" | awk '{print $3}')
  art=$(echo "$sum_exec_runtime $nr_switches" | awk '{if ($1 ~ /[0-9]+/ && $2) print $1/$2}')
  if ! [[ -z $art ]]; then  
     echo "ProcessID=$pid : Parent_ProcessID=$ppid : Average_Running_Time=$art"
  fi   
done | sort -n -t '=' -k3 > res4

