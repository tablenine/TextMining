package edu.yonsei.co_word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import cc.mallet.types.Instance;
import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;

public class SimpleCooccurrenceManager {

	HashMap<Pair<String,String>,Integer> coWordMap = new HashMap();
	public SimpleCooccurrenceManager()
	{
	}
	
	public void computeCooccurrence(String inputFile) throws Exception
	{
		long startTime = System.currentTimeMillis();	
		Scanner s = new Scanner(new FileReader(inputFile));
		ArrayList<String> docs = new ArrayList();
		int count = 0;
		while(s.hasNext()) {
			String text = s.nextLine();	
			docs.add(text);
			count++;
		}	
		s.close();
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		double elapsedSeconds = estimatedTime / 1000.0;
		
		System.out.println("Time taken: " + elapsedSeconds);
	
		//configuration information
		String token_mode = "ngram"; //ngram or noun_phrase
		String mode = "en"; //en or ko
		boolean isKomoran = false; //komoran or korean twitter
		int n = 1;
	
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
    	
		Collection collection = new Collection(docs);
		for(int i=0; i<collection.size(); i++) {
			Document document = collection.get(i);
			document.preprocess(mode,isKomoran,pipe);
			System.out.println("Document " + i);

			for (int j = 0; j < document.size(); ++j) {
				Sentence sentence = document.get(j);
				for (int k = 0; k < sentence.size(); ++k) {
					Token token1 = sentence.get(k);
					if (token1.isStopword()) continue;
					if (!token1.getPOS().startsWith("N")) continue;
					for (int l = k+1; l < sentence.size(); ++l) {
						Token token2 = sentence.get(l);
						if (token2.isStopword()) continue;
						if (!token2.getPOS().startsWith("N")) continue;
						if (!token1.getLemma().equals(token2.getLemma())) {
							Pair pair = Pair.of(token1.getLemma(), token2.getLemma());
							
							if (!coWordMap.containsKey(pair)) {
								coWordMap.put(pair, 1);
							} else {
								coWordMap.put(pair, coWordMap.get(pair)+1);
							}
						}
					}
				}
			}
		}	
		
		File field_file = new File("co_word_map_results.txt");
		FileWriter fstream = new FileWriter(field_file);
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			for (Map.Entry<Pair<String,String>, Integer> entry : coWordMap.entrySet()) {
				Pair pair = entry.getKey();
				Integer frequency = entry.getValue();
				out.write(pair.getLeft() + "\t" + pair.getRight() + "\t" + frequency + "\n");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long estimatedTime1 = System.currentTimeMillis() - estimatedTime;
		elapsedSeconds = estimatedTime1 / 1000.0;
		System.out.println("Time taken for ngram and co-occurrence: " + elapsedSeconds);
	}
	
	public static void main(String[] args) throws Exception
	{
		String inputFile = "temp.txt";
		SimpleCooccurrenceManager cooccurrence = new SimpleCooccurrenceManager();
		cooccurrence.computeCooccurrence(inputFile);
	}
}
