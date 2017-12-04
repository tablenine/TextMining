package edu.yonsei.test.main;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Sentence;

/**
 * @author Reinald Kim Amplayo & Min Song
 */
public class temp1 {
	
	public static void main(String[] args) throws Exception {
		String year = "2014";
		if (year.length() > 4) {
			year = year.substring(0, 4);
		}
		
		String year1 = "2014- 12";
		if (year1.length() > 4) {
			year1 = year1.substring(0, 4).trim();
		}
		
		System.out.println(year + " : " + year1 + ":");
		
	//	Scanner s = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
     	Scanner s = new Scanner(new FileReader("1016.txt"));	
		Vector<String> list = new Vector<String>();
		int count = 0;
		while(s.hasNext()) {
		//	s.nextLine();
	    //s.nextLine();
			String text = "";
			while(s.hasNext()) {
				String line = s.nextLine();
				if(line.isEmpty()) break;
				text += line + " ";
			}
			list.add(text);
			count++;
			if(count == 10) break;
		}
		s.close();
		
		
		
		
		//String mode = "en";
		String mode = "ko";
		boolean isKomoran = true;
		System.out.println("starting sentiment analysis");
		Collection collection = new Collection(list);
    	Pipeline pipe = null;
    	if (mode.equals("en")) {
    		pipe = new Pipeline();
    	} 	
		for(int i=0; i<collection.size(); i++) {
			Document document = collection.get(i);
			document.preprocess(mode, isKomoran, pipe);
			
			System.out.println("Document " + collection.get(0));
			double total_score =0.0;
			double avg_score =0.0;
			for(int j=0; j<document.size(); j++) {
				Sentence sentence = document.get(j);
				System.out.println("Sentence: " + sentence.getSentence());
			//	System.out.println("Sentiment (SentiWordNet): " + sentence.getSentimentWordNet()); // <0 - negative, >0 - positive
				System.out.println();
				
			//	total_score += sentence.getSentimentWordNet();
			}
			
//			avg_score = new Double(total_score/document.size()).doubleValue();
//			System.out.println("total_score: " +total_score);
//			System.out.println("avg_score: " +avg_score);
		}
		
		System.exit(0);
	}

}
