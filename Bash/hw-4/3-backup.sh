#!/bin/bash

source_dir="$HOME/source"
report_file="$HOME/backup-report"
current_date=$(date +%Y-%m-%d)

backup_path="$HOME/Backup-$current_date"
dateFiles=$(ls $HOME | grep -E "^Backup-" | sort -n | tail -1 | cut -d "-" -f 2,3,4)
late_date="$(date -d "$dateNow" +%s)"

tmp=0
[[ -d "$HOME/Backup-$dateFiles" ]] && tmp=$(date -d "$dateFiles" +%s)
cnt=$((($late_date-$tmp)/60/60/24))

if [[ $cnt -gt 7 ]]; then
  echo "Need to create a new dir"
  mkdir "$HOME/Backup-$current_date"
  for file in `ls -a "$source_dir"`;do
    cp -R $source_dir/$file "$HOME/Backup-$current_date"
  done
  echo "Name: " "Backup-$current_date" >> $report_file
  echo "Date: "$current_date >> $report_file
  echo "Copied files:" >> $report_file
  for file in `ls -a "$source_dir"`;do
    echo "-"$file >> $report_file
  done
 
else
  echo "Dir exists, no need to create a new dir"
  cd $HOME/Backup-$current_date
  echo "Name: " "Backup-$current_date" >> $report_file
  echo "Date: "$current_date >> $report_file
  for file in `ls -a "$source_dir"`;
  do
      file_name=$(basename "$file")
      if [[ ! -f ./$file_name ]]; then
        cp -R $source_dir/$file ./
        echo "File $file_name copied to backup directory" >> $report_file
      else
        source_size=$(stat -c%s $HOME/source/$file)
        backup_size=$(stat -c%s ./$file_name)
        if [[ ! $source_size -eq $backup_size ]]; then
          mv "$file_name" "${file_name}.$(date +%Y-%m-%d)"
          cp -R $HOME/source/$file ./
          echo "File $file_name updated in backup directory with new name ${file_name}.$(date +%Y-%m-%d)" >> $report_file
        fi

    fi  
  done
fi
