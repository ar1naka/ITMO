#!/bin/bash

echo $$ > .pid
res=1
mode=0
add()
{
	mode=0
	echo "adding mode"
}

multiply()
{
	mode=1
	echo "mul mode"
}

terminate()
{
	echo "TERM"
	exit
}

trap 'add' USR1
trap "multiply" USR2
trap "terminate" SIGTERM

while true
do
  if [[ $mode -eq 0 ]]; then
     res=$(( $res + 2 ))
  else 
     res=$(( $res * 2 ))
  fi
  echo "$res"
  sleep 1s
done

