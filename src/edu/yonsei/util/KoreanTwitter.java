package edu.yonsei.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import scala.collection.Seq;

import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessor;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer.KoreanToken;

public class KoreanTwitter {

	HashMap<String,Integer> posCountMap = new HashMap();
	
	public KoreanTwitter()
	{
	}
	
	public void clearPosCountMap()
	{
		posCountMap.clear();
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public ArrayList<String> tokenize(String text)
	{
		ArrayList<String> tokenizedList = new ArrayList();
		
		// Normalize
	    CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
	    
	    try {
		    // Tokenize
		    Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
		  
		    // Stemming
		    Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
	
		    List<KoreanTokenJava> _tokens = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);
		    for (KoreanTokenJava token : _tokens) {
		    	String word = token.getText();
		    	String pos = token.getPos().name();
		    	int length = token.getLength();
		    	int offset = token.getOffset();
		    	
		    	System.out.println("Token " + word + " Pos " + pos + " Len " + length + " Offset " + offset);

		    	if (!pos.startsWith("Josa") 
		    			&& !pos.startsWith("Numb") && !pos.startsWith("URL") 
		    			&& !pos.startsWith("ScreenName") && !pos.startsWith("Hashtag")
		    			//&& ((pos.startsWith("Punc") || pos.startsWith("KoreanParticle"))) ) {
		    		) {
		    			
		    		tokenizedList.add(word);
		    		
		    		if (!posCountMap.containsKey(pos)) {
		    			posCountMap.put(pos, 1);
		    		} else {
		    			posCountMap.put(pos, posCountMap.get(pos)+1);
		    		}
		    	} 
		    }
		    
		    //System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed));

		    // Phrase extraction
		    /**
		    List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
		    for (int i = 0; i < phrases.size(); ++i) {
		    	KoreanPhraseExtractor.KoreanPhrase phrase = phrases.get(i);
		    	if (tokenizedList.contains(phrase.text())) {
		    		tokenizedList.add(phrase.text());
		    	}
		    }
		    **/
	    } catch (Exception e) {    	
	    	String[] _tokens = text.split("\\s+");
	    	for (int i = 0; i < _tokens.length; ++i) {
	    		tokenizedList.add(_tokens[i]);
	    	}	
	    }
	    
	    return tokenizedList;

	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Integer> getPosCountMap()
	{
	    return posCountMap;
	}
	
	public static void main(String[] args) {
	    String text = "한국어를~~~~ 처리하는!! 아름다운^^ 예시입니닼ㅋㅋㅋㅋㅋ #한국어";

	    // Normalize
	    CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
	    System.out.println(normalized);
	    // 한국어를 처리하는 예시입니다ㅋㅋ #한국어

	    // Tokenize
	    Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(tokens));
	    // [한국어, 를, 처리, 하는, 예시, 입니, 다, ㅋㅋ, #한국어]
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens));
	    // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


	    // Stemming
	    Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(stemmed));
	    // [한국어, 를, 처리, 하다, 예시, 이다, ㅋㅋ, #한국어]
	    
	    List<KoreanTokenJava> _tokens = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);
	    for (KoreanTokenJava token : _tokens) {
	    	String word = token.getText();
	    	String pos = token.getPos().name();
	    	int length = token.getLength();
	    	int offset = token.getOffset();
	    	System.out.println("Token " + word + " Pos " + pos + " Len " + length + " Offset " + offset);
	    }
	    
	    System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed));
	    // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하다(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 이다(Adjective: 12, 3), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


	    // Phrase extraction
	    List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
	    System.out.println(phrases);
	    // [한국어(Noun: 0, 3), 처리(Noun: 5, 2), 처리하는 예시(Noun: 5, 7), 예시(Noun: 10, 2), #한국어(Hashtag: 18, 4)]

	  }
	
}
