#!/usr/bin/python
# encoding: utf-8


import sys
import re

#check if called correctly
if len(sys.argv) != 3:
    print "error: false number of arguments"
    sys.exit()

with open(sys.argv[1], 'r') as infile:
    with open(sys.argv[2]+"-ed", 'w') as outfile:
       # outfile.write(re.sub("<\/s>\n<s>", "\n", infile.read()))
        text=re.sub("<\/s>\n<s>", "\n", infile.read())
	text2=re.sub("<s>","", text)
	text3=re.sub("<\/s>","", text2)
	text4=re.sub("\n\n","\n",text3)
        outfile.write(text4)
