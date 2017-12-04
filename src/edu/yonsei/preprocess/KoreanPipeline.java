package edu.yonsei.preprocess;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class KoreanPipeline extends Pipeline {

	static SentenceDetectorME sentenceDetector = null;	
	Komoran analyzer = null;
	private ArrayList<String> stopSet;
	ChunkerME chunkerModel = null;
	
	public KoreanPipeline(String morphData, String stopFile) throws Exception
	{
		stopSet = new ArrayList();
		analyzer = new Komoran(morphData);	
		initializeStopwords(stopFile);
		
		initSentSplitter("model/en-sent.bin");
		
		// chunker
		try {
			InputStream is = new FileInputStream("model/en-chunker.bin");
			ChunkerModel chunker = new ChunkerModel(is);
			chunkerModel = new ChunkerME(chunker);
			
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	@Override
	public void initializeStopwords(String stopFile) throws Exception {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(new FileReader(stopFile));
		while(s.hasNext())
			stopSet.add(s.next());
		s.close();
	}
	
	public static void initSentSplitter(String modelFile) throws Exception
	{
		InputStream modelIn = new FileInputStream(modelFile);

		try {
		  SentenceModel model = new SentenceModel(modelIn);
		  sentenceDetector = new SentenceDetectorME(model);
		} catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    }
		  }
		}
		
		
	}
	
	public SentenceDetectorME getSentenceDetector()
	{
		return sentenceDetector;
	}
	
	public boolean isStopword(String word)
	{
		return stopSet.contains(word);
	}
	
	public Komoran getKoreanAnalzer()
	{
		return analyzer;
	}
	
	public ArrayList<String> getStopWords()
	{
		return stopSet;
	}
	
	public ChunkerME  getChunker()
	{
		return chunkerModel;
	}
}
