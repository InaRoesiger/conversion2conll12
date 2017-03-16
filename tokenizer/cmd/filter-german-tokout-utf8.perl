#!/usr/bin/perl

use utf8;
use Encode;

while (<>) {
    $_ = Encode::decode('utf8',$_);

    my @tags;
    # read XML mark-up
    while (/^<.*>$/) {
	push @tags, $_;
	$_ = <>;
	$_ = Encode::decode('utf8',$_);
    }

    # Handle quotation followed by ÿ (indicating sentence-final quotation)
    if (/^("|\)|\'\'|”|“|«)$/) {
	$punc = $_;
	$_ = <>;
	$_ = Encode::decode('utf8',$_);
	if ($_ eq "ÿ\n") {
	    print Encode::encode('utf8',$punc);
	    $_ = <>;
	    $_ = Encode::decode('utf8',$_);

	    # read further sgml tags
	    while (/^<.*>$/) {
		push @tags, $_;
		$_ = <>;
		$_ = Encode::decode('utf8',$_);
	    }
	}
	else {
	    $_ = $punc.$_;
	}
    }

    # clean up XML tags
    for( $i=0; $i<$#tags; $i++ ) {
	if ($tags[$i] eq "<s>\n" && $tags[$i+1] eq "</s>\n") {
	    $tags[$i] = $tags[$i+1] = '';
	}
    }

    # print the tags and the following token
    for( $i=0; $i<=$#tags; $i++ ) {
	print Encode::encode('utf8',$tags[$i]);
    }
    print Encode::encode('utf8',$_);
}
