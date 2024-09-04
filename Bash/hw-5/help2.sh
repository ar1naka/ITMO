#!/bin/bash

./mem.sh &
./mem2.sh &

interval=5
while pgrep -f "mem.sh" > /dev/null || pgrep -f "mem2.sh" > /dev/null; do
    script_pid=$(pgrep -o "mem.sh")  
    script_pid2=$(pgrep -o "mem2.sh")  
    top -b -n1 | awk 'NR==4' >> info_mem.log
    top -b -n1 | awk 'NR==5' >> info_swap.log
    top -b -n1 -p $script_pid | tail -n +8 >> process.log 
    top -b -n1 -p $script_pid2 | tail -n +8 >> proccess2.log
    top -n1 -bo +%MEM | sed 1,7d  | head -5 >> top.log
    sleep $interval
done
