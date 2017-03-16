#!/usr/bin/perl

use utf8;
use Encode;

while (<>) {
    $_ = Encode::decode('utf8',$_);
    foreach $x (split) {
	my $final;
	do {
	    $finished = 1;
	    if ($x =~ s/^([\$£¥¢§©®¿¡])(.)/$2/ ||
		$x =~ s/^(»)([^«]*.)$/$2/ ||
		$x =~ s/^(«)([^»]*.)$/$2/ ||
		$x =~ s/^(<)([^>]*)$/$2/ ||
		$x =~ s/^(>)([^<]*.)$/$2/ ||
		$x =~ s/^(\")([^\"]*.)$/$2/ ||
		$x =~ s/^([„“”])([^“”]*.)$/$2/ ||
		$x =~ s/^(\()([^\)]*.)$/$2/ ||
		$x =~ s/^(\[)([^\]]*.)$/$2/ ||
		$x =~ s/^(\{)([^\}]*.)$/$2/ ||
		$x =~ s/^([\`'])([^\`']*.)$/$2/)
	    {
		print Encode::encode('utf8',$1),"\n";
		$finished = 0;
	    }
	    if ($x =~ s/([^.])(\.+)$/$1/ ||
		$x =~ s/(.)([,;:?¿!¡\$£¥¢%§%©®¤°¶])$/$1/ ||
		$x =~ s/^(.[^»]*)(«)$/$1/ ||
		$x =~ s/^(.[^«]*)(»)$/$1/ ||
		$x =~ s/^(.[^>]*)(<)$/$1/ ||
		$x =~ s/^([^<]*)(>)$/$1/ ||
		$x =~ s/^(.[^\"]*)(\")$/$1/ ||
		$x =~ s/^(.[^`']*)([`'])$/$1/ ||
		$x =~ s/^(.[^\(]*)(\))$/$1/ ||
		$x =~ s/^(.[^\[]*)(\])$/$1/ ||
		$x =~ s/^(.[^\{]*)(\})$/$1/)
	    {
		$final = "$2\n$final";
		$finished = 0;
	    }
	}
	while (!$finished);
	print Encode::encode('utf8',"$x\n$final") unless $x eq '';
    }
}
