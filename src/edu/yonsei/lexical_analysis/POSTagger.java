package edu.yonsei.lexical_analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class POSTagger {
	
	POSTaggerME posTagger = null;
	public POSTagger()
	{
	}
	
	public void parse(String sentence)
	{
		InputStream modelIn = null;
	    String[] tokens = null;
	    try {
			InputStream is = new FileInputStream("./models/en-token.bin"); 
			TokenizerModel model = new TokenizerModel(is); 
			Tokenizer tokenizer = new TokenizerME(model); 
			tokens = tokenizer.tokenize(sentence); 
	
			// Loading pos model
			POSModel posModel = new POSModelLoader().load(new File("./models/en-pos-maxent.bin"));
			posTagger = new POSTaggerME(posModel);

	    } catch (final IOException ioe) {
		   ioe.printStackTrace();
	    } finally {
			if (modelIn != null) {
				try {
				  modelIn.close();
				} catch (final IOException e) {} 
			}
	    }
	
		String[] tags = posTagger.tag(tokens);
		for (int i = 0; i < tokens.length; ++i) {
		     System.out.print(tokens[i] + "/" + tags[i] + " ");
		}
	}
	
	public static void main(String[] args)
	{
		String sentence = "She wants to go back to school.";
		POSTagger tagger = new POSTagger();
		tagger.parse(sentence);
	}
}
