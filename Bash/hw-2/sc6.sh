#!/bin/bash

all=$(ps -A -o pid)
for pid in $all; do
  if ! [[ -d "/proc/$pid" ]]; then
    continue
  fi 
  mem=$(grep -s "VmSize:" "/proc/$pid/status" | awk '{print $2}')
  if [[ "$tmp_mem" -lt "$mem" ]]
  then
   tmp_mem=$mem
   tmp_pid=$pid
  fi
  done
echo "my_res:$tmp_pid memory:$tmp_mem"

