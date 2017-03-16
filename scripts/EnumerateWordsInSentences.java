import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EnumerateWordsInSentences{
   
   public static void main(String[] args) throws IOException {
	   
	   
		try {		
			
		BufferedReader reader = new BufferedReader(new FileReader( new File(args[0]) ));	
		BufferedWriter writer = new BufferedWriter(new FileWriter( new File(args[1])));

		String text="";
		
	 	int counter=0;
		while (reader.ready()) {
			
			text=reader.readLine();
			
			if (text.equals("")){
				counter=0;writer.write("\n");}
		
			else {
			
				counter++;
				writer.write(counter+"\t"+text+"\n");
				
			}
			
		}
		
		writer.close();
			
			
}



catch(Exception e){
}
}


}


