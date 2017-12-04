package applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.google.common.collect.ImmutableSet;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TargetStringToFeatures;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.njit.util.SortByValueMap;

import edu.yonsei.preprocess.EnglishPipeline;
import edu.yonsei.preprocess.KoreanPipeline;
import edu.yonsei.preprocess.Pipeline;
import edu.yonsei.util.Collection;
import edu.yonsei.util.CooccurrenceHandler;
import edu.yonsei.util.Document;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;
import gnu.trove.TObjectDoubleHashMap;

public class KISDIProcessor {

	static Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}\\p{Punct}\\S]+");

	public KISDIProcessor() {
	}

	public void preProcessPublicationFile(String inputFile, String prefix, String outputFile) throws Exception {
		System.out.println("Process: " + inputFile + "...");
		Scanner s = new Scanner(new FileReader(inputFile));
		// configuration information
		String token_mode = "ngram"; // ngram or noun_phrase
		String mode = "ko"; // en or ko
		boolean isKomoran = true; // komoran or korean twitter
		int n = 2; // ngram size

		String morphData = "";
		String stopword = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			morphData = "datas/";
			stopword = "data/util/stopwords_kr.txt";
		} else {
			morphData = "/home/tsmm/yTextMiner/datas/";
			stopword = "/home/tsmm/yTextMiner/data/util/stopwords_kr.txt";
		}

		Pipeline pipe = null;
		if (mode.equals("en")) {
			pipe = new EnglishPipeline(stopword);
		} else if (mode.equals("ko")) {
			pipe = new KoreanPipeline(morphData, stopword);
		}
		
		int docCount = 0;
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFile)));
		long estimatedTime;
		double elapsedSeconds;
		int count = 0;
		while (s.hasNext()) {
			String text = s.nextLine();
			String[] texts = text.split("\t");
			if (texts.length < 3 || texts[0].length() < 4)
				continue;
			String date = texts[0].substring(0, 4);
			try {
				Integer.parseInt(date);
			} catch (Exception e) {
				continue;
			}
			String rdoc = texts[1] + " . " + texts[2];
			//if(rdoc.split("\\s+").length > 500) continue;

			Document document = new Document(rdoc);

			document.preprocess(mode, isKomoran, pipe);
			System.out.println("Document " + count);
			estimatedTime = System.currentTimeMillis();
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

				// System.out.println("token results: " + token_results);

				if (token_results.size() < 1)
					continue;

				String sent = "";
				for (int k = 0; k < token_results.size(); ++k) {
					String ngram = token_results.get(k);
					ngram = ngram.trim();

					if (!StringUtils.isAlphaSpace(ngram))
						continue;

					if (ngram.length() > 1) {
						ngram = ngram.replaceAll("\\s+", "_").toLowerCase() + " ";
						sent += ngram + " ";
					}
				}

				if (sent.trim().length() > 0) {
					System.out.println(prefix + "_n_" + new Integer(docCount).toString() + "\t" + date + "\t" + sent.trim());
					out.write(prefix + "_n_" + new Integer(docCount).toString() + "\t" + date + "\t" + sent.trim() + "\n");
				}
			}

			docCount++;

			long estimatedTime1 = System.currentTimeMillis() - estimatedTime;
			elapsedSeconds = estimatedTime1 / 1000.0;
			System.out.println("Time taken for ngram: " + elapsedSeconds);
			count++;
		}
		s.close();		
		out.close();
	}


	
	public static void main(String[] args) throws Exception {
		//String dir = args[0];
		
		String dir = "D:\\My Documents\\yonsei_courses";
		
		File directory = new File(dir);
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				if (!file.getName().endsWith(".txt"))
					continue;
				KISDIProcessor pp = new KISDIProcessor();
				String name = file.getName().replace(".txt", "");
				System.out.println("FILE " + name);
				pp.preProcessPublicationFile(file.getAbsolutePath(), name, "output/" + name + "_ngram.txt");
			}
		}
	}
}
