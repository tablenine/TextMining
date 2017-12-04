package edu.yonsei.test.main;

import java.io.FileReader;
import java.util.Scanner;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Document;
import edu.yonsei.util.Sentence;

public class ParsingMain {
	
	public static void main(String[] args) throws Exception {
		Scanner s = new Scanner(new FileReader("data/corpus/github_text.txt"));
		

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
			if(text.startsWith("URL:")) break;
			if(text.isEmpty()) continue;
			
			Document doc = new Document(text);
			doc.preprocess(mode,isKomoran,pipe);
			
			for(int i=0; i<doc.size(); i++) {
				Sentence sent = doc.get(i);
				
				System.out.println("Sentence " + (i+1));
				System.out.println(sent.getSentence());
				System.out.println("Dependencies");
				for(String dependency : sent.getDependencies())
					System.out.println(dependency);
				System.out.println();
			}
		}
		
		s.close();
	}

}
