package edu.yonsei.test.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import edu.yonsei.classification.feature.TFIDF;
import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Collection;

public class TFIDFMain {
	
	public TFIDFMain()
	{
	}
	
	public void writeResults(String fileName) throws Exception
	{
		Scanner s1 = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
		Scanner s2 = new Scanner(new FileReader("data/corpus/nytimes_news_json.txt"));
		
		List<String> documents = new ArrayList<String>();
		List<String> classes = new ArrayList<String>();
		
		TreeMap<String, Integer> classIndexMap = new TreeMap<String, Integer>();
		TreeMap<Integer, String> indexClassMap = new TreeMap<Integer, String>();
		int docCount = 0, classCount = 0;
		while(s1.hasNext()) {
			String url = s1.nextLine();
			s1.nextLine();
			
			String docText = "";
			while(s1.hasNext()) {
				String text = s1.nextLine();
				if(text.isEmpty()) break;
				docText += text + " ";
			}
			
			if(docText.length() > 100)
				docText = docText.substring(0, 100);
			
			String json = s2.nextLine();
			s2.nextLine();
			
			String section = json.split("\"section_name\":\"")[1].split("\"")[0];
			if(!classIndexMap.containsKey(section)) {
				classIndexMap.put(section, classCount);
				indexClassMap.put(classCount++, section);
			}
			int sectionIndex = classIndexMap.get(section);
			
			System.out.println("doc " + docCount + ": " + section);
			
			documents.add(docText);
			classes.add(sectionIndex+"");
			docCount++;
			
			if(docCount == 100) break;
		}
		System.out.println();
		
		s1.close();
		s2.close();
		
		System.out.println("classes: " + classIndexMap);

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
		
		Collection collection = new Collection(documents, classes);
		collection.preprocess(mode,stopword,morphData);
		
		System.out.print("extracting features...");
		collection.getTfIdf();
		System.out.println("done");
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			
			TFIDF tfidf = collection.getTfIdf();
			
			double[][] matrix = tfidf.getTfIdfMatrix();
			String[] wordVector = tfidf.getWordVector();
			for(int i=0; i<docCount; i++) {
				System.out.printf("%-10s", "doc " + i);
				writer.print(collection.getClassLabel(i) + " ");
				 
				for(int j=0; j < matrix[0].length; ++j) {
					if (j != matrix[0].length-1) {
						writer.print("T: " + wordVector[j] + ":" + matrix[i][j] + " ");
					} else {
						writer.print("T: " + wordVector[j] + ":" + matrix[i][j]);
					}
				}
				
				if (i < docCount-1) {
					writer.println();
				}
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public void computeTFIDF() throws Exception
	{
		Scanner s = new Scanner(new FileReader("data/corpus/nytimes_news_articles.txt"));
		
		List<String> documents = new ArrayList<String>();
		int docCount = 0;
		while(s.hasNext()) {
			String url = s.nextLine();
			s.nextLine();
			
			String docText = "";
			while(s.hasNext()) {
				String text = s.nextLine();
				if(text.isEmpty()) break;
				docText += text + " ";
			}
			
			if(docText.length() > 500)
				docText = docText.substring(0, 500);
			
			System.out.println("doc " + docCount + ": " + url);
			
			documents.add(docText);
			docCount++;
			
			if(docCount == 10) break;
		}
		System.out.println();
		

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
		
		Collection collection = new Collection(documents);
		collection.preprocess(mode,stopword,morphData);
		TFIDF tfidf = collection.getTfIdf();
		TFIDFMain main = new TFIDFMain();
		main.writeResults("tfidf_matrix.txt");
		
		double[][] matrix = tfidf.getTfIdfMatrix();
		String[] wordVector = tfidf.getWordVector();
		
		System.out.print("         ");
		for(int i=0; i<100; i+=10)
			System.out.printf("%11s", wordVector[i]);
		System.out.println();
		
		for(int i=0; i<docCount; i++) {
			System.out.printf("%-10s", "doc " + i);
			for(int j=0; j<100; j+=10)
				System.out.printf("%.8f ", matrix[i][j]);
			System.out.println();
		}
		
		s.close();	
	}
	
	public static void main(String[] args) throws Exception 
	{
		String tfidf = "tfidf.txt";
		TFIDFMain main = new TFIDFMain();
		main.writeResults(tfidf);
	}

}
