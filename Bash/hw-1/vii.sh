#!/bin/bash
mail="[[:alnum:]._-]+@[[:alnum:]._-]+.[[:alpha:]]+"

grep -Ehsor $mail /etc | tr '\n' ',' | sed 's/.$//' > emails.lst