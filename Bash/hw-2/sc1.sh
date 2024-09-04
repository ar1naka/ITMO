#!/bin/bash
res=$(ps -u $USER -o pid,command --no-headers | awk '{print $1":"$2}')
echo `wc -l <<< $res` > res1
echo $res >> res1