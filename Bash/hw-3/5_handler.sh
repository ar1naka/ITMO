#!/bin/bash

mode=0
res=1
r='^[0-9]+$'
tail -f pipe |
while true 
do
    read LINE
    case $LINE in
        "+")
            echo "adding mode"
            mode=0
            ;;
        "*")
            echo "mul mode"
            mode=1
            ;;
        "QUIT") 
            killall tail
            echo "Exit"
            exit 0
            ;;    
        *)
           if [[ "$LINE" =~ $r ]]; then
             if [[ mode -eq 0 ]]; then
                 echo "Adding: $LINE"
                 res=$(($res + $LINE))
             elif [[ mode -eq 1 ]]; then
                 echo "Multiplying: $LINE"
                 res=$(($res * $LINE))
             fi
           else 
             killall tail
             echo "Invalid data"
	     exit
	   fi
           ;;
    esac
    echo "Result: $res"
done

