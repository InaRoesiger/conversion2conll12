#!/usr/bin/perl

use strict;
use warnings;
use open ':encoding(utf8)'; #Equivalent to perl -CD,
binmode STDERR,":encoding(utf8)";
binmode STDOUT,":encoding(utf8)"; #Equivalent to -CS
binmode STDIN,":encoding(utf8)";

while(<STDIN>){
    chomp;
    if(/^\s*$/){ #empty line (end of sentence)
	print "\n";
    } else {
	if(s/\(([^\)\(]+)\)/\*/){
	    my ($tag,$form)=split(/ /,$1);
	    s/ //g; #remove blanks in the non-terminals
	    print join("\t",$form,$tag,$_),"\n";;
	} else { #problem on this line
	    die("problem parsing: $_");
	}
    }
}
