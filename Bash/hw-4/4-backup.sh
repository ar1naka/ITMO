#!/bin/bash

latestBackup=$(ls -d $HOME/Backup-* 2>/dev/null | sort -n | tail -1)
restoreDir="$HOME/restore"

if [[ -z "$latestBackup" ]]; then
   echo "No active backup found."
   exit 1
fi

if [[ ! -d "$restoreDir" ]]; then
   mkdir "$restoreDir"
fi

for file in "$latestBackup"/*; do
   base_file=$(basename "$file")
   if [[ ! $base_file =~ \.[[:digit:]]{4}-[[:digit:]]{2}-[[:digit:]]{2}$ ]]; then
      echo "$file"
      cp -R "$file" "$restoreDir"
   fi
done
