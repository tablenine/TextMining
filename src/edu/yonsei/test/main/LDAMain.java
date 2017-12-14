package edu.yonsei.test.main;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Topic;

/**
 * @author Reinald Kim Amplayo & Min Song
 */
public class LDAMain {
	
	public static void main(String[] args) throws Exception {
		//Scanner s = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
		Scanner s = new Scanner(new FileReader("data/corpus/naver_movie_comments.txt"));
		
		Vector<String> list = new Vector<String>();
		while(s.hasNext()) {
			
			String line = s.nextLine();
			if (line.split("\t").length > 5) {
				String text = line.split("\t")[5];
			
				list.add(text);
			}
		}	
		s.close();
		
		String token_mode = "ngram"; //ngram or noun_phrase
 		int n = 2;
		String mode = "ko";
		boolean isKomoran = true;
		String morphData = "";
		String stopword = "";
		//if (System.getProperty("os.name").startsWith("Windows")) {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
//		} else {
//			morphData = "/home/tsmm/yTextMiner/datas/";
//			stopword = "/home/tsmm/yTextMiner/data/util/stopwords.txt";
//		}
			
		Pipeline pipe = null;
		if (mode.equals("en")) {
			pipe = new EnglishPipeline(stopword);
		} else if (mode.equals("ko")) {
			pipe = new KoreanPipeline(morphData,stopword);
		}
		
		ArrayList<String> ngramList = new ArrayList();
		Collection collection1 = new Collection(list);
		for(int i=0; i<collection1.size(); i++) {
			Document document = collection1.get(i);	
			if (document.getDocument().length() < 5) continue;
			
			System.out.println("Document " + i + " : " + document.getDocument());
			
			document.preprocess(mode,isKomoran,pipe);
			
			String doc = "";
			for (int j = 0; j < document.size(); ++j) {
				Sentence sentence = document.get(j);
				List<String> token_results = null;
				
				if (token_mode.equals("ngram")) {
					token_results = sentence.getNNounGrams(n);
				} else if (token_mode.equals("noun_phrase")) {
		
					sentence.clearNounPhrases();
					sentence.setStopWord(pipe.getStopWords());
					
					if (mode.equals("en")) {
						sentence.getNounPhraseByOpenNLP(pipe.getChunker());
					} else if (mode.equals("ko")) {
						sentence.getNounPhraseBySequence();
					}
					
					token_results = sentence.getNounPhrases();
								
				}	
				
				//System.out.println("token results: " + token_results);
				
				if (token_results.size() < 1) continue;
				
				for (int k = 0; k < token_results.size(); ++k) {
					String ngram = token_results.get(k);
					ngram = ngram.trim();
					
					//if (!StringUtils.isAlphaSpace(ngram)) continue;
					
					if (ngram.length() > 1) {
						doc += ngram.replaceAll("\\s+", "_").toLowerCase() + " ";
					}
				}
			}	
			
			doc = doc.trim();
			if (doc.length() > 0) {
				ngramList.add(doc);	
				System.out.println("processed doc " + doc);
			}
		}
		
		Collection collection = new Collection(ngramList);
		collection.createLDA(10, 1000);
		
		for(int i=0; i<10; i++) {
			int idx = (int)(Math.random()*collection.size());
			Document document = collection.get(idx);
			System.out.println("Document: " + document.getDocument());
			Topic topic = document.getTopic("model/mallet/");
			System.out.println(topic);
		}
		
		System.out.println("TOPICS: ");
		Topic[] topics = collection.getTopics();
		for(int i=0; i<10; i++)
			System.out.println(topics[i]);
	}

}




