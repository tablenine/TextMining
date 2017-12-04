package edu.yonsei.preprocess;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.analysis.kr.KoreanFilter;
import org.apache.lucene.analysis.kr.morph.AnalysisOutput;
import org.apache.lucene.analysis.kr.morph.MorphAnalyzer;
import org.apache.lucene.analysis.kr.tagging.Tagger;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

public class KoreanMorphologyAnalyzer {

	public String getLuceneKoMorph(String input) throws Exception
	{
		MorphAnalyzer analyzer = new MorphAnalyzer();
		String result = "";
		List<AnalysisOutput> list = analyzer.analyze(input);
		for(AnalysisOutput o:list) {
			
			result += o.toString() + " : ";
			System.out.print(o.toString()+"->");
			for(int i=0;i<o.getCNounList().size();i++){
				System.out.print(o.getCNounList().get(i).getWord()+"/");
			}
			System.out.print(o.getPatn());
			System.out.println("<"+o.getScore()+">");
			
		}
			
		return result.trim();	
	}
	
	public void printLuceneKoMorph(String input) throws Exception
	{
		MorphAnalyzer analyzer = new MorphAnalyzer();
		String result = "";
		List<AnalysisOutput> list = analyzer.analyze(input);
		for(AnalysisOutput o:list) {
			
			System.out.print(o.toString()+"->");
			for(int i=0;i<o.getCNounList().size();i++){
				System.out.print(o.getCNounList().get(i).getWord()+"/");
			}
			System.out.print(o.getPatn());
			System.out.println("<"+o.getScore()+">");
			
		}
	}
	

	
	public String getTag(String token) throws Exception {

		MorphAnalyzer morphAnal = new MorphAnalyzer();
		
		Tagger tagger = new Tagger();
		AnalysisOutput ao = tagger.tagging(token, morphAnal.analyze(token));
		
		
		return ao.toString();
	}
	
	public void testKoreanMorph(String[] inputs) throws Exception
	{
		MorphAnalyzer analyzer = new MorphAnalyzer();
		long start = 0;
		for(String input:inputs) {
			List<AnalysisOutput> list = analyzer.analyze(input);
			for(AnalysisOutput o:list) {
				System.out.print(o.toString()+"->");
				for(int i=0;i<o.getCNounList().size();i++){
					System.out.print(o.getCNounList().get(i).getWord()+"/");
				}
				System.out.print(o.getPatn());
				System.out.println("<"+o.getScore()+">");
			}
			if(start==0) start = System.currentTimeMillis();
		}
		System.out.println((System.currentTimeMillis()-start)+"ms");
		
	}
	
	public void testTag() throws Exception {

		String str0 = "증가함에";
		String str1 = "따라서";
		String str2 = "적다";
		
		MorphAnalyzer morphAnal = new MorphAnalyzer();
		
		Tagger tagger = new Tagger();
		tagger.tagging(str0, morphAnal.analyze(str0));
		AnalysisOutput o = tagger.tagging(str1,str2, morphAnal.analyze(str1),morphAnal.analyze(str2));
		
		System.out.println(">>"+o);
	}
	
	public void printTextByKomoran(String text)
	{
		Komoran analyzer = new Komoran("datas/");
		/**
		List<List<Pair<String,String>>> results =  analyzer.analyze(text);
		for (List<Pair<String, String>> wordResult : results) {
			for (Pair<String, String> pair : wordResult) {
				String first = pair.getFirst();
				String second = pair.getSecond();
				
				System.out.println(first + " :: " + second);
			}
			System.out.println();
		}	
		**/
		
		System.out.println(text);
    	List<List<Pair<String,String>>> result = analyzer.analyze(text);
    	String terms="";
    	for (List<Pair<String, String>> eojeolResult : result) {
    		for (Pair<String, String> wordMorph : eojeolResult) {
    			terms=terms+wordMorph.getFirst().replaceAll
    					("[^가-힇A-Za-z0-9]", "")+" ";
    			
    			System.out.print("Term: " + terms);
    		}
    	}
    	
	}
	
	public static void main(String[] args) throws Exception
	{
		String[] inputs = new String[] {
				"합쳐져","뛰어오르고","급여생활자나","영세자영업자들을", "어떻게 대우할것인가?","영세농어민","서민계층들은","온몸으로","엄습하고",
				"사랑받아봄을","둔"
		};
		
		KoreanMorphologyAnalyzer kat = new KoreanMorphologyAnalyzer();
		kat.testKoreanMorph(inputs);

		System.out.println("************");
		
		String input = "아버지가 방에 들어가신다";
		kat.printLuceneKoMorph(input);

		System.out.println("************");
		
		
		String token = "어떠하실지";
		String tag = kat.getTag(token);
		System.out.println("Result " + tag);
		
		String source2 = " 대한민국 만세 우리나라 만세 야호!";
		String result1 = kat.getLuceneKoMorph(source2);
		System.out.println("Result by Lucene Morph Function " + result1);
	
		kat.printTextByKomoran(source2);
	}
}
