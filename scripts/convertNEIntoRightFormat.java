//if you already have a conll file with NE (but not in the right format for the resolver) this should do the trick
//specify if 0 or - for no NE!


import java.io.*;
import java.util.*;

public class convertNEIntoRightFormat {
	
	private static BufferedReader reader3;//CONLL file
		//ARG 2: writer	

	public static void main(String[] args){
		
		try {		
			
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%INPUT AND OUTPUT%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

			reader3 = new BufferedReader(new FileReader( new File(args[0]) ));
			BufferedReader reader4 = new BufferedReader(new FileReader( new File(args[0]) ));	
			BufferedWriter writer = new BufferedWriter(new FileWriter("./tmp.txt"));
			BufferedWriter writer2 = new BufferedWriter(new FileWriter("./tmp2.txt"));
			BufferedWriter writer3 = new BufferedWriter(new FileWriter(args[1]));
	
			String NE_negative="-"; //choose 0 otherwise
			String line="";
			String line2="";
			String line3="";
			Boolean active=false;
			String previousne="";
			Boolean inserted=false;
			int counter=1;
			String ne_previously="";
			
			while (reader3.ready()) {
			
				line=reader3.readLine();
			
				if (line.contains("\t")){
				String ne=line.split("\t")[9];
				
				if (ne.equals(NE_negative)&&active==true){active=false;writer.write(ne_previously.subSequence(0, ne_previously.length()-1)+")\n"+ne+"\n");ne_previously="";}
				
				else if (ne.equals(NE_negative)&&active==false){writer.write(ne+"\n");}
				
				
				else if (active==true&&!ne.equals(NE_negative)){ne_previously=ne_previously+ne+"\n";	}

				else if (active==false &&!ne.equals(NE_negative)){
					active=true;ne_previously="("+ne+"\n";}
					previousne=ne;
				}
				
				else {
					if (active==true){
						active=false; writer.write(ne_previously.subSequence(0, ne_previously.length()-1)+")\n"+line+"\n");ne_previously="";
					}
				
					else {writer.write(line+"\n");}
			}
			}
			
			writer.close();
			BufferedReader reader2 = new BufferedReader(new FileReader( new File("./tmp.txt") ));	

			while (reader2.ready()) {
				
				line=reader2.readLine();
				
				String ne=line;
				//System.out.println(ne);
			
				if (ne.equals(NE_negative)){ne="-";}
					
					else if (ne.contains(")")&&ne.contains("(")){ne=ne.replace(")","*)");}
					else if (ne.contains(")")&&!ne.contains("(")){
						ne="*)";
					}
					
					else if (ne.contains("(")&&!ne.contains(")")){
						ne=ne+"*";
					}
				
					else if (ne.equals("")){ne="-";}
					
					else if (!ne.equals(NE_negative)&&!ne.contains("(")&&!ne.contains(")")){
						ne="*";
					}
				
					else {ne="-";}

					writer2.write(ne+"\n");
				
				
				
		}	
			writer2.close();
			BufferedReader reader5 = new BufferedReader(new FileReader( new File("./tmp2.txt") ));	

			while (reader4.ready()) {
				
				
				line=reader4.readLine();
				String line_ne=reader5.readLine();
				
				if (line.contains("\t")){
				
					String[] columns=line.split("\t");
					
					writer3.write(columns[0]+"\t"+columns[1]+"\t"+columns[2] +"\t"+columns[3]+"\t"+columns[4]+"\t"+columns[5]+"\t"+columns[6]+"\t"+columns[7]+"\t"+columns[8]+"\t"+line_ne+"\t"+columns[10]+"\n");
					
				}
				
				else writer3.write(line+"\n");
				
		
			
			}
			
			writer3.close();
			System.out.println("finished");
			
			File filetmp = new File("./tmp.txt");
			filetmp.delete();
			File filetmp2 = new File("./tmp2.txt");
			filetmp2.delete();
		
			}
			catch(Exception e){}}}


