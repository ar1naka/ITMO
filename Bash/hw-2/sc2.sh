#!/bin/bash
ps ax | grep -E '/sbin/' | awk '{print $1}' > res2