#!/usr/bin/perl

use utf8;
use Encode;

while (<>) {
    $_ = Encode::decode('utf8',$_);

    chomp;
    if (/^<.*>$/) {
	print Encode::encode('utf8',"$_\n");
    }
    
    elsif (/^(Mo|Di|Mi|Do|Fr|Sa|So)\.-(Mo|Di|Mi|Do|Fr|Sa|So)\.$/) {
	print Encode::encode('utf8',"$1.\n-\n$2.\n");
    }

    elsif (/^(.*[^.])([.?\!,;:])([^\/.].*)$/) {
	$w1 = $1;
	$p = $2;
	$w2 = $3;
	if (/^Sat\.1|\.de$/) {
	    print Encode::encode('utf8',"$_\n");
	}
	elsif ($w1 !~ /^[\"\(]*[A-Za-zÀ-ÿ]{2,}[\"\)]*$/ && $w2 !~ /^[A-Za-zÀ-ÿ]{2,}$/) {
	    print Encode::encode('utf8',"$_\n");
	}
	else {
	    $w1 =~ s/^([\(\"])([^\)\"]*.)$/$1\n$2/;
	    $w2 =~ s/^([\(\"])([^\)\"]*.)$/$1\n$2/;
	    $w1 =~ s/^(.[^\(\"]*)([\)\"])$/$1\n$2/;
	    $w2 =~ s/^(.[^\(\"]*)([\)\"])$/$1\n$2/;
	    print Encode::encode('utf8',"$w1\n$p\n$w2\n");
	}
    }

    else {
	print Encode::encode('utf8',"$_\n");
    }
}
