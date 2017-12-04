package edu.yonsei.test.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.topic.MalletDMR;
import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Jaso;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;

/**
 * @author Reinald Kim Amplayo & Min Song
 */
public class DMRMain {
	
	public DMRMain()
	{	
	}
	
	public void parseNaverComments() throws Exception
	{
		Scanner s = new Scanner(new FileReader("data/corpus/newsfordmr4th.txt"));

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
		
		int d_count = 0;
		HashMap<String,String> docMap = new HashMap();
		while(s.hasNext()) {
			String text = s.nextLine();
			
			String[] texts = text.split("\t");
			System.out.println(":: " + text);
			String year = texts[0];		
			String[] _years = year.split("-");
			if (_years.length < 2) continue;
			String n_year = _years[0] + "_" + _years[1];
			String doc = texts[1];
			
			if (!docMap.containsKey(doc)) {
				docMap.put(doc, n_year);
			} 
			
			d_count++;
			
			if (d_count > 10) break;
		}	
		s.close();
		
		ArrayList<String> docs = new ArrayList();
		int docCount = 0;
		for (Map.Entry<String,String> entry : docMap.entrySet()) {
			docs.add(entry.getKey());
				
		}
		 
		ArrayList<String> ngramDocList = new ArrayList();

    	System.out.println("Stopword list " + pipe.getStopWords());
		Collection collection = new Collection(docs);
		for(int i=0; i<collection.size(); i++) {
			Document document = collection.get(i);
			
			if (document.getDocument().length() < 5) continue;
			
			System.out.println("Document " + i);
			
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
					
					if (!StringUtils.isAlphaSpace(ngram)) continue;
					
					if (ngram.length() > 1) {
						doc += ngram.replaceAll("\\s+", "_").toLowerCase() + " ";
					}
				}
			}		
			doc = doc.trim();
			if (doc.length() > 0) {
				ngramDocList.add(doc);	
				System.out.println("processed doc " + doc);
 			}
		}
		s.close();

		ArrayList<String> classes = new ArrayList(docMap.values());
		Collection collection1 = new Collection(ngramDocList, classes);
		collection1.createDMR(15, 500, 100);
		
		MalletDMR dmr = new MalletDMR("model/mallet/");
		dmr.printParameters("newsfordmr4th_parameter.txt");
		dmr.printTopics("newsfordmr4th_topic.txt");
		
	}
	
	public void parseNYTimes() throws Exception
	{
		Scanner s1 = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
		Scanner s2 = new Scanner(new FileReader("data/corpus/nytimes_news_json.txt"));
		
		List<String> documents = new ArrayList<String>();
		List<String> classes = new ArrayList<String>();
		
		int docCount = 0;
		while(s1.hasNext()) {
			String json = s2.nextLine();
			s2.nextLine();
			String[] dates = json.split("\"pub_date\":\"");
			
			for(int i=1; i<dates.length; i++) {
				String url = s1.nextLine();
				s1.nextLine();
				
				String docText = "";
				while(s1.hasNext()) {
					String text = s1.nextLine();
					if(text.isEmpty()) break;
					docText += text + " ";
				}
				
				if(docText.length() > 1000)
					docText = docText.substring(0, 1000);
				
				docCount++;
				documents.add(docText);
				
				String pubDate = dates[i].split("T")[0];
				classes.add(pubDate);
				
				System.out.println(pubDate + " " + docText);
				
				if(docCount == 100) break;
			}
		
			//if(docCount == 5000) break;
		}
		System.out.println();
		
		s1.close();
		s2.close();
		
		Collection collection = new Collection(documents, classes);
		collection.createDMR(10, 500, 100);
		
		MalletDMR dmr = new MalletDMR("model/mallet/");
		dmr.printParameters("parameters.txt");
		dmr.printTopics("topics.txt");
	}
	
	public static void main(String[] args) throws Exception 
	{
		DMRMain dmr = new DMRMain();
		dmr.parseNaverComments();
		//dmr.parseNYTimes();
		
	}

}









