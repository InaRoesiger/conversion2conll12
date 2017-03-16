#!/usr/bin/perl

use utf8;
use Encode;

use Getopt::Std;
getopts('hm:ons:p:x');

$ucchars='A-ZÀ-Þ';
$lcchars='a-zß-ÿ';

if (defined($opt_h)) {
    print "\nUsage: $0 [options] ...files...\n";
    print "\nOptions:\n";
    print "-o: Autoflush the output stream.\n";
    print "-n: Print an empty line after each sentence.\n";
    print "-m <marker>: Use <marker> rather than \"s\" as sentence tag.\n";
    print "-s <chars>: Treat the characters in <chars> as sentence markers.\n";
    print "-p l: Treat the XML tags in l as sentence boundary indicators.\n";
    print "-x: put sentence boundary tags before and after XML tags.\n";
    print "-g: for handling Greek characters.\n";
    die;
}

if (defined($opt_m)) {
    $stag = "<".$opt_m.">\n";
    $etag = "</".$opt_m.">\n";
} else {
    $stag = "<s>\n";
    $etag = "</s>\n";
}

if (defined($opt_s)) {
    $smarkers = "$opt_s";
} else {
    $smarkers = ".!?";
}

if (defined($opt_p)) {
    foreach $t (split(/\s+/, $opt_p)) {
	$SBTag{"$t\n"} = 1;
    }
}

$etag .= "\n" if (defined($opt_n));

$| = 1 if (defined $opt_o);
$start = 1;

while (<>) {
    $_ = Encode::decode('utf8',$_);
    s/\s*$/\n/;
    if ($_ eq $stag || $_ eq $etag) {
	print $etag unless $start;
	$start = 1;
	next;
    }
    elsif ($_ eq "...\n") {
	print $stag if ($start);
	print Encode::encode('utf8',$_);
	$_ = <>;
	$_ = Encode::decode('utf8',$_);
	if (/^[$ucchars]/) {
	    print $etag;
	    $start = 1;
	}
	elsif ($_ eq $stag) {
	    print $etag;
	    $start = 1;
	    next;
	}
	elsif (exists $SBTag{$_}) {
	    print $etag,Encode::encode('utf8',$_);
	    $start = 1;
	    next;
	}
	elsif (/^(,,|``)$/) {
	    $punc = $_;
	    $_ = <>;
	    $_ = Encode::decode('utf8',$_);
	    if (/^[$ucchars]/) {
		print $etag,Encode::encode('utf8',$punc);
		$start = 1;
	    }
	    elsif (exists $SBTag{$_}) {
		print $etag, Encode::encode('utf8',"$punc$_");
		$start = 1;
		next;
	    }
	    else {
		print Encode::encode('utf8',$punc);
	    }
	}
	elsif (/^[\'\"«„“”]*$/) {
	    print Encode::encode('utf8',$_);
	    $_ = <>;
	    $_ = Encode::decode('utf8',$_);
	    if (/^[$ucchars]/) {
		print $etag;
		$start = 1;
	    }
	    elsif (exists $SBTag{$_}) {
		print $etag,Encode::encode('utf8',$_);
		$start = 1;
		next;
	    }
	}
    }

    elsif ($_ eq "\n" || exists $SBTag{$_}) {
	if ($start == 0) {
	    print $etag;
	    $start = 1;
	}
	print Encode::encode('utf8',$_);
	next;
    }

    elsif (/^<.*>$/) {
	if ($_ eq $stag) {
	    $start = 0;
	} 
	elsif (defined $opt_x && !$start) {
	    print $etag;
	    $start = 1;
	}
	if ($_ ne $etag) {
	    print Encode::encode('utf8',$_); 
	}
	next;
    }

    print $stag if ($start);
    print Encode::encode('utf8',$_);
    $start = 0;

    if (/^[$smarkers]$/ || /^[$smarkers]\t/) {
	print $etag;
	$start = 1;
    }
}

print $etag unless ($start);
