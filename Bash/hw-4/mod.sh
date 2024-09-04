#!/bin/bash

search_dir="$PWD"

declare -A arr
declare -A group_arr

for file in $(find "$search_dir" -type f); do
    sum=$(sha256sum "$file" | awk '{ print $1 }')
    if [ -z "${arr[$sum]}" ]; then
        arr[$sum]=$file
    else
        if [ -z "${group_arr[$sum]}" ]; then
            group_arr[$sum]+="${arr[$sum]} $file"
        else
            group_arr[$sum]+=" $file"
        fi
    fi
done

for res in "${group_arr[@]}"; do
    echo "Group: $res"
done
