package edu.yonsei.lexical_analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class NGramWordAnalyzer {

	public NGramWordAnalyzer()
	{
	}
	
	public void computeNGram(String text, int min, int max) throws Exception
	{	
		// Parse the input into n-gram tokens
		WhitespaceAnalyzer simpleAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_31);	
		boolean outputUnigrams = true;
		boolean outputUnigramsIfNoShingles = true;	
		ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(simpleAnalyzer, min, max, " ", outputUnigrams, outputUnigramsIfNoShingles);
		StringReader reader = new StringReader(text);
		TokenStream stream = shingleAnalyzer.tokenStream("contents", reader);
		CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class); 
  
		stream.reset();
	    while (stream.incrementToken()) {
	      
	      String termText = charTermAttribute.toString();
	      System.out.print(termText + "|");
	    }
	}
	
	public static void main(String[] args) throws Exception
	{
		String text = "Friends Romans Countrymen lend me your ears";
		int min  = 2;
		int max = 3;
		NGramWordAnalyzer ngram = new NGramWordAnalyzer();
		ngram.computeNGram(text, min, max);
	}
}
