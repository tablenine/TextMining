package edu.yonsei.lexical_analysis;

import java.util.StringTokenizer;

import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenization;
import com.aliasi.tokenizer.TokenizerFactory;

public class SimpleTokenizer {

	public SimpleTokenizer()
	{
	}
	
	public void tokenize(String text, String delimiter)
	{
		StringTokenizer st = new StringTokenizer(text,delimiter); 
		while (st.hasMoreTokens()) { 
			String token = st.nextToken(); 
			System.out.println("Token: " + token); 
		}
	}
	
	public void tokenizeBySplit(String text, String delimiter)
	{
		String[] tokens = text.split(delimiter);

		for (int i = 0; i < tokens.length; ++i) {
		    String token = tokens[i];
		    token = token.trim();
		     System.out.println("Token: " + token);
		}
	
	}
	
	public void tokenizeByLingPipe(String text, String delimiter)
	{
		TokenizerFactory tokFact = new RegExTokenizerFactory(delimiter);
		Tokenization tokenization = new Tokenization(text,tokFact);
		for (int n = 0; n < tokenization.numTokens(); ++n) { 
		      int start = tokenization.tokenStart(n);
		      int end = tokenization.tokenEnd(n); 
		      String whsp = tokenization.whitespace(n);
		      String token = tokenization.token(n);
		      System.out.println("Index: " + n + " Start Pos: " + start  
		            		+ " End Pos: " + end + " Delimiter: " + whsp 
		                      + " Token: " + token);
		}
	}
	
	public static void main(String[] args)
	{
		String text = "In many fields, recent years have brought a sharp rise in the size of the data to be analyzed and the complexity of the analysis to be performed.";
		String delimiter = "\\s+";
		SimpleTokenizer tokenizer = new SimpleTokenizer();
		tokenizer.tokenize(text, delimiter);
	}
}
