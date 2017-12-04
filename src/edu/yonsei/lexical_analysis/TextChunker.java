package edu.yonsei.lexical_analysis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
//import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class TextChunker {

	static ArrayList<Parse> nounPhrases = new ArrayList();
	public TextChunker()
	{
	}
	
	public void parse(String sentence)
	{
		InputStream modelInParse = null;
		try {
		   //load chunking model
		   modelInParse = new FileInputStream("models/en-parser-chunking.bin");

		   ParserModel model = new ParserModel(modelInParse);
		   //create parse tree
		   Parser parser = ParserFactory.create(model);
		   Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
		   //call subroutine to extract noun phrases
		   for (Parse p : topParses) { 
			   p.show();
		       getNounPhrases(p);
		   }

		   //print noun phrases
		   for (Parse s : nounPhrases) { 
		       System.out.println("Noun Phrase: " + s); 
		   }
		} catch (IOException e) {
			  e.printStackTrace();
		} finally {
			if (modelInParse != null) {
				try {
			    	modelInParse.close();
			    }
			    catch (IOException e) {
			    }
			}
		}
	}
	
	public static void getNounPhrases(Parse p) 
	{
	    if (p.getType().equals("NP")) {
	         nounPhrases.add(p);
	    }
	    for (Parse child : p.getChildren()) {
	         getNounPhrases(child);
	    }
		
	}
	

	public static void main(String[] args)
	{
		String sentence = "We chased a colorful bird.";
		TextChunker chunker = new TextChunker();
		chunker.parse(sentence);
	}
}
