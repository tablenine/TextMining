package edu.yonsei.test.main;

import java.io.FileReader;
import java.util.Scanner;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;

public class StemAndStopMain {

	public StemAndStopMain()
	{
		
	}
	
	public static void main(String[] args) throws Exception {
		//English text
		//Scanner s = new Scanner(new FileReader("data/corpus/twitter_stream.txt"));
		//Korean text
		Scanner s = new Scanner(new FileReader("data/corpus/twitterquery.txt"));


		String token_mode = "noun_phrase"; //ngram or noun_phrase
		String mode = "ko"; //en or ko
		boolean isKomoran = true; //komoran or korean twitter
		int n = 2; //ngram size
		
		String morphData = "";
		String stopword = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
		} else {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
		}
			
		Pipeline pipe = null;
		if (mode.equals("en")) {
			pipe = new EnglishPipeline(stopword);
		} else if (mode.equals("ko")) {
			pipe = new KoreanPipeline(morphData,stopword);
		}
		
		//while(s.hasNext()) {
		for(int i=0; i<10; i++) {
			String text = s.nextLine();
				
			Sentence sent = new Sentence(text);
			sent.preprocess(mode, isKomoran,pipe);

			//System.out.println("Sentence # " + (i+1));
			System.out.println(sent.getSentence());
			System.out.println("After stemming");
			for(Token token : sent) {
				
				System.out.print(token.getStem() + " :: " + token.isStopword() + " "); 
			}
			System.out.println();
			System.out.println("After lemmatization");
			for(Token token : sent)
				System.out.print(token.getLemma() + " ");

		}
		
		s.close();
	}

}
