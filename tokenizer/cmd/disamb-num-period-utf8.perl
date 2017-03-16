#!/usr/bin/perl

use utf8;
use Encode;

use Getopt::Std;
getopts('hdf:og');

$ucchars='A-ZÀ-Þ';
$lcchars='a-zß-ÿ';

if (!defined($opt_f) || defined($opt_h)) {
    die "\nUsage: $0 {-d} {-o} -f <number parameter file>\n";
}

if (!open(FILE, $opt_f)) {
    die "\nCan\'t open number parameter file: ",$opt_f,"\n";
}

$| = 1 if (defined $opt_o);

$flag = 0;
while (<FILE>) {
    $_ = Encode::decode('utf8',$_);
    chomp;
    next if (/^ *>>>.*<<< *$/);  # ignore comments
    @F = split(/\t/);
    if (length == 0) {
	$flag++;
    } elsif ($flag == 0) {
	if ($#F != 4) {
	    print STDERR;
	    die "Wrong format";
	}
	$preceding{$F[0],$F[1]} = $F[4];
    } elsif ($flag == 1) {
	if ($#F != 2) {
	    die "Wrong format";
	}
	$preceding_suffix{$F[0],$F[1]} = $F[2];
    } elsif ($flag == 2) {
	if ($#F != 4) {
	    die "Wrong format";
	}
	$following{$F[0],$F[1]} = $F[4];
    } elsif ($flag == 3) {
	if ($#F != 3) {
	    die "Wrong format";
	}
	$ordinal_ratio{$F[0]} = $F[3];
    } elsif ($flag == 4) {
	if ($#F != 3) {
	    die "Wrong format";
	}
	$corr_factor{$F[0]} = $F[3];
    } elsif ($flag == 5) {
	if ($#F != 3) {
	    die "Wrong format";
	}
	$following_backoff{$F[0]} = $F[3];
    } elsif ($flag == 6) {
	if ($#F != 2) {
	    die "Wrong format";
	}
	$corr_factor2{$F[0],$F[1]} = $F[2];
    } elsif ($flag == 7) {
	if ($#F < 1 || $#F > 2) {
	    die "Wrong format";
	}
	$lowerprob{$F[0]} = $F[1];
    } elsif ($flag == 8) {
	if ($#F != 1) {
	    die "Wrong format";
	}
	$kuerzel{$F[0]} = $F[1];
    } else {
	die "Wrong format";
    }
}
close(FILE);


$w1=$w2="";

sub print_token {
    
    if ($w2 eq "") {
	return;
    }
    
    if ($w2 =~ /[0-9].*\.$/ && $w2 !~ /[$ucchars$lcchars]/) {  # ordinal number?
	
	$w1 =~ s/^.*[-\/]([^0-9-])/\1/;  # "Michael-Stumpf-Str" is mapped to "Str"
	
	if ($w2 =~ /[_,:]/) {  # phone number, fraction or similar
	    $curr = substr($curr,0,-1)."\n."; # cut off the period
	}
	elsif ($w3 !~ /^[-,;:$lcchars]/ || 
	       $w3 =~ /-[$ucchars]/ || exists($kuerzel{$w3})) {
	    
	    # probability of ordinal/cardinal in the context of the preceding word
	    if (exists($preceding{$w1,$w2})) {
		$ratio = $preceding{$w1,$w2};
	    } elsif (exists($preceding{$w1,"0"})) {
		$ratio = $preceding{$w1,"0"};
		# try the suffix table
	    } elsif (exists($preceding_suffix{lowercase(substr($w1,-5)),"0"})) {
		$ratio = $preceding_suffix{lowercase(substr($w1,-5)),"0"};
	    } elsif (exists($preceding_suffix{lowercase(substr($w1,-4)),"0"})) {
		$ratio = $preceding_suffix{lowercase(substr($w1,-4)),"0"};
	    } elsif (exists($preceding_suffix{lowercase(substr($w1,-3)),"0"})) {
		$ratio = $preceding_suffix{lowercase(substr($w1,-3)),"0"};
	    } elsif (exists($preceding_suffix{lowercase(substr($w1,-2)),"0"})) {
		$ratio = $preceding_suffix{lowercase(substr($w1,-2)),"0"};
		# fallback to unconditioned probability
	    } elsif (exists($ordinal_ratio{$w2})) {
		$ratio = $ordinal_ratio{$w2};
		$ratio *= 5 if ($w1 =~ /^<..*>$/);  # SGML tag
	    } else {
		$ratio = $ordinal_ratio{"0"};
		$ratio *= 5 if ($w1 =~ /^<..*>$/);  # SGML tag
	    }

	    $pratio = $ratio * 0.2;	# P(w z. . w') / P(w z . w')
	    
	    # probability of ordinal/cardinal in the context of the following word
	    if (exists($following{$w2,$w3})) {
		$ratio *= $following{$w2,$w3} * $corr_factor{$w2};
	    } elsif (($suff = $w3) =~ s/.*-(....)/$1/ &&
		     exists($following{$w2,$suff})) {
		$ratio *= $following{$w2,$suff} * $corr_factor{$suff};
	    } elsif (exists($following{"0",$w3})) {
		$ratio *= $following{"0",$w3} * $corr_factor{"0"};
	    } elsif ($w2 eq "1999." && $w3 =~ /^<..*>$/) {
		$ratio = $pratio = 0;
	    } else {
		# fallback
		if (exists($lowerprob{$w3})) { # z. L
		    $ratio *= (1.0 - $lowerprob{$w3});
		} elsif ($w3 =~ /^<..*>$/) {   # SGML tag
		    $ratio *= 0.1;
		}
		if (exists($following_backoff{$w2})) {
		    $ratio *= $following_backoff{$w2} * $corr_factor{$w2};
		} elsif (exists($following_backoff{"0"})) {
		    $ratio *= $following_backoff{"0"} * $corr_factor{"0"};
		}
	    }

	    # correction factor for cases where P(.|z) deviates from P(.|wz)

	    if (exists($corr_factor2{$w1,$w2})) {
		$ratio *= $corr_factor2{$w1,$w2};
	    } elsif (exists($corr_factor2{$w1,"0"})) {
		$ratio *= $corr_factor2{$w1,"0"};
	    }
	    
	    if ($ratio > 1.0 && $ratio > $pratio) {
		# ordinal number
	    } elsif ($pratio > 1.0) {
		# ordinal number at end of sentence
		$curr = $curr."\n.";	# add sentence period after ordinal
		if ($opt_d) {
		    $next = lowercasefirst($next);
		}
	    } else {
		$curr = substr($curr,0,-1)."\n."; # cut off the period
		if ($opt_d && exists($lowerprob{$w3}) && $lowerprob{$w3} > 0.9) {
		    $next = lowercasefirst($next);
		}
	    }
	}
    }
    
    if ($w1 ne "") {
	print "\n";
    }
    print Encode::encode('utf8',$curr);
}


while (<>) {
    $_ = Encode::decode('utf8',$_);
    chomp;
    $w3 = $next = $_;
    $period = 0;
    
    # normalization of numbers
    if ($w3 =~ /[0-9]/ && $w3 !~ /[$ucchars$lccchars]/) {
	
	if ($w3 =~ s/\.$//) {	# remove period of possible ordinal number
	    $period = 1;
	}
	if ($w3 =~ /^0?[1-9]$/ || $w3 =~ /^1[0-2]$/) {
	    $w3 = "12";
	} elsif ($w3 =~ /^[012][0-9]$/ || $w3 =~ /^3[0-1]$/) {
	    $w3 = "31";
	} elsif ($w3 =~ /^19[0-9][0-9]$/) {
	    $w3 = "1999";
	} elsif ($w3 =~ /^[0-3]?[0-9]\.[0-3]?[0-9]$/) {
	    $w3 = "11.9";
	} elsif ($w3 =~ /^[0-3]?[0-9]\.[0-3]?[0-9]\.19[0-9][0-9]$/ ||
		 $w3 =~ /^[0-3]?[0-9]\.[0-3]?[0-9]\.[5-9][0-9]$/) {
	    $w3 = "11.9.1999";
	} elsif ($w3 =~ /^[0-9]*$/) {
	    $w3 = "100";
	} elsif ($w3 =~ /^[0-9,]*$/) {
	    $w3 = "1,11";
	} elsif ($w3 =~ /^[0-9.]*$/) {
	    $w3 = "1.111";
	} elsif ($w3 =~ /^[0-9:]*$/) {
	    $w3 = "1:0";
	} elsif ($w3 =~ /^[0-9\/]*$/) {
	    $w3 = "1/0";
	} elsif ($w3 =~ /^[-0-9 \/]*$/) {
	    $w3 = "12_34_56";
	} else {
	    $w3 = "1%(0)";
	}
	if ($period) {
	    $w3 = $w3.".";		# restore period
	}
    }
    
    print_token;
    
    $w1 = $w2;
    $w2 = $w3;
    $curr = $next;
}

print_token;
print "\n";

#############################
# case conversion functions #
#############################

sub lowercase {
    my $string=shift;

    $string =~ tr/A-Z¶¸¹º¼¾¿À-Þ/a-zÜÝÞßüýþà-þ/;
    return $string;
}

sub uppercase {
    my $string=shift;

    $string =~ tr/a-zÜÝÞßýà-ýÞþ/A-Z¶¸¹º¾À-Ý¹¿/;
    return $string;
}

sub lowercasefirst {
    my $string=shift;

    return lowercase(substr($string,0,1)).substr($string,1);
}

sub uppercasefirst {
    my $string=shift;

    return uppercase(substr($string,0,1)).substr($string,1);
}
