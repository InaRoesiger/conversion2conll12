
import java.io.*;
import java.util.*;

public class catTokensIntoSentencePerLine {
	
	
	public static void main(String[] args){
		
		try {
		
			
			BufferedReader reader = new BufferedReader(new FileReader( new File(args[0])));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[1])));

			String text="";
			
			String sentence="";
			while (reader.ready()) {
				text=reader.readLine();
				
				if (text.equals("")){		
					writer.write(sentence+"\n");
					sentence="";
				}
				else {
					sentence=sentence+" "+text;
				}
				
			}	
			
			if (!sentence.equals("")){
				writer.write(sentence+"\n");	
			}			
			writer.close();	
			
	}
		

		catch(Exception e){
		}
	}
	
	
	}
