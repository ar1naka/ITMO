#!/bin/bash

echo "enter pipe name"
read pipe

while true; do
  if read LINE; then
    if [[ $LINE == "switch" ]]; then
      echo "enter new pipe name"
      read new_pipe
      pipe=$new_pipe
      echo $pipe
    else
      echo "$LINE" > $pipe
    fi
  fi
done
