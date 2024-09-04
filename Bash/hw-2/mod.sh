#!/bin/bash

zombie_pids=$(ps ax -u $USER | grep -w Z)

echo "Zombie process IDs: " "$zombie_pids"



