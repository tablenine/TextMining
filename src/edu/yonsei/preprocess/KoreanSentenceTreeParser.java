package edu.yonsei.preprocess;

import kaist.cilab.db.FileManager;
import kaist.cilab.parser.berkeleyadaptation.BerkeleyParserWrapper;
import kaist.cilab.parser.berkeleyadaptation.Configuration;
import kaist.cilab.parser.corpusconverter.sejong2treebank.sejongtree.ParseTree;
import kaist.cilab.parser.dependency.DTree;
import kaist.cilab.parser.psg2dg.Converter;

public class KoreanSentenceTreeParser {

	public KoreanSentenceTreeParser()
	{
		Configuration.hanBaseDir = "D:\\workspace\\yTextMiner\\hannanum\\";
		Configuration.parserModel = "D:\\workspace\\yTextMiner\\hannanum\\models\\parser\\KorGrammar_BerkF_ORIG ";
	}
	
	public DTree parseSentence(String sentence, String parseResult)
	{
		//parse result
		String parsResult_FuncTag = "";
			
		//ParseTree OBJ
		ParseTree pt = null;
		
		//convert Dependency Tree OBJ
		DTree depTree = null;
		
		try {
			//CFG Parsing and dependency parsing part
			//Configuration.parserModel = "D:\\workspace\\yTextMiner\\hannanum\\models\\parser";
			BerkeleyParserWrapper bpw	= new BerkeleyParserWrapper(Configuration.parserModel);			
			String str = sentence;
			//1. parse the sentence
			parseResult = bpw.parse(str);
			//2. convert PSG-> DG
			Converter cv = new Converter();
			//2-1 need to function Tags so we split function tags
			parsResult_FuncTag = Converter.functionTagReForm(parseResult);
			//2-3 delete \n information from parse result
			parsResult_FuncTag = cv.StringforDepformat(parsResult_FuncTag);
			//2-4 convert to store ParseTree OBJ
			pt = new ParseTree(sentence, parsResult_FuncTag, 0, true);		
			//2-5 convert to store Dependency Tree OBJ
			depTree	= cv.convert(pt);

			System.out.println("Start#\n" + parseResult+ "End");
			System.out.println(depTree.toString());
						
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return depTree;
	}
	
	public static void main(String[] args)
	{

		String originalText = "아버지가 방에 들어가신다.";
		String sentence = "대한민국은 동아시아의 한반도 남부에 있는 공화국이다. ";
		String parseResult = "";
		
		KoreanSentenceTreeParser parser = new KoreanSentenceTreeParser();
		DTree depTree = parser.parseSentence(sentence, parseResult);
		
		System.out.println("==============================================");
		System.out.println("results : "+ originalText);
		System.out.println("");
		System.out.println("PSG parsing : ");
		System.out.println(parseResult);
		System.out.println("");
		System.out.println("DG parsing : ");
		System.out.println(depTree.toString());
		System.out.println("==============================================");
		
	}
	
}
