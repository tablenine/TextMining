package edu.yonsei.lexical_analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.njit.util.PorterStemmer;
import edu.njit.util.Stemmer;
import edu.njit.util.StopwordsEnglish;


public class SimpleCountingWordHandler {
	
	StopwordsEnglish stop = null;
	HashMap<String,Integer> frequencyMap = null;
	public SimpleCountingWordHandler()
	{
		frequencyMap = new HashMap();
		stop = new StopwordsEnglish("./data/stopwords");	
	}
	
	public void storeTerms(String document)
	{
		String[] splited = document.split(" ");
		
		for (int i = 0; i < splited.length; ++i) {
			String word = splited[i];	
			//System.out.println("Word: " + word);
			
			if (!frequencyMap.containsKey(word)) {
				frequencyMap.put(word, 1);
			} else {
				frequencyMap.put(word, frequencyMap.get(word)+1);
			}
		}
	}
	
	public void computeWordCount(String file, String delimiter) throws Exception
	{
		Stemmer stem = new PorterStemmer();
		FileInputStream fstream = new FileInputStream(file); 
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
		String test; 
				
		//Read File Line By Line
		while ((test = br.readLine()) != null)   { 
		    String[] tokens = test.split(delimiter);
		    for (int i = 0; i < tokens.length; ++i) { 
				String word = tokens[i]; 				
				word = word.replaceAll("[^\\p{L}\\p{N}]", ""); 
							
				if (!stop.isStopword(word) && !word.equals("")) { 
				      word = stem.stem(word); 
				      if (!frequencyMap.containsKey(word)) { 
					   frequencyMap.put(word, 1); 
				      } else { 
					   frequencyMap.put(word, frequencyMap.get(word)+1); 
				      }	
				}	
		    }
		}
	}
	
	public void writeToFile()
	{
		 try {
			 // Create file 
			 FileWriter fstream = new FileWriter("./output.txt");
			 BufferedWriter out = new BufferedWriter(fstream);
			  
			 for (Iterator iter = frequencyMap.entrySet().iterator(); iter.hasNext();) {
				 Map.Entry entry = (Map.Entry) iter.next();
			     String key = (String)entry.getKey();
			     Integer value = (Integer)entry.getValue(); 
			     out.write("Word: " + key + " Frequency: " + value + "\n");
			     System.out.println("Word: " + key + " Frequency: " + value);
			 }
	
			 //Close the output stream
			 out.close();
		 } catch (Exception e){//Catch exception if any
			 System.err.println("Error: " + e.getMessage());
		 }

	}
	
	public static void main(String args[]) throws Exception
	{
		String file = "./data/corpus/sample_text.txt";
		String delimiter = "\\s+";
		SimpleCountingWordHandler word_handler = new SimpleCountingWordHandler();
		word_handler.computeWordCount(file, delimiter);
		word_handler.writeToFile();
	}

	
	
}
