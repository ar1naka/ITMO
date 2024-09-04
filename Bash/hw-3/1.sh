#!/bin/bash

mkdir ~/test && { 
            echo "catalog test was created successfully" >> ~/report;
            touch ~/test/$(date | tr ' ' '_')
            }
ping -c 1 www.net_nikogo.ru || echo "$(date | tr ' ' '_'): invalid host" >> ~/report

