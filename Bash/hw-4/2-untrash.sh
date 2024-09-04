#!/bin/bash

filename="$1"
if [[ $# -ne 1 ]]; then
  exit 1
fi

(grep -E "$filename" ~/.trash.log) | while read -r line; do
    read -p "Do you want to restore the file: $line? [y/n]: " answer<&1

    if [ "$answer" == "y" ]; then
        fullpath=$(echo "$line" | sed 's/^\(.*\) \(.*\)$/\1/')
        hardlink=$(echo "$line" | sed 's/^\(.*\) \(.*\)$/\2/')

        if [ ! -d $(dirname "$fullpath") ]; then
            fullpath="~"
            echo "Directed to HOME dir"
        else 
             ln ~/.trash/"$hardlink" "$filename" && rm ~/.trash/"$hardlink"
            if [ -f "$filename" ]; then
                echo "The file was restored along the way $fullpath"
            else
                read -p "New name is " newname
                ln ~/.trash/$hardlink $(dirname "$fullpath")/$newname && rm ~/.trash/$filename
                echo "Fail in $(dirname $fullpath)/$newname"
            fi
        fi
    fi
done
