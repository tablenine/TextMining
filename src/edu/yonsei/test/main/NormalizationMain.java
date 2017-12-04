package edu.yonsei.test.main;

import java.io.FileReader;
import java.util.Scanner;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;

public class NormalizationMain {
	
	public static void main(String[] args) throws Exception {
		//Scanner s = new Scanner(new FileReader("data/corpus/twitter_stream.txt"));	
		Scanner s = new Scanner(new FileReader("data/corpus/naver_movie_comments.txt"));
				
		String token_mode = "ngram"; //ngram or noun_phrase
		String mode = "ko"; //en or ko
		boolean isKomoran = true; //komoran or korean twitter
		int n = 1; //ngram size
		
		String morphData = "";
		String stopword = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
		} else {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
			//morphData = "/home/tsmm/yTextMiner/datas/";
			//stopword = "/home/tsmm/yTextMiner/data/util/stopwords.txt";
		}
			
		Pipeline pipe = null;
		if (mode.equals("en")) {
			pipe = new EnglishPipeline(stopword);
		} else if (mode.equals("ko")) {
			pipe = new KoreanPipeline(morphData,stopword);
		}
		
		for(int i=0; i<10; i++) {
			String text = s.nextLine();
					
			//Sentence sent = new Sentence(text);
			if(text.split("\t").length < 6) break;
			
			Sentence sent = new Sentence(text,"\t",5);
			
			sent.preprocess(mode,isKomoran,pipe);

			System.out.println("Sentence # " + (i+1));
			System.out.println(sent.getSentence());
			System.out.println("After tokenization");
			for(Token token : sent)
				System.out.print(token.getToken() + " ");
			System.out.println();
			System.out.println();
		}
		
		s.close();
	}

}
