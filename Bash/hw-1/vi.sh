#!/bin/bash
grep "] (WW)" /var/log/anaconda/X.log | sed -e 's/(WW)/Warning:/' > full.log
grep "] (II)" /var/log/anaconda/X.log | sed -e 's/(II)/Information:/' >> full.log


