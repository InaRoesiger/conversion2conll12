import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/* File: replaceWithMeaningFulAntecedents
# Author: Ina Roesiger (roesigia@)
# Created: 2017-01-27
# Modified: 2017-01-27
# Purpose: Takes the extracted syntactic functions and coreference chains  as an input
 and replaces pronouns with common or proper NP antecedents (the head therof, to be specific)
 # Arguments: 1: Output of coreference system, 2: extracted info from parses *extracted  3: output-file 4: final-output-file
*/
public class replaceWithMeaningfulAntecedentXMLVersion {

	
	public static void main(String[] args) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader( new File(args[0])));
		BufferedReader functionReader = new BufferedReader(new FileReader( new File(args[1])));
		BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
		BufferedWriter finalWriter = new BufferedWriter(new FileWriter(args[3]));
		BufferedWriter finalfinalWriter = new BufferedWriter(new FileWriter(args[4]));


		//first part: make sure that the tokens are the same in the extracted file as well as in the conll file
		// * if they are: merge the two files and write as output 
		
		String conllLine="";
		String functionLine="";
		int corefCount=0;
		int wordCount=0;
		int functionCount=0;
		
		HashMap<Integer,Integer> wordsCoref=new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> wordsFunction=new HashMap<Integer,Integer>();

		while (reader.ready()){
			conllLine=reader.readLine();
			corefCount++;
			

			if (conllLine.contains("\t")){
				wordCount++;
				String currentToken=conllLine.split("\t")[3];
				
				functionLine=functionReader.readLine();
				functionCount++;
				if (functionLine.contains("\t")){
					String searchToken=functionLine.split("\t")[0];
					
					if (currentToken.replace("$SPACE$"," ").replaceAll(" +", " ").replace("_", "-").equals(searchToken.replace("$SPACE$"," ").replaceAll(" +", " ").replace("_", "-"))){
						wordsCoref.put(wordCount, corefCount);
						wordsFunction.put(wordCount, functionCount);
						
						writer.write(conllLine+"\t"+functionLine+"\n");

						
					}
					
					else {	
						System.out.println("error while merging");
					}
					
				}
				
				else {
					
					while (!functionLine.contains("\t")){
						functionLine=functionReader.readLine();
						functionCount++;
						if (functionLine.contains("\t")){
							String searchToken=functionLine.split("\t")[0]; 
							
							if (currentToken.replace("$SPACE$"," ").replaceAll(" +", " ").replace("_", "-").equals(searchToken.replace("$SPACE$"," ").replaceAll(" +", " ").replace("_", "-"))){
								
								wordsCoref.put(wordCount, corefCount);
								wordsFunction.put(wordCount, functionCount);
								writer.write(conllLine+"\t"+functionLine+"\n");
	
							}
							
							else {
								System.out.println("error while merging");
								
							}
							
						}
						
				}
				
				
			}
			}
			else {writer.write(conllLine+"\n");}
		}
		
		System.out.println(wordsFunction.size());
		System.out.println(wordsCoref.size());
		writer.close();
		
		if (wordsFunction.size()!=wordsCoref.size()){
			System.out.println("something's wrong, both files do not have the same number of tokens ...");
		}
		
		
		

		//list of vague expressions that you would like to have replaced
		ArrayList<String> vague = new ArrayList<String>();
		vague.add("Teil"); vague.add("Gerät"); vague.add("Ding"); vague.add("Maschine");
		
		int wordCounter=0;
		int wordCounterCoref=0;
		int wordCounter2=0;
		
		HashMap<String,String> corefChains=new HashMap<String,String>();
		
		
		//save sentences from CONLL file in ArrayList
		BufferedReader CorefChainsreader = new BufferedReader(new FileReader( new File(args[2]))); //was 0
		
		ArrayList<String> sentences=new ArrayList<String>();
		
		String sentence="";
		while (CorefChainsreader.ready()){
			String tmp2=CorefChainsreader.readLine();
			
			if (tmp2.contains("\t")){
				sentence=sentence+tmp2+"\n";
			}
			
			else if (tmp2.equals("")){
				if (sentence.endsWith("\n")){
					sentence=sentence.substring(0, sentence.length()-1);
				}
				sentences.add(sentence);
				sentence="";
			}
			
			else if (tmp2.contains("#begin document")||tmp2.contains("#end document")){
				sentences.add(tmp2);
			}
		}
		

		//saving coref chains in format <chain-id, linenumbermention1, linenumbermention2, ..., multi-word-mention>
		Boolean active=false;
		String multiWord="";
		int docCounter=0;
		
		for (int o=0;o<sentences.size();o++){
			String tmp2=sentences.get(o);
			if (tmp2.contains("#begin document")){
				docCounter++;
			}
						
			if (tmp2.contains("\t")){
				
				//ArrayList<String> ids=new ArrayList<String>();
				HashSet<String> ids=new HashSet<String>();

				//more than one word in sentence
				if (tmp2.contains("\n")){
					
					String[] lines=tmp2.split("\n");

					//for every line in one sentence: do
					for (int p=0;p<lines.length;p++){
						String currentLine=lines[p];
						
						String coref=currentLine.split("\t")[13]; //was String coref=currentLine.split("\t")[currentLine.split("\t").length-1]; 

						//String word=currentLine.split("\t")[3];
						//String pos=currentLine.split("\t")[4];
						
						//| separated multiple mentions
						if (coref.contains("|")){
							
							String[] multipleMentions=coref.split("\\|");
							for (int z=0;z<multipleMentions.length;z++){
								if (multipleMentions[z].contains("(")){
									ids.add(multipleMentions[z].replace("(", "").replace(")",""));
								}
							}
							
						}
						//only one mention
						else {
							
							//start
							if (coref.contains("(")){
								ids.add(coref.replace("(", "").replace(")",""));
							}
							
						}
						
						
					}
					
					
					
					
				}
				//only one word 
				
				else {
					String coref=tmp2.split("\t")[tmp2.split("\t").length-1];
					//String word=tmp2.split("\t")[3];
					//String pos=tmp2.split("\t")[4];
					
					if (coref.contains("(")){
						ids.add(coref.replace("(", "").replace(")",""));
					}
					
				}
				
				
				//now that we have all the ids that occur in the sentence, 
				//we need to go through the sentence for every id and save the mention in the hashmap
				
				//for every id
				for (String s : ids) {
					
					String id=s;
					active=false;
					String lastword="";

					//more than one word in sentence
					if (tmp2.contains("\n")){
						String[] lines=tmp2.split("\n");
						
						//for every line in one sentence: do
						for (int p=0;p<lines.length;p++){
							String currentLine=lines[p];
							String coref=currentLine.split("\t")[13]; //							String coref=currentLine.split("\t")[currentLine.split("\t").length-1]; //

							String word=currentLine.split("\t")[16]; //used to be 3 for wordform, 6 for lemma
							String pos=currentLine.split("\t")[4];
							
							//multiple mentions
							if (coref.contains("|")){
								
								String[] multipleMentions=coref.split("\\|");
								for (int z=0;z<multipleMentions.length;z++){
									
										String coref2=multipleMentions[z];
										String tmp=multipleMentions[z].replace("(","").replace(")","");
										
										
										if (tmp.equals(id)||active==true){
										
											//single word mention
											if (coref2.contains("(")&&coref2.contains(")")){
												
												String idForCorefChain=docCounter+"-"+tmp;
												
												//already contained
												if (corefChains.containsKey(idForCorefChain)){
													String value=corefChains.get(idForCorefChain);
													value=value+","+word+"$%$"+pos;
													corefChains.put(idForCorefChain,value);
												}
												//not yet
												else {
													corefChains.put(idForCorefChain,word+"$%$"+pos);
												}
												//active with embedded
											if (active){
												multiWord=multiWord+"-"+word+"$%$"+pos;
											}
												
											}
											
											//multi-word mention
											
											//start
											
											else if (coref2.contains("(")&&active==false){
												coref2=coref2.replace("(", "");
												active=true;
												multiWord=word+"$%$"+pos;
											}
											
											else if (coref2.contains("(")&&active==true){
												multiWord=multiWord+"-"+word+"$%$"+pos;
											}
											//middle	
											else if (coref2.contains("-")){
												multiWord=multiWord+"-"+word+"$%$"+pos;
											}
											//end
											
											else if (coref2.contains(")")){
												
												
										
												coref2=coref2.replace(")", "");
												//only if it is the right end
												if (coref2.equals(id)){
													
												if (lastword.equals(word)){
													
												}
												else {
												multiWord=multiWord+"-"+word+"$%$"+pos;
												}
												active=false;
												if (multiWord.startsWith("-")){
													multiWord.replaceFirst("-", "");
												}
												if (multiWord.endsWith("-")){
													multiWord=multiWord.substring(0, multiWord.length()-1);
												}
												
												
												//check if already contained
												String idForCorefChain=docCounter+"-"+coref2;
												
												//already contained
												if (corefChains.containsKey(idForCorefChain)){
													String value=corefChains.get(idForCorefChain);
													value=value+","+multiWord;
													corefChains.put(idForCorefChain,value);
												}
												//not yet
												else {		
													corefChains.put(idForCorefChain,multiWord);
												}	
												}
												
												else {lastword=word;
													multiWord=multiWord+"-"+word+"$%$"+pos;}
												
											}
											}
									
							
										}
							}
							
							//only one mention
							String tmp=coref.replace("(","").replace(")","");
							
							if (tmp.equals(id)||active==true){
							
							//single word mention
							if (coref.contains("(")&&coref.contains(")")){
								
								String idForCorefChain=docCounter+"-"+tmp;
								
								//already contained
								if (corefChains.containsKey(idForCorefChain)){
									String value=corefChains.get(idForCorefChain);
									value=value+","+word+"$%$"+pos;
									corefChains.put(idForCorefChain,value);
								}
								//not yet
								else {
									corefChains.put(idForCorefChain,word+"$%$"+pos);
								}
								
							}
							
							//multi-word mention
							
							//start of current entity
							
							else if (coref.contains("(")&&active==false){
								coref=coref.replace("(", "");
								active=true;
								multiWord=word+"$%$"+pos;
							}
							//start of another entity
							else if (coref.contains("(")&&active==true){
								coref=coref.replace("(", "");
								multiWord=multiWord+"-"+word+"$%$"+pos;
							}
							//middle	
							else if (coref.contains("-")){
								multiWord=multiWord+"-"+word+"$%$"+pos;
							}
							
							//end
							
							else if (coref.contains(")")){
								
								
								coref=coref.replace(")", "");
								
								if (coref.equals(id)){
									
								multiWord=multiWord+"-"+word+"$%$"+pos;
								active=false;
								if (multiWord.startsWith("-")){
									multiWord.replaceFirst("-", "");
								}
								if (multiWord.endsWith("-")){
									multiWord=multiWord.substring(0, multiWord.length()-1);
								}
								
								
								//check if already contained
								String idForCorefChain=docCounter+"-"+coref;
								
								//already contained
								if (corefChains.containsKey(idForCorefChain)){
									String value=corefChains.get(idForCorefChain);
									value=value+","+multiWord;
									corefChains.put(idForCorefChain,value);
								}
								//not yet
								else {		
									corefChains.put(idForCorefChain,multiWord);
									}	
								}
								//end of another entity 
								else {multiWord=multiWord+"-"+word+"$%$"+pos;
	
								}
							}
						
							}
							
							
							}
							
							//END FOR EVERY LINE DO
							
							
							
							
						
						
					}
					//only one word
					else {
						
						
						String currentLine=tmp2;
						String coref=currentLine.split("\t")[currentLine.split("\t").length-1];
						String word=currentLine.split("\t")[6];//used to be 3 for wordform
						String pos=currentLine.split("\t")[4];
						
						String tmp=coref.replace("(","").replace(")","");
						
						if (tmp.equals(id)){
						
						//single word mention
						if (coref.contains("(")&&coref.contains(")")){
							
							String idForCorefChain=docCounter+"-"+tmp;
							
							//already contained
							if (corefChains.containsKey(idForCorefChain)){
								String value=corefChains.get(idForCorefChain);
								value=value+","+word+"$%$"+pos;
								corefChains.put(idForCorefChain,value);
							}
							//not yet
							else {
								corefChains.put(idForCorefChain,word+"$%$"+pos);
							}
							
						}
						}
						
						
						
					}
				}
				
			}
			
			
		}

		
		    Iterator it = corefChains.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		    //    System.out.println(pair.getKey() + " = " + pair.getValue());
		    }
				
		
	
		BufferedReader mergedReader = new BufferedReader(new FileReader( new File(args[2])));

		//read syntactic functions
		String text="";
		docCounter=0;
		while (mergedReader.ready()){
			text=mergedReader.readLine();
			
			if (text.contains("#begin document")){
				docCounter++;
			}
			
			else if (text.equals("")){
				finalWriter.write(text+"\n");
			}
			//words and annotations contain tabs, sentence boundaries not
			else if (text.contains("\t")){
				
				String[] split=text.split("\t");
				//MULTEXT TAGS: personal pronouns: Pp*, possessive pronouns: Pp* , relative pronouns Pr*
				String posTag=text.split("\t")[4];
				String word=split[3]; //length-3
				String lemma=split[13]; //length-3
				String wordNo=split[2]; //length-4
				String id=text.split("\t")[text.split("\t").length-4];
				String cparse=split[5];
				String function=text.split("\t")[text.split("\t").length-5];
				String coref=text.split("\t")[text.split("\t").length-11].replace("(","").replace(")","").replace("|","");
				//found a pronoun that has a syntactic function
				if ((posTag.startsWith("PPER")|posTag.startsWith("PRELS")||posTag.startsWith("PPOSAT")||posTag.startsWith("PDS")||vague.contains(lemma))&&!function.equals("NULL")&&!coref.equals("-")&&!word.equals("Sie")&&!word.equals("Ich")&&!word.equals("Ihr")&&!word.equals("ich")&&!word.equals("wir")&&!word.equals("Wir")&&!word.equals("uns")){
								
								
								//check if it is a part of a coref chain
								
									
									//if so: find closest non-pronominal NP with same coref ID 
									//pronouns are always single-word mentions
									coref=docCounter+"-"+coref;
									String headOfAntecedent="";
									
									if (corefChains.containsKey(coref)){
									String antecedents=corefChains.get(coref);
									

									
									if (antecedents.contains(",")){
										String[] splits=antecedents.split(",");
										
										//multiple antecedents
										//one could make a frequency distribution here and choose the most frequent instead of the left-most/right-most
										for (int i=0;i<splits.length;i++){
											
											//take right-most multi-word antecedent that is a multi-word
											
											if (splits[i].contains("-")){
												
												String[] splitted=splits[i].split("-");
												
												for (int u=0;u<splitted.length;u++){
													
													if (!splitted[u].equals("")&&splitted[u].contains("$%$")){
														String pos=splitted[u].split("\\$%\\$")[1];
													
														if (pos.equals("NN")||pos.equals("NE")){
															headOfAntecedent=splitted[u].split("\\$%\\$")[0];
															
														
													}
												}
											}
											}
											//if head of antecedent not yet found: take single one but only if not a pronoun
											else if (headOfAntecedent.equals("")){
												
												if (splits[i].contains("$%$")){
												if (!splits[i].split("\\$%\\$")[1].equals("PPOSAT")&&!splits[i].split("\\$%\\$")[1].equals("PRELS")&&!splits[i].split("\\$%\\$")[1].equals("PPER")&&!splits[i].split("\\$%\\$")[1].equals("PRF")){
											
												headOfAntecedent=splits[i].split("\\$%\\$")[0];
											}}}
											
											
											
										}
									}
									//only one antecedent
									else {
										
										//reduce to head
										
										//multi-word: search for leftmost NN or NE
										if (antecedents.contains("-")){
											
											String[] splitted=antecedents.split("-");
											
											for (int u=0;u<splitted.length;u++){
												String pos=splitted[u].split("\\$%\\$")[1];
												
												if (pos.equals("NN")||pos.equals("NE")){
													headOfAntecedent=splitted[u].split("\\$%\\$")[0];
													break;
												}
											}
										}
										//single-word but only if not a pronoun itself
										else if (!antecedents.split("\\$%\\$")[1].equals("PPER")&& !antecedents.split("\\$%\\$")[1].equals("PPOSAT")&&!antecedents.split("\\$%\\$")[1].equals("PRELS")&&!antecedents.split("\\$%\\$")[1].equals("PRF")){
											headOfAntecedent=antecedents;
										}
									}
									}
									
									//for all other cases: column should be NULL
									if (headOfAntecedent.equals("")){
										headOfAntecedent="NULL";
									}
									//System.out.println(headOfAntecedent);
									
									finalWriter.write(wordNo+"\t"+word+"\t"+function+"\t"+id+"\t"+headOfAntecedent+"\t"+cparse+"\n");
								}
				
				else {
					
					
					
					finalWriter.write(wordNo+"\t"+word+"\t"+function+"\t"+id+"\t"+"NULL"+"\t"+cparse+"\n");}
								
				
							}
						}
					finalWriter.close();
					
					BufferedReader finalReader = new BufferedReader(new FileReader( new File(args[3]))); //reader: wordNo word function id antecedent cparse
					BufferedReader functionReader2 = new BufferedReader(new FileReader( new File(args[1]))); //XML file
					
					

					
					//merging step: merge newly created annotations and XML file
					
					while (finalReader.ready()){
						
						conllLine=finalReader.readLine();
						
						if (!conllLine.equals("")){
							functionLine=functionReader2.readLine();
							String currentToken=conllLine.split("\t")[1];
							String ante=conllLine.split("\t")[4];
							String cparse=conllLine.split("\t")[5];
							if (conllLine.contains("\t")&&functionLine.contains("\t")){
								String searchToken=functionLine.split("\t")[0];
								
								if (currentToken.replace("$SPACE$"," ").replaceAll(" +", " ").replace("_", "-").equals(searchToken.replaceAll(" +", " ").replace("_", "-"))){
									finalfinalWriter.write(functionLine+"\t"+ante+"\t"+cparse+"\n");		
								}
								
								else {	
									
									
									
									System.out.println("error while merging");
								}
								
						}
							
						else if (conllLine.contains("\t")&&!functionLine.contains("\t")){
							
							finalfinalWriter.write(functionLine+"\n");
								
								while (!functionLine.contains("\t")){
									functionLine=functionReader2.readLine();
									if (functionLine.contains("\t")){
										String searchToken=functionLine.split("\t")[0]; 
										
										if (currentToken.replace("$SPACE$"," ").replaceAll(" +", " ").replace("_", "-").equals(searchToken.replaceAll(" +", " ").replace("_", "-"))){
											finalfinalWriter.write(functionLine+"\t"+ante+"\t"+cparse+"\n");
										}								
										else {
											System.out.println("error while merging");
										}
										
									}
									else {
										finalfinalWriter.write(functionLine+"\n");
										}
								}
						}
						
						else if (!conllLine.contains("\t")&&!functionLine.contains("\t")){
							finalfinalWriter.write(functionLine+"\n");
						
						}}
					}
					
					while (functionReader2.ready()){
						functionLine=functionReader2.readLine();
						finalfinalWriter.write(functionLine+"\n");
					}
					
					
/*					while (finalReader.ready()){
						conllLine=finalReader.readLine();
						corefCount++;

						if (conllLine.contains("\t")){
							wordCount++;
							String currentToken=conllLine.split("\t")[1];
							String ante=conllLine.split("\t")[4];
							functionLine=functionReader2.readLine();
							functionCount++;
							if (functionLine.contains("\t")){
								String searchToken=functionLine.split("\t")[0];
								
								if (currentToken.equals(searchToken)){
									
									finalfinalWriter.write(functionLine+"\t"+ante+"\n");		
								}
								
								else {	finalfinalWriter.write(functionLine+"\n");
								}
								
							}
							
							else {
								
								while (!functionLine.contains("\t")){
									if (functionLine.contains("\t")){
										String searchToken=functionLine.split("\t")[0]; 
										
										if (currentToken.equals(searchToken)){
											
											finalfinalWriter.write(functionLine+"\t"+ante+"\n");
				
										}
										
										else {
											finalfinalWriter.write(functionLine+"\n");
										}
										
									}
									else {
										finalfinalWriter.write(functionLine+"\n");}
								
									functionLine=functionReader2.readLine();

								}
							
							
						}
						}
						
					}*/
					
					finalfinalWriter.close();


	}
	
	
} 
