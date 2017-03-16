//CLASS TO MERGE ALL REQUIRED PARTS FOR CONLL

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class mergeForCoNLL {
	
	private static BufferedReader reader; //mate morphtagged file
	private static BufferedReader reader2;//parse
	private static BufferedReader reader3;//ne
	private static BufferedReader reader4; //mate lemma file
	private static BufferedWriter writer;

	public static void main(String[] args){
		
		try {		
			
			reader = new BufferedReader(new FileReader( new File(args[0]) ));	
			reader2 = new BufferedReader(new FileReader( new File(args[1]) ));	
			reader3 = new BufferedReader(new FileReader( new File(args[2]) ));	
			reader4 = new BufferedReader(new FileReader( new File(args[3]) ));	
			writer = new BufferedWriter(new FileWriter(args[4]));
			
			int parseCounter=0;
			int counter=0;
			String text="";
			String text2="";
			String text3="";
			String text4="";
			Boolean active=false;

			String docname=args[0].replace(".morph","").replace("./../output/","");
		
			writer.write("#begin document ("+docname+"); part 000\n");
			
			while (reader.ready()) {
				
				if (active){
					text=reader.readLine();
					text3=reader3.readLine();
					text4=reader4.readLine();
					counter++;

					active=false;
				}
				else {
				parseCounter++;
				text=reader.readLine();
				text2=reader2.readLine();
				text3=reader3.readLine();
				text4=reader4.readLine();
				counter++;
	
				if (text.contains("\t")){
				String parse="";
				String pos="";
				String word="";
				
				String no=text.split("\t")[0];
				String wordForm=text.split("\t")[1];
				wordForm=wordForm.replace("  "," ");
				String originalWordForm=wordForm;
				String lemma=text4.split("\t")[3];
				String gend=text.split("\t")[7];
				String num=text.split("\t")[7];
				String ne=text3.split("\t")[2];
				
				if (text2.contains("\t")){
					parse= text2.split("\t")[2];
					pos= text2.split("\t")[1];
					word=text2.split("\t")[0];
				
					//same word basis
					if (!word.equals(wordForm)){
					
						int count = StringUtils.countMatches(wordForm.replace("  ","")," ");
						for (int i=0;i<count;i++){
							text2=reader2.readLine();
							parseCounter++;
							if (text2.contains("\t")){
							word=word+" "+text2.split("\t")[0];
							parse=parse.replace("*", text2.split("\t")[2]);
							}
							else {
								active=true;
								break; 
							}
							
						}
						
						if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
							text2=reader2.readLine();
							parseCounter++;
							if (text2.contains("\t")){
							word=word+" "+text2.split("\t")[0];
							parse=parse.replace("*", text2.split("\t")[2]);
							}
							else {
								active=true;
								break; 
							}
							if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
								text2=reader2.readLine();
								parseCounter++;
								if (text2.contains("\t")){
								word=word+" "+text2.split("\t")[0];
								parse=parse.replace("*", text2.split("\t")[2]);
								}
								else {
									active=true;
									break; 
								}
								if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
									text2=reader2.readLine();
									parseCounter++;
									if (text2.contains("\t")){
									word=word+" "+text2.split("\t")[0];
									parse=parse.replace("*", text2.split("\t")[2]);
									}
									else {
										active=true;
										break; 
									}
									if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
										text2=reader2.readLine();
										parseCounter++;
										if (text2.contains("\t")){
										word=word+" "+text2.split("\t")[0];
										parse=parse.replace("*", text2.split("\t")[2]);
										}
										else {
											active=true;
											break; 
										}
										if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
											text2=reader2.readLine();
											parseCounter++;
											if (text2.contains("\t")){
											word=word+" "+text2.split("\t")[0];
											parse=parse.replace("*", text2.split("\t")[2]);
											}
											else {
												active=true;
												break; 
											}
											if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
												text2=reader2.readLine();
												parseCounter++;
												if (text2.contains("\t")){
												word=word+" "+text2.split("\t")[0];
												parse=parse.replace("*", text2.split("\t")[2]);
												}
												else {
													active=true;
													break; 
												}
												if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
													text2=reader2.readLine();
													parseCounter++;
													if (text2.contains("\t")){
													word=word+" "+text2.split("\t")[0];
													parse=parse.replace("*", text2.split("\t")[2]);
													}
													else {
														active=true;
														break; 
													}
													if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
														text2=reader2.readLine();
														parseCounter++;
														if (text2.contains("\t")){
														word=word+" "+text2.split("\t")[0];
														parse=parse.replace("*", text2.split("\t")[2]);
														}
														else {
															active=true;
															break; 
														}
														if (!word.replaceAll(" +", " ").equalsIgnoreCase(wordForm.replaceAll(" +", " "))){
														System.out.println("error at "+ counter+" "+parseCounter);
														}
													}
												}
											}
										}
									}
								}
							}
							}
						
					/*	
						while (wordForm.contains(" ")){
							text2=reader2.readLine();
											
							if (text2.contains("\t")){
							parse=parse.replace("*", text2.split("\t")[2]);
							wordForm=wordForm.replaceFirst(" ","");
							}
							else {
								active=true;
								break; 
							}
						}*/
					}
					}
				else {
					parse="((VROOT*))";
					pos="NN";
				}
				
				
			

				//NUMBER
				
					if (num.contains("sg")){
						num="sg";	
					}
					else if (num.contains("pl")){
						num="pl";
					}
					else {num="-";}
				
				
			
			//GENDER
				if (gend.contains("masc")){gend="masc";
				}
				else if (gend.contains("fem")){gend="fem";
				}
				else if (gend.contains("neut")){gend="neut";
				}	
				else {gend="-";}
		
			//LEMMA
			if (lemma.contains("unknown")){lemma=wordForm;}
			if (lemma.equals("--")){lemma=wordForm;}	

			//NER

			if (ne.startsWith("I-")){ne=ne.replace("I-","");}
			if (ne.equals("O")){ne="-";}
				System.out.println(counter);
				writer.write(docname+"\t"+"000\t"+no+"\t"+originalWordForm+"\t"+pos+"\t"+parse+"\t"+lemma+"\t"+num+"\t"+gend+"\t"+ne+"\t"+"-\n");
				}
				
			else {
				writer.write(text+"\n");
	
			}
			
				
			}}
			
			writer.write("#end document" +docname);
			
			writer.close();
			
	
			
			}
			catch(Exception e){}}}


