
//Input 1: bc.final with meta tags in separate column, file should include s tags
//Input 2: conll12 file
//Output: conll12 file with split documents where meta tag changes

import java.io.*;
import java.util.*;

public class splitDocumentsAccordingToParagraph {
	
	
	public static void main(String[] args){
		
		try {
		
			
			BufferedReader reader = new BufferedReader(new FileReader( new File(args[0]))); // bc-final with metatags
			BufferedReader reader2 = new BufferedReader(new FileReader( new File(args[1]))); //conll

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[2])));

			

			
			String text="";
			String text2="";
			int counter=0;
			int docCounter=0;
			String sentence="";
			String oldmeta="";
			String meta="";
			int lineCounter=0;
			int splitCounter=0;

			while (reader.ready()) {
				text=reader.readLine();
				counter++;
				
				System.out.println(counter);
				
				//found paragraph marker
				if (text.equals("<p>")){
					if (docCounter==0){
						writer.write("#begin document ("+docCounter+"); part 000\n"); 
						docCounter++;

					}
					else {
					writer.write("#begin document ("+docCounter+"); part 000"); 
					docCounter++;
					lineCounter=0;
					splitCounter=0;
					}
				}
				
				//found end of paragraph marker 
				else if (text.equals("</p>")){
					writer.write("\n#end document\n"); 
					
				}
				//normal text lines

				else if (text.contains("\t")){
					String word2=text.split("\t")[0]; //word in bc.annotated
					lineCounter++;

					if (reader2.ready()){
						text2=reader2.readLine();
						if (text2.contains("\t")){
								String word=text2.split("\t")[3]; //word in CoNLL
								
								if (word2.replaceAll(" +", " ").equalsIgnoreCase(word.replaceAll(" +", " "))||(word2.replaceAll(" +", " ").replace("_", "-").equals(word.replaceAll(" +", " ").replace("_", "-")))){
									writer.write(text2.replace(" ","$SPACE$").replace("(---CJ","(CJ")+"\n");

								}
								else {
								System.out.println("mismatch at "+counter);
								
								}
						}
						//empty line
						else if (text2.equals("")){
							writer.write("\n");
							text2=reader2.readLine();
							if (lineCounter>4000){
								splitCounter++;
								lineCounter=0;
								writer.write("#end document\n#begin document ("+docCounter+"-"+splitCounter+"); part 000"+"\n"); 	
							}


							if (text2.contains("\t")){
								String word=text2.split("\t")[3]; //word in CoNLL
								
								if (word2.replaceAll(" +", " ").equalsIgnoreCase(word.replaceAll(" +", " "))||(word2.replaceAll(" +", " ").replace("_", "-").equals(word.replaceAll(" +", " ").replace("_", "-")))){

									writer.write(text2.replace(" ","$SPACE$").replace("(---CJ","(CJ")+"\n");

								}
								else {
								System.out.println("mismatch at "+counter);
								}
							}
							
						}
					}
				}
			}	
			
			writer.close();
			
			
			
	}
		

		catch(Exception e){
		}
	}
	
	
	}
