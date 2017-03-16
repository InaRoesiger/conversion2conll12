
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

//Extracts tokens as listed in bc.annotated (final corpus version)

public class extractTokens {

public static void main(String[] args){

	try {
		
		BufferedReader reader = new BufferedReader(new FileReader( new File(args[0]))); //input file: bc.annotaed
		BufferedWriter writer = new BufferedWriter(new FileWriter( new File(args[1]))); //output

		String text="";
		
		while (reader.ready()){
			text=reader.readLine();
			
			if (!text.startsWith("<")&&!text.startsWith(">")&&text.contains("\t")){
				writer.write(text.split("\t")[0]+"\n");
			}
			else if (text.equals("</s>")){
				writer.write("\n");
			}
		}
		writer.close();
	}


catch(Exception e){
}
}
}


