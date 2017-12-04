package edu.yonsei.test.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;

import edu.njit.util.SortByValueMap;
import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;

import edu.yonsei.util.Sentence;

public class NGramMain {
	
	HashMap<Pair<String,String>,Double> ngramMap = new HashMap();
	
	static HashMap<String, Integer> frequencyMap = new HashMap();
	
	public NGramMain()
	{
	}
	
	public void createNGramNetwork(List<String> ngramList)
	{
		for (int i = 0; i < ngramList.size(); ++i) {
			String phrase = ngramList.get(i);
			for (int j = i+1; j < ngramList.size(); ++j) {
				String phrase2 = ngramList.get(j);
				Pair<String,String> pair = null;
				if (!phrase.equals(phrase2)) {
					if (phrase.compareTo(phrase2) < 0) {
						pair = Pair.of(phrase, phrase2);
					} else {
						pair = Pair.of(phrase2, phrase);
					}
					
					if (!ngramMap.containsKey(pair)) {
						ngramMap.put(pair,1.0);
					} else {
						ngramMap.put(pair,ngramMap.get(pair)+1.0);
					}
				}
			}
		}
	}
	
	public HashMap<Pair<String,String>,Double> getNgramMap()
	{
		return ngramMap;
	}
		
	public void writeFrequencyMap(String outputFile) throws Exception
	{
		File field_file = new File(outputFile);
		FileWriter fstream = new FileWriter(field_file);
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			HashMap<String, Integer> sorted = SortByValueMap.sortMapByValue(frequencyMap,false);
			for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
				if (entry.getValue() > 5)
				out.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			}
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) throws Exception {
		
		NGramMain main = new NGramMain();
		
		//Scanner s = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
		//Scanner s = new Scanner(new FileReader("naver_blog_results.txt"));
		Scanner s = new Scanner(new FileReader("naver_finance_results.txt"));
			

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
			morphData = "/home/tsmm/yTextMiner/datas/";
			stopword = "/home/tsmm/yTextMiner/data/util/stopwords.txt";
		}
			
		Pipeline pipe = null;
		if (mode.equals("en")) {
			pipe = new EnglishPipeline(stopword);
		} else if (mode.equals("ko")) {
			pipe = new KoreanPipeline(morphData,stopword);
		}
		
		int count = 0;
		while(s.hasNext()) {
			String text = s.nextLine();
			if(!text.equals("")) {
				
				Sentence sent = new Sentence(text);
				sent.preprocess(mode,isKomoran, pipe);
	
				System.out.println("Sentence");
				System.out.println(sent.getSentence());
				System.out.println(n + "-grams");
				
				List<String> token_results = null;
				
				if (token_mode.equals("ngram")) {
					token_results = sent.getNNounGrams(n);
				} else if (token_mode.equals("noun_phrase")) {
					sent.clearNounPhrases();
					sent.setStopWord(pipe.getStopWords());
					if (mode.equals("en")) {
						sent.getNounPhraseByOpenNLP(pipe.getChunker());
					} else if (mode.equals("ko")) {
						sent.getNounPhraseBySequence();
					}
					token_results = sent.getNounPhrases();
				}	
				
				//System.out.pri
				
				for (String ngram : token_results) {
					if (!frequencyMap.containsKey(ngram)) {
						frequencyMap.put(ngram, 1);
					} else {
						frequencyMap.put(ngram, frequencyMap.get(ngram)+1);
					}
				}
				
				System.out.println(token_results);
				System.out.println();
				
				main.createNGramNetwork(token_results);
				
				count++;
			}
		}
		s.close();
		
		
	}

}










