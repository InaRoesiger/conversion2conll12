
//INPUT FORMAT: OUTPUT OF MERGING SCRIPT, IN CONLL FORMAT 
//OUTPUT FORMAT: AS HOTCOREF EXPECTS IT

//changes the format so that hotcoref accepts it
//changes parse trees so that we can use them, for example by inserting NPs into flat PPs

import java.io.*;
import java.util.*;


public class preProcessCoNLLForHotCoref {
	
	
	public static void main(String[] args){
		
		try {
			
			//%%%%%%%%%%%%PART ONE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//replace spaces with tab, adjust header, replace single word parses with * with NP, replace _ with -
			//split morph info into separate columns
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

			
			BufferedReader reader = new BufferedReader(new FileReader( new File(args[0])));
			BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
			BufferedWriter writertmp = new BufferedWriter(new FileWriter("./tmp.txt"));
			
			ArrayList<String> ed0=new ArrayList<String>();
	

			String text="";
			String file="";
			int counter=0;
			while (reader.ready()) {
				counter++;
				//System.out.println(counter);
				text=reader.readLine();
				
				if (text.contains("#begin")){
					//text=text.replace("document ","document (");
					//text=text+("); part 000");
				}
				
				else if (text.contains("#end")){}
				else if (text.equals("")){}
				
				else {
					text=text.replace("_", "-");
					//text=text.replaceAll("\\s+", "\t");
					text=text.replace("export-","export_");
					String columns[]=text.split("\t");
					if (columns[2].equals("1")&&(columns[5].equals("*"))){
						text=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t"+"(NP*)"+"\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
					}
					//NUM
				/*	if (columns[7].contains("num")){
							
						if (columns[7].contains("num=sg")){
							text=text.replace(columns[7]+"\t"+columns[8], "sg");
							columns[8]="sg";
						}
						else if (columns[7].contains("num=pl")){
							text=text.replace(columns[7]+"\t"+columns[8], "pl");
							columns[8]="pl";

						}
						else {text=text.replace(columns[7]+"\t"+columns[8], "-");
								columns[8]="-";}	
					}
					else {
						text=text.replace(columns[6]+"\t"+columns[7]+"\t"+columns[8], columns[6]+"\t"+"-");
						columns[8]="-";}	
					
					//GENDER
					if (columns[7].contains("gend")){
						if (columns[7].contains("gend=masc")){
							text=text.replace(columns[6]+"\t"+columns[8]+"\t"+columns[9], columns[6]+"\t"+columns[8]+"\t"+"masc");
						}
						else if (columns[7].contains("gend=fem")){
							text=text.replace(columns[6]+"\t"+columns[8]+"\t"+columns[9], columns[6]+"\t"+columns[8]+"\t"+"fem");
						}
						else if (columns[7].contains("gend=neut")){
							text=text.replace(columns[6]+"\t"+columns[8]+"\t"+columns[9], columns[6]+"\t"+columns[8]+"\t"+"neut");
						}	
						else {text=text.replace(columns[6]+"\t"+columns[8]+"\t"+columns[9], columns[6]+"\t"+columns[8]+"\t"+"-");}
					}
					
					else {text=text.replace(columns[6]+"\t"+columns[8]+"\t"+columns[9], columns[6]+"\t"+columns[8]+"\t"+"-");}
*/
				}
				if (text.endsWith("-")){
					text=text.substring(0,text.length()-1);
					text=text+"_";
				}
				
				text=text.replace("gend=(NP*)","gend=*");
				ed0.add(text);
			}
			
			

		
			
			
			//%%%%%%%%%%%%PART TWO %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//adjust parses created with PSParser so that NPs match annotated markables better
			//hard coded for PSParser
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			ArrayList<String> ed1=new ArrayList<String>();
			ArrayList<String> ed2=new ArrayList<String>();
			ArrayList<String> ed3=new ArrayList<String>();
			ArrayList<String> ed4=new ArrayList<String>();
			ArrayList<String> ed5=new ArrayList<String>();
			ArrayList<String> ed6=new ArrayList<String>();
			
			//Replace (DL*) with (NP*)
			ed1=ReplaceDL(ed0);
			

			//non-embedded PPS
			ed2=InsertNPsIntoPPs(ed1);
			
			for (int i=0;i<ed2.size();i++){
				writertmp.write(ed2.get(i));				
			}
			writertmp.close();
			BufferedReader readertmp = new BufferedReader(new FileReader( new File("./tmp.txt")));
			
			//embedded PPs
			InsertNPsIntoPPsEmbedded(readertmp);
	
			BufferedReader readertmp2 = new BufferedReader(new FileReader( new File("./tmp2.txt")));
			//Single word NPs
			InsertNPs(readertmp2);
			BufferedReader readertmp3 = new BufferedReader(new FileReader( new File("./tmp.txt")));

			//PN adjustment
			PNadjust(readertmp3);
			BufferedReader readertmp4 = new BufferedReader(new FileReader( new File("./tmp2.txt")));

			//CNP adjustment
			CNPadjust(readertmp4,writer);

			
			
			//DELETE TMP FILES HERE
			
			File filetmp = new File("./tmp.txt");
			filetmp.delete();
			File filetmp2 = new File("./tmp2.txt");
			filetmp2.delete();

		}

		catch(Exception e){
		}
	}
	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//FUNCTIONS   
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
public static ArrayList ReplaceDL(ArrayList<String> hs) throws IOException {

	String text="";
	ArrayList<String> tmp=new ArrayList<String>();
	for (int j=0;j<hs.size();j++){	
		text=hs.get(j).replace("(DL*)","(NP*)");
		tmp.add(text);
	}
	return tmp;
	
	
}

public static ArrayList InsertNPsIntoPPs(ArrayList<String> hs)  {

	Boolean PP=false;
	Boolean PP2=false;
	Boolean PP3=false;

	Boolean NPinserted=false;
	Boolean NPinserted2=false;
	Boolean NPinserted3=false;

	String parse="";
	String line="";
	String sentence="";
	String tag="";
	String linenew="";
	String parsenew="";
	int counter=0;
	
	String text="";
	ArrayList<String> tmp=new ArrayList<String>();
	
	for (int j=0;j<hs.size();j++){	
	
		line=hs.get(j);
		counter++;
	
	if (line.contains("\t")){sentence=sentence+line+"\n";}
	
	//Satz vorbei: Sentence prozessieren	
	else if (line.equals("")){
		
		tmp.add("\n");
		

		int bracketcounterauf=0;
		int bracketcounter2auf=0;
		int bracketcounter2zu=0;
		int bracketcounterzu=0;
		Boolean firstPP=true;
		Boolean lineafter=false;
		Boolean secondPP=true;
		Boolean lineafter2=false;
		
		String lines[]=sentence.split("\n");
		for (int i=0;i<lines.length;i++){
			
			parse=lines[i].split("\t")[5];
			String parseold=parse;
			tag=lines[i].split("\t")[4];
			String word=lines[i].split("\t")[3];
					
			//found first PP
			if (parse.contains("(PP*")&&(firstPP==true)&&(!parse.contains("(PP*)"))){
				firstPP=false;
				bracketcounterauf++; 
				lineafter=true;
				
				
			}
			//line after
			else if (lineafter==true){
				lineafter=false;
				
				String parsecopy=parse;
				while (parsecopy.contains("(")){
					bracketcounterauf++;
					parsecopy=parsecopy.replaceFirst("\\(","");
				}
				while (parsecopy.contains(")")){
					bracketcounterzu++;
					parsecopy=parsecopy.replaceFirst("\\)","");
				}
				
				 if (parse.contains(")")){parse=parse.replace("*", "(NP*)");firstPP=true; bracketcounterauf=0;bracketcounterzu=0;}
				 else if (parse.contains("*")){
					 parse=parse.replace("*", "(NP*");NPinserted=true;}
				else if (parse.contains(")")&&(parse.contains("("))){} //later
				
			
				
			}

			
			else if ((firstPP==false)&&(parse.contains("(")&&(!parse.contains(")")))){
		
					
					String parsecopy=parse;
					while (parsecopy.contains("(")){
						bracketcounterauf++;
						parsecopy=parsecopy.replaceFirst("\\(","");
					}
				
			}
			
			//Klammer zu
			else if ((firstPP==false)&&(parse.contains(")")&&(!parse.contains("(")))){
				
		
				//normal case
				
				
				//check: bracket of PP or not
				String parsecopy=parse;
				
				while (parsecopy.contains(")")){
					bracketcounterzu++;
					if (bracketcounterauf==bracketcounterzu){
						
						if (NPinserted==true){

							parse=parse.replace("*)","*))");NPinserted=false; firstPP=true;bracketcounterauf=0; bracketcounterzu=0;
							break;
						}
						
					}
					
					parsecopy=parsecopy.replaceFirst("\\)","");
							
				}
				
				
				
				
				
			}
			
			else if  ((firstPP==false)&&(parse.contains(")")&&(parse.contains("(")))){
			
			
			//embedded PPs end

			//gucken ob nur () oder mehr davor oder danach
			
			String parsecopy=parse;
			while (parsecopy.contains("(")){
				bracketcounterauf++;
				parsecopy=parsecopy.replaceFirst("\\(","");
			}
			while (parsecopy.contains(")")){
				bracketcounterzu++;
				parsecopy=parsecopy.replaceFirst("\\)","");
				
				if (NPinserted==true){
					parse=parse.replace("*)","*))");NPinserted=false; firstPP=true;bracketcounterauf=0; bracketcounterzu=0;
				    break;
				}
				
			}
			}
			
			//tmp.add(lines[i].replace(parseold, parse)+"\n");
			String[] columns=lines[i].split("\t");
			String linePart1=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t";
			String linePart2="\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
			tmp.add(linePart1+parse+linePart2+"\n");
		}	
	
	
	sentence="";
	
	}
	
	else if  (line.contains("begin document")){
		tmp.add(line);
	}
	
	
	
	else {
		tmp.add("\n"+line+"\n");
		} //hier war \n noch davor
	}
	
	
	
	return tmp;
}

public static void InsertNPsIntoPPsEmbedded(BufferedReader reader) throws IOException {

	BufferedWriter writertmp2 = new BufferedWriter(new FileWriter("./tmp2.txt"));

	String parse="";
	String line="";
	String sentence="";
	String tag="";
	String linenew="";
	String parsenew="";
	int counter=0;

	while (reader.ready()) {
		
		line=reader.readLine();
		
		counter++;

	if (line.contains("\t")){sentence=sentence+line+"\n";}
	

	//Satz vorbei: Sentence prozessieren	
	else if (line.equals("")){
		
		writertmp2.write("\n");
		
		Boolean NPinserted=false;
		Boolean NPinserted2=false;
		Boolean NPinserted3=false;
		
		int bracketcounterauf=0;
		int bracketcounter2auf=0;
		int bracketcounter2zu=0;
		int bracketcounterzu=0;
		
		Boolean firstPP=true;
		Boolean lineafter=false;
		Boolean secondPP=true;
		Boolean lineafter2=false;
		
		String lines[]=sentence.split("\n");
		for (int i=0;i<lines.length;i++){
			
			parse=lines[i].split("\t")[5];
			String parseold=parse;
			tag=lines[i].split("\t")[4];
			String word=lines[i].split("\t")[3];
					
		
		
			//embedded found
			
					if (secondPP==false){
					
							
						//HIER WEITERMACHEN: FIRST LINE OF EMBEDDED PP
						//first line
						if (lineafter2==true){
							
							lineafter2=false;

							String parsecopy=parse;
							while (parsecopy.contains("(")){
								bracketcounter2auf++;
								parsecopy=parsecopy.replaceFirst("\\(","");
							}
							while (parsecopy.contains(")")){
								bracketcounter2zu++;
								parsecopy=parsecopy.replaceFirst("\\)","");
							}
							
									//if and else
									 if (parse.contains(")")){
										 
										 if (parse.contains("(NP*)")){
										 secondPP=true; 
										 bracketcounter2auf=0;
										 bracketcounter2zu=0;}
										 
										 else {
											 parse=parse.replace("*", "(NP*)");
											 secondPP=true; 
											 bracketcounter2auf=0;
											 bracketcounter2zu=0;
										 }
										 }
									
								
									 else if (parse.contains("*")){
										 parse=parse.replace("*", "(NP*");
										 NPinserted=true;
									}
						
						 }
						// Klammer zu
						
					

						else if ((NPinserted==true)&&(parse.contains(")"))){
							String parsecopy=parse;
							while (parsecopy.contains("(")){
								bracketcounter2auf++;
								parsecopy=parsecopy.replaceFirst("\\(","");
							}
							while (parsecopy.contains(")")){
								
								if (bracketcounter2auf==bracketcounter2zu){
									 parse=parse.replace("*", "*)");
									 secondPP=true;
									 bracketcounter2auf=0;
									 bracketcounter2zu=0;
									}
								bracketcounter2zu++;
								parsecopy=parsecopy.replaceFirst("\\)","");
								
								
							}		
						}
						
						//else: count brackets
						
						else {
							String parsecopy=parse;

							while (parsecopy.contains("(")){
								bracketcounter2auf++;
								parsecopy=parsecopy.replaceFirst("\\(","");
							}
							while (parsecopy.contains(")")){
								
								bracketcounter2zu++;
								parsecopy=parsecopy.replaceFirst("\\)","");

							}		
							
							
						}
								
						} //end: embedded PP
			
			//embedded PP
			if ((firstPP==false)&&(secondPP==true)&&(parse.contains("(PP*")&&(!parse.contains("(PP*)")))){
				
				secondPP=false;
				lineafter2=true;
				bracketcounter2auf=0;
				bracketcounter2zu=0;
				
			}
			
			//erste PP gefunden
			if ((firstPP==false)){
				
				
				String parsecopy=parse;
				while (parsecopy.contains("(")){
					bracketcounterauf++;
					parsecopy=parsecopy.replaceFirst("\\(","");
				}
				
				while (parsecopy.contains(")")){
					bracketcounterzu++;
					parsecopy=parsecopy.replaceFirst("\\)","");
					
					
					if (bracketcounterauf==bracketcounterzu){
						firstPP=true;bracketcounterauf=0; bracketcounterzu=0; secondPP=true;
						break;
					
					}
						
				}
		
		
		
		}

			//found first PP
			if (parse.contains("(PP*")&&(firstPP==true)&&(secondPP==true)&&(!parse.contains("(PP*)"))){
				firstPP=false;
				bracketcounterauf++; 
				lineafter=true;
				}
		
			

		//writertmp2.write(lines[i].replace(parseold, parse)+"\n");
		String[] columns=lines[i].split("\t");
		String linePart1=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t";
		String linePart2="\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
		writertmp2.write(linePart1+parse+linePart2+"\n");

		}
		
		sentence="";

	}

	else if  (line.contains("begin document")){
		writertmp2.write(line);}



	else {
		writertmp2.write("\n"+line+"\n");}
	}


	writertmp2.close();
	
	}
public static void PNadjust(BufferedReader reader) throws IOException {
	
	//Idea: 
	//deletes PN label if it is part of bigger NP
	

BufferedWriter writer = new BufferedWriter(new FileWriter("./tmp2.txt"));


String parse="";
String line="";
String sentence="";
String tag="";
String linenew="";
int counter=0;
String previoustag="";
String previousparse="";

while (reader.ready()) {
	
	line=reader.readLine();

	
	counter++;

if (line.contains("\t")){sentence=sentence+line+"\n";}

//Satz vorbei: Sentence prozessieren	
else if (line.equals("")){

	writer.write("\n");
	

	Boolean NP=false;
	Boolean PNdelete=false;
	Boolean PNdeletedouble=false;
	Integer bracketcounterauf=0;
	Integer bracketcounterzu=0;
	previoustag="";
	previousparse="";
	
	
	
	String lines[]=sentence.split("\n");
	for (int i=0;i<lines.length;i++){
		
		parse=lines[i].split("\t")[5];
		String parseold=parse;
		String parsenew=parse;
		tag=lines[i].split("\t")[4];
		String word=lines[i].split("\t")[3];
				
		previoustag=tag;
		previousparse=parse;
		
		if ((NP==true)&&((parse.contains("(PP")||(parse.contains("(S"))))){
			bracketcounterauf=0; bracketcounterzu=0; NP=false;
			
		}
		
		
		if (PNdelete==true){
						
			if (parse.contains(")")){
				parse=parse.replaceFirst("\\)","");
				PNdelete=false;
			}
		}
	if (PNdeletedouble==true){

			
			if (parse.contains(")")){
				parse=parse.replaceFirst("\\)","");
				parse=parse.replaceFirst("\\)","");
				PNdeletedouble=false;
			}
		}
		
	
		
		
		if (NP==true){
			
			if (parse.contains("(PN(NP*")){
				PNdeletedouble=true;
				parse=parse.replace("(PN(NP*","*");
				bracketcounterauf++;
				bracketcounterauf++;
				
			}
			
			else if ((parse.contains("(PN*"))&&(!parse.contains("(PN*)"))){
				PNdelete=true;
				parse=parse.replace("(PN*","*");
				bracketcounterauf++;
				
			}
			
			
			
			//(PN(NP auch abfangen
			
			String parsecopy=parse;
			while (parsecopy.contains("(")){
				bracketcounterauf++;
				parsecopy=parsecopy.replaceFirst("\\(","");
			}
			
			while (parsecopy.contains(")")){
				bracketcounterzu++;
				parsecopy=parsecopy.replaceFirst("\\)","");
				
				
				if (bracketcounterauf==bracketcounterzu){
					bracketcounterauf=0; bracketcounterzu=0; NP=false;
					break;
				
				}	
			}
			
			
			
			
			
		}
		
		if (((parse.contains("(NP*")&&(!parse.contains("(NP*)")))||((parse.contains("(PN*")&&(!parse.contains("(PN*)")))))){
			
			NP=true;
			bracketcounterzu=0;
			bracketcounterauf=0;
			bracketcounterauf++;
		}
		

		//writer.write(lines[i].replace(parseold, parse)+"\n");
		previoustag=tag;
		previousparse=parse;
		String[] columns=lines[i].split("\t");
		String linePart1=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t";
		String linePart2="\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
		writer.write(linePart1+parse+linePart2+"\n");

		
		
		}
	sentence="";

}

else if  (line.contains("begin document")){
	writer.write(line);}



else {
	writer.write("\n"+line+"\n");}
}


writer.close();
}



public static void CNPadjust(BufferedReader reader,BufferedWriter writer) throws IOException {


//IDEA: 

//insert NP labels into CNP parts

String parse="";
String line="";
String sentence="";
String tag="";
Integer counter=0;
String linenew="";
String previoustag="";
String previousparse="";

while (reader.ready()) {


	
	line=reader.readLine();

	
	counter++;

if (line.contains("\t")){sentence=sentence+line+"\n";}

//Satz vorbei: Sentence prozessieren	
else if (line.equals("")){

	writer.write("\n");
	

	Boolean CNP=false;
	Boolean CNPinserted=false;
	Boolean PNdeletedouble=false;
	Integer bracketcounterauf=0;
	Integer bracketcounterzu=0;
	Integer counter2=0;
	previoustag="";
	previousparse="";
	
	
	
	String lines[]=sentence.split("\n");
	for (int i=0;i<lines.length;i++){
		
		parse=lines[i].split("\t")[5];
		String parseold=parse;
		String parsenew=parse;
		tag=lines[i].split("\t")[4];
		String word=lines[i].split("\t")[3];
				
		previoustag=tag;
		previousparse=parse;
		

		if (CNP==true){
			counter2++;
		}
		
		if (counter2>2){counter2=0; CNP=false;}
		if ((CNP==true)&&(tag.contains("NN")||tag.contains("NE")&&counter2==2)){
			
			parse=parse.replace("*","(NP*)"); CNP=false; counter2=0;
		}
		
		
	
		if (parse.contains("(CNP*")&&(tag.contains("NN")||tag.contains("NE")||tag.contains("TRUNC"))){
			CNP=true;
			
		
			
			if (tag.contains("TRUNC")){}
			else {
				
				if (parse.endsWith("(CNP*")){
				parse=parse.replace("(CNP*","(CNP(NP*)");
				
			}}
			
		}
		
		
		
		
		
		

		//writer.write(lines[i].replace(parseold, parse)+"\n");
		previoustag=tag;
		previousparse=parse;
		String[] columns=lines[i].split("\t");
		String linePart1=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t";
		String linePart2="\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
		writer.write(linePart1+parse+linePart2+"\n");
		
		
		}
	sentence="";

}

else if  (line.contains("begin document")){
	writer.write(line);}



else {
	writer.write("\n"+line+"\n");}
}


writer.close();
}



public static void ADVadjust(BufferedReader reader) throws IOException {

//BufferedWriter writer = new BufferedWriter(new FileWriter("/projekte/sfb-732/a6/Coref/CorefResolver/datasets/final/training/tueba-train-ed8.conll"));
BufferedWriter writer = new BufferedWriter(new FileWriter("/projekte/sfb-732/a6/Coref/CorefResolver/datasets/final/dev/edits/tueba-dev-ed8.conll"));


String parse="";
String line="";
String sentence="";
String tag="";
Integer counter=0;
String linenew="";
String previoustag="";
String previousparse="";

while (reader.ready()) {
	
	line=reader.readLine();

	
	counter++;

if (line.contains("\t")){sentence=sentence+line+"\n";}

//Satz vorbei: Sentence prozessieren	
else if (line.equals("")){

	writer.write("\n");
	

	Boolean NPdeleted=false;
	Integer bracketcounterauf=0;
	Integer bracketcounterzu=0;
	Integer counter2=0;
	Integer counter3=0;

	previoustag="";
	previousparse="";
	String previousparseold="";
	String previouspreviousparse="";
	String previouspreviousparseold="";

	String changedline="";
	String previousline="";
	String previouspreviousline="";
	Boolean active=false;
	Boolean active2=false;
	String parseold="";
	
	String lines[]=sentence.split("\n");
	for (int i=0;i<lines.length;i++){
		
		parse=lines[i].split("\t")[5];
		parseold=parse;
		tag=lines[i].split("\t")[4];
		String word=lines[i].split("\t")[3];


		if (NPdeleted==true&& (tag.contains("NN")||tag.contains("NE")||tag.contains("APZR")||tag.contains("PDS")||tag.contains("PROAV")||tag.contains("ADJA")||tag.contains("ART")||tag.contains("CARD")||tag.contains("PIS")||tag.contains("PPER")||tag.contains("PPOSAT"))){
			
			parse=parse.replace("*", "(NP*");
			NPdeleted=false;
			active2=true;
		}
		
			
		
		
		if (parse.contains("(NP*")&&(!parse.contains(")"))&&(!parse.contains("AP"))&&(!parse.contains("CAVP"))&&tag.contains("ADV")&&(!word.equalsIgnoreCase("sehr")&&(!parse.contains("(NP*)")))){
			
			parse=parse.replace("(NP*","*");
			NPdeleted=true;
			changedline=lines[i];
			active=true;}
		
		if (active==true){counter3++;
		
		if (counter3==3){
			
			if (active2==true && active==true){
			writer.write(previouspreviousline.replace(previouspreviousparseold, previouspreviousparse)+"\n");
			writer.write(previousline.replace(previousparseold, previousparse)+"\n");
			writer.write(lines[i].replace(parseold, parse)+"\n");
			
			}
			
			else {
				
				writer.write(previouspreviousline+"\n");
				writer.write(previousline+"\n");
				writer.write(lines[i]+"\n");
			}
			
			counter3=0;
			active2=false;
			active=false;
		}
		
		}
		
		//writer.write(lines[i].replace(parseold, parse)+"\n");
		String columns[]=lines[i].split("\t");
		String linePart1=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t";
		String linePart2="\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
		writer.write(linePart1+parse+linePart2+"\n");

		previouspreviousparse=previousparse;
		previouspreviousparseold=previousparseold;
		
		previousparse=parse;
		previousparseold=parseold;


		previouspreviousline=previousline;
		previousline=lines[i];
		
		
		
		}
	
	if (NPdeleted==true){//System.out.println(changedline);
}
	
	sentence="";
	NPdeleted=false;
	
	

}

else if  (line.contains("begin document")){
	writer.write(line);}



else {
	writer.write("\n"+line+"\n");}
}


writer.close();
System.out.println("finished inserting NPs");
}



public static void InsertNPs(BufferedReader reader) throws IOException {

	//BufferedWriter writer = new BufferedWriter(new FileWriter("/projekte/sfb-732/a6/Coref/CorefResolver/datasets/final/training/tueba-train-ed4.conll"));

	BufferedWriter writer = new BufferedWriter(new FileWriter("./tmp.txt"));



String parse="";
String line="";
String sentence="";
String tag="";
String linenew="";
int counter=0;
String previoustag="";
String previousparse="";

while (reader.ready()) {
	
	line=reader.readLine();

	
	counter++;

if (line.contains("\t")){sentence=sentence+line+"\n";}

//Satz vorbei: Sentence prozessieren	
else if (line.equals("")){
	
	writer.write("\n");
	

	Boolean NP=false;
	Integer bracketcounterauf=0;
	Integer bracketcounterzu=0;
	previoustag="";
	previousparse="";
	

	
	String lines[]=sentence.split("\n");
	for (int i=0;i<lines.length;i++){
		
		parse=lines[i].split("\t")[5];
		String parseold=parse;
		String parsenew=parse;
		tag=lines[i].split("\t")[4];
		String word=lines[i].split("\t")[3];

		
	
		
		if (((parse.contains("(NP*")&&(!parse.contains("(NP*)")))||((parse.contains("(PN*")&&(!parse.contains("(PN*)")))))){
			
			NP=true;
			bracketcounterzu=0;
			bracketcounterauf=0;
			bracketcounterauf++;
		}
		
	
		
		
		if (NP==false){
			
				if (tag.equals("NE")||tag.equals("NN")){
				
				if (!previoustag.equals("ART")&&(!previoustag.startsWith("ADJ"))){
					
					if (parse.contains("(")&& parse.contains(")")){
						
						
					}
					
					else if (parse.contains("(")){
						String h1=parse.replace("*", "");
						parsenew=h1+"(NP*)";
						

					}
					
					else if (parse.contains(")")){
						String h1=parse.replace("*", "");
						parsenew="(NP*)"+h1;

					}
					
					else if (parse.contains("*")){
						String h1=parse.replace("*", "");
						parsenew="(NP*)"+h1;
					}

				}
		}
		}
		
		
		if (NP==true){
			
			String parsecopy=parse;
			while (parsecopy.contains("(")){
				bracketcounterauf++;
				parsecopy=parsecopy.replaceFirst("\\(","");
			}
			
			while (parsecopy.contains(")")){
				bracketcounterzu++;
				parsecopy=parsecopy.replaceFirst("\\)","");
				
				
				if (bracketcounterauf==bracketcounterzu){
					bracketcounterauf=0; bracketcounterzu=0; NP=false;
					break;
				
				}	
			}
		}

	//writer.write(lines[i].replace(parseold, parsenew)+"\n");
	String[] columns=lines[i].split("\t");
	String linePart1=columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+columns[3]+"\t"+columns[4]+"\t";
	String linePart2="\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+columns[9]+"\t"+columns[10];
	writer.write(linePart1+parsenew+linePart2+"\n");
	previoustag=tag;
	previousparse=parse;
	
	}
	
	sentence="";

}

else if  (line.contains("begin document")){
	writer.write(line);}



else {
	writer.write("\n"+line+"\n");}
}


writer.close();
}



}



		

