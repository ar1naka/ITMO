#!/bin/bash

awk '$2=="INFO" {print}' /var/log/anaconda/syslog > info.log