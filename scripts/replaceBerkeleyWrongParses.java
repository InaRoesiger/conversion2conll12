
import java.io.*;
import java.util.*;

public class replaceBerkeleyWrongParses {
	
	
	public static void main(String[] args){
		
		try {
		
			
			BufferedReader reader = new BufferedReader(new FileReader( new File(args[0])));
			BufferedReader reader2 = new BufferedReader(new FileReader( new File(args[1])));

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[2])));

			
			String text="";
			String text2="";
			int counter=0;
			String sentence="";
			while (reader.ready()) {
				text=reader.readLine();
				text2=reader2.readLine();
				String parserOutput="( (PSEUDO"; //( (PSEUDO (NE Puppenhaus) ($. .)) )

				
				if (text.equals("(())")){
					
					String[] split=text2.split(" ");
					
					for (int i=1;i<split.length;i++){
						parserOutput=parserOutput+" (NN "+split[i]+")";
					}
					counter++;
					parserOutput=parserOutput+") )";
					writer.write(parserOutput+"\n");
				}
				
				else writer.write(text+"\n");
				
			}
			
			writer.close();
			
			
			
	}
		

		catch(Exception e){
		}
	}
	
	
	}
