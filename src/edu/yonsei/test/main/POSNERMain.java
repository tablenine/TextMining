package edu.yonsei.test.main;

import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;

public class POSNERMain {
	
	public static void main(String[] args) throws Exception {
		Scanner s = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
		//Scanner s = new Scanner(new FileReader("data/corpus/naver_movie_comments.txt"));
		

		String token_mode = "noun_phrase"; //ngram or noun_phrase
		String mode = "en"; //en or ko
		boolean isKomoran = false; //komoran or korean twitter
		int n = 2; //ngram size
		
		String morphData = "";
		String stopword = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
		} else {
			morphData = "/home/tsmm/yTextMiner/datas/";
			stopword = "/home/tsmm/yTextMiner/data/util/stopwords.txt";
		}
			
		Pipeline pipe = null;
		if (mode.equals("en")) {
			pipe = new EnglishPipeline(stopword);
		} else if (mode.equals("ko")) {
			pipe = new KoreanPipeline(morphData,stopword);
		}
		
		String url = s.nextLine();
		s.nextLine();
		while(true) {
			String text = s.nextLine();
			if(text.equals("")) break;
			
			Sentence sent = new Sentence(text);
			sent.preprocess(mode,isKomoran,pipe);
			
			System.out.println("Sentence");
			System.out.println(sent.getSentence());
			System.out.println("Sentence with POS");
			for(Token token : sent)
				System.out.print(token.getToken() + "|" + token.getPOS() + "|" + token.getNER() + " ");
			System.out.println();
			System.out.println();
		}
		
		/**
		while(s.hasNext()) {
			String text = s.nextLine();
			if(!text.equals("") && text.split("\t").length >= 6) {
				
				Sentence sent = new Sentence(text,"\t",5);
				sent.preprocess(mode,isKomoran);

				System.out.println("Sentence");
				System.out.println(sent.getSentence());
				System.out.println("Sentence with POS");
				for(Token token : sent)
					System.out.print(token.getToken() + "|" + token.getPOS() + " ");
				System.out.println();
				
			}
		}
		**/
		s.close();
	}

}
