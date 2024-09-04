#!/bin/bash

if [[ $# -ne 1 ]]; then
  exit 1
fi

file_name="$1"
trash_dir="$HOME/.trash"


if [[ ! -d $trash_dir ]]; then
  mkdir $trash_dir
  echo "directory trash is successfully created"
fi

link_number=$(ls -l $trash_dir | grep -c ^-)
trash_dir="$trash_dir/$link_number"

if [[ -f "$file_name" ]]; then
    ln "$file_name" $trash_dir
    rm "$file_name"
    echo "$PWD/'$file_name' $link_number" >> ~/.trash.log
else 
    echo "No such file"
fi
