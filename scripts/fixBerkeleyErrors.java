
import java.io.*;
import java.util.*;


//replace lines with no tab with word + tab + NN + tab + ((VROOT*))
public class fixBerkeleyErrors {
	
	
	public static void main(String[] args){
		
		try {
		
			
			BufferedReader reader = new BufferedReader(new FileReader( new File(args[0])));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[2])));

			String text="";
			int counter=0;
			String sentence="";
			while (reader.ready()) {
				text=reader.readLine();

				if (!text.contains("\t")){
				
					
				}
				
				else writer.write(text+"\n");
				
			}
			
			writer.close();
			
			
			
	}
		

		catch(Exception e){
		}
	}
	
	
	}
