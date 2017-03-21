#!/bin/bash

#=========================================
# Preprocess raw text for DE HotCoref system and run coref system
# Creates CoNLL-12 format 
#=========================================
# Usage: sh PreprocessAndCoreference.sh inputfile outputfile

# Check the following paths:

# path to output-folder that should contain finished CoNLL output
OUTDIR=./output

# path to Berkeley parser and pretty print
PARSERDIR=./parser

# path to mate
MATEDIR=./mate

# path to NER
NERDIR=./ner

# path to other mini scripts
SCRIPTSDIR=./scripts

# path to tokenizer
TOKDIR=./tokenizer

# path to coreference system
COREFDIR=./ims-hotcoref

file="$1"

  echo "Processing $file file..."

  	filename="${file##*/}"
	origfilename=$filename

	echo "filename: $filename"
	#=========================================
	# Tokenizing with tokenize-german-utf8
	#=========================================

	echo "
	Tokenising ..........................."
	
	sh $TOKDIR/tokenize-german-utf8.sh $file > $OUTDIR/$filename.tok
	
	file=$OUTDIR/$filename.tok

	echo "....................... done"

	#=========================================
	# Replace structural tags <s> with newline
	#=========================================
	echo "
	Creating right format for lemmatisation..........."
	python $SCRIPTSDIR/replaceStructuralTags.py $file
	cat -s $OUTDIR/$filename.tok-ed > $OUTDIR/$filename.tok-ed-ed
	sed '1d' $OUTDIR/$filename.tok-ed-ed > tmpfile ; mv tmpfile $OUTDIR/$filename.tok-ed

	file=$OUTDIR/$filename.tok-ed
		

	echo "....................... done"

	#=========================================
	# Creating right format for lemmatiser
	# Lemmatising
	#=========================================
	
	cd $SCRIPTSDIR

	file=./../$OUTDIR/$filename.tok-ed
	outfile=./../$OUTDIR/$filename.tok-ed.counted
	java EnumerateWordsInSentences $file $outfile
	file=./../$OUTDIR/$filename.tok-ed.counted

	cd ./../

	echo "
	Lemmatising ......................."
	cd $MATEDIR

	java -cp anna-3.61.jar  is2.lemmatizer.Lemmatizer -model lemma-ger-3.6.model -test $file -out ./../$OUTDIR/$filename.lemm
	file=./../$OUTDIR/$filename.lemm
	echo "....................... done"
	#=========================================
	# POS tagging
	#=========================================

	echo "
	POS tagging ......................."

	java -cp anna-3.61.jar  is2.tag.Tagger -model tag-ger-3.6.model -test $file -out ./../$OUTDIR/$filename.tagged
	file=./../$OUTDIR/$filename.tagged
	echo "....................... done"

	#=========================================
	# Morph tagging
	#=========================================

	echo "
	Morphological tagging .............."

	java -cp anna-3.61.jar  is2.mtag.Tagger -model morphology-ger-3.6.model -test $file -out ./../$OUTDIR/$filename.morph
	file=./../$OUTDIR/$filename.morph
	echo "....................... done"

	cd ./../

	#=========================================
	# Named Entity Recognition
	#=========================================
	cd $NERDIR	
	
	echo "
	#NER ..........................."
	file=./../$OUTDIR/$filename.tok-ed
	
	perl -ne 'chomp; print "$_ O O O O\n"' $file > ./../$OUTDIR/$filename.ne-ready

	file=./../$OUTDIR/$filename.ne-ready
	java -cp stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier classifiers/hgc_175m_600.crf.ser.gz -testFile $file > ./../$OUTDIR/$filename.ne
	
	echo "....................... done"

	cd ./../

	#=========================================
	# Parsing
	#=========================================

	file=$OUTDIR/$filename.tok-ed
	filename=$filename.tok-ed


	echo "
	Parsing ........................"

	 # replace brackets
	 # currently not: deleting new lines at the end of the file | awk '/^$/ {nlstack=nlstack "\n";next;} {printf "%s",nlstack; nlstack=""; print;}'
	 echo "Replacing brackets and creating raw file"
	 sed 's/(/*LRB*/g' $file |sed 's/)/*RRB*/g' | sed '1{/^$/d}'> $OUTDIR/$filename.tok-ed.ed
	file=$OUTDIR/$filename.tok-ed.ed
	
	cd $SCRIPTSDIR
	# Berkeley parser needs one sentence per line format
	java catTokensIntoSentencePerLine ./../$file ./../$OUTDIR/$filename.ospl
	file=./../$OUTDIR/$filename.ospl
	cd ./../
	cd $PARSERDIR

	 # parse 
	 echo "Parsing file $file"
	java -jar ./BerkeleyParser-1.7.jar -gr ger_sm5.gr < $file > ./../$OUTDIR/$filename.parsed
	
	file=$OUTDIR/$filename.parsed


	# some sentences do not get parsed (too long or strange characters), we need to replace them with dummy parses

	cd ./../
	cd $SCRIPTSDIR
	java replaceBerkeleyWrongParses ./../$file ./../$OUTDIR/$filename.ospl ./../$OUTDIR/$filename.parsed.edited
	file=$OUTDIR/$filename.parsed.edited
	filename=$filename.parsed.edited
	cd ./../
	echo "....................... done"		
	#=========================================
	# Parsing --pretty print
	#=========================================

	echo "
	Pretty print ...................."

 	java -cp $SCRIPTSDIR/pp.jar ims.util.PTBPrettyPrinter < $file | $SCRIPTSDIR/ptbpretty2cols.pl > $OUTDIR/$filename.pretty

	file=$OUTDIR/$filename.pretty
	filename=$filename.pretty

 	 # replace brackets
	sed 's/\*LRB\*/(/g' $file | sed 's/\*RRB\*/)/g' | sed 's/PSEUDO/VROOT/g' > $OUTDIR/$filename.ed

	file=$OUTDIR/$filename.ed
	filename=$filename.ed
	echo "....................... done"


	#=========================================
	# Merge
	#=========================================

	echo "
	Merge .................."
	cd $SCRIPTSDIR
	java -Xmx4g -cp "./mergeForCoNLL.jar" mergeForCoNLL ./../$OUTDIR/$origfilename.morph ./../$OUTDIR/$origfilename.tok-ed.parsed.edited.pretty.ed ./../$OUTDIR/$origfilename.ne ./../$OUTDIR/$origfilename.lemm ./../$OUTDIR/$origfilename.merged
	echo "....................... done"


	
	#=========================================
	# Adjust parses
	#=========================================
	echo "
	Adjust parses  .................."
	java preProcessCoNLLForHotCoref ./../$OUTDIR/$origfilename.merged ./../$OUTDIR/$origfilename.conll

	echo "....................... done"


	#=========================================
	# Adjust NE
	#=========================================
	echo "
	Bring NE into format required for coref  .................."
	java convertNEIntoRightFormat ./../$OUTDIR/$origfilename.conll ./../$OUTDIR/$origfilename.conll.final

	cd ./../

	#=========================================
	# Coreference resolution
	#=========================================
	echo "
	Coreference resolution  .................."
	cd $COREFDIR
	java -Xmx20g -cp "./ims-hotcoref-standalone.jar:./lib/*" ims.hotcoref.Test -model ./coref-model-2017  -out ./../$OUTDIR/$origfilename.conll.final.out2 -cores "4" -in ./../$OUTDIR/$origfilename.conll.final -lemmaBased -beam 20

	#java -Xmx20g -cp "./ims-hotcoref-standalone.jar:./lib/*" ims.hotcoref.Test -model ./coref-model-tueba9-trained-on-all  -out ./../$OUTDIR/$origfilename.conll.final.out -cores "4" -in ./../$OUTDIR/$origfilename.conll.final -lemmaBased -beam 20
	#=========================================
	# Deletion of temp files and output
	#=========================================
	
	cd ./../$OUTDIR
	mv $origfilename.conll.final.out2 $2
	#rm $origfilename.*

	
	echo "

	FINISHED  .................."





 
