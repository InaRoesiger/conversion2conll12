#!/usr/bin/perl
###################################################################
###      File: filter-german-tokin.perl                         ###
###    Author: Helmut Schmid                                    ###
###   Purpose:                                                  ###
###   Created: Mon Oct  7 12:31:17 2002                         ###
###  Modified: Mon Jun 20 13:52:51 2011 (schmid)                ###
### Copyright: Institut fuer maschinelle Sprachverarbeitung     ###
###               Universitaet Stuttgart                        ###
###################################################################

use utf8;
use Encode;

while (<>) {
  $_ = Encode::decode('utf8',$_);

  tr/\177/ /;

  # replace blanks within XML tags with ð
  while (s/(<[A-Za-z].*?) ([^<>]*>)/$1ð$2/g) {}

  # Add whitespace around XML tags
  s/(<[^<>]*?>)/ $1 /g;

  # Mark parentheses and quotation symbols
  # which are immediately preceded by punctuation
  s/(\w[.\?\!])(\"|\)|\'\'|”|“|«)/$1 $2 ÿ /g;

  print Encode::encode('utf8',$_);
}
