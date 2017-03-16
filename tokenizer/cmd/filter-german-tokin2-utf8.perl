#!/usr/bin/perl

use utf8;
use Encode;

while (<>) {
    $_ = Encode::decode('utf8',$_);

    # restore whitespace inside of XML tags
    tr/ð/ /;
    # insert missing whitespace after punctuation
    if (length($_) > 5 && 
	(/^([A-Za-zÀ-ÿ]+)([.:\?\!])([\"\)\']*)([\(,]*)([A-Za-zÀ-ÿ]+)$/ ||
	 /^([A-Za-zÀ-ÿ]+)([,;\/])([\"\)\']*)([\(,]*)([A-Za-zÀ-ÿ]+)$/))
    {
	print Encode::encode('utf8',"$1\n$2\n");
	print Encode::encode('utf8',"$3\n") if ($3 ne '');
	print Encode::encode('utf8',"$4\n") if ($4 ne '');
	print Encode::encode('utf8',"$5\n");
    }
    else {
	print Encode::encode('utf8',$_);
    }
}
