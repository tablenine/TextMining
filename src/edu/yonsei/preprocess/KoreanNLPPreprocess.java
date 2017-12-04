package edu.yonsei.preprocess;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;
import kr.co.shineware.util.common.model.Pair;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import scala.collection.Seq;

/**
 * @author Reinald Kim Amplayo & Min Song
 */
public class KoreanNLPPreprocess implements Preprocess {

	private Porter porter;
	
	public boolean isKomoran = false;
	Pipeline pipe = null;
	
	public KoreanNLPPreprocess(Pipeline pipe, boolean isKomoran) throws Exception
	{
		this.pipe = pipe;
		
		porter = new Porter();
		this.isKomoran = isKomoran;
	}
	
	public boolean isStopword(String word)
	{
		return pipe.getStopWords().contains(word);
	}
	
	@Override
	public void initializeStopwords(String stopFile) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void preprocess(Token t) {
		
		if (!isKomoran) {
			// Normalize
		    CharSequence normalized = TwitterKoreanProcessorJava.normalize(t.getToken());
		  
		    // Tokenize
		    Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
		   
		    // Stemming
		    Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
		    
		    List<KoreanTokenJava> _tokens = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);
		    	
			t.setLemma(_tokens.get(0).getText());
			t.setPOS(_tokens.get(0).getPos().name());
			t.setNER("O");
			t.setStop(isStopword(_tokens.get(0).getText()) || isStopword(t.getToken()));
		
		} else {
			applyKomoranInToken(t);
		}
	}

	@Override
	public void preprocess(Sentence s) {
		try {
			
			if (!isKomoran) {
				// Normalize
			    CharSequence normalized = TwitterKoreanProcessorJava.normalize(s.getSentence());
			  
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
			    	
			    	//System.out.println("Token " + word + " Pos " + pos + " Len " + length + " Offset " + offset);

			    	if (!StringUtils.endsWithAny(word, new String[] {null, ",", ".", "-", "\"", "?", "'", ";", ":"})) {
			    		Token t = new Token(word, word, pos, "O", word, isStopword(word), "ko", isKomoran);
			    		s.add(t);
			    	}
			    }
			} else {
				applyKomoranInSentence(s, isKomoran);
			}
		} catch (Exception e) {
			System.out.println("There is an error " + e.getMessage());
		}
		
	}

	@Override
	public void preprocess(Document d) throws Exception {
		if (d.size() > 0) {
			System.out.println("IS this????");
			for(int i = 0; i < d.size(); ++i) {
				Sentence sent = d.get(i);
				preprocess(sent);
				d.add(sent);
			}
		} else {
			String[] sentences = pipe.getSentenceDetector().sentDetect(d.getDocument());
			for (int j = 0; j < sentences.length; ++j) {
				String sent = sentences[j];
				Sentence s = new Sentence(sent);
				preprocess(s);
				d.add(s);
			}
		}
	}

	@Override
	public void preprocess(Collection c) throws Exception {
		for(int i=0; i<c.size(); i++) {
			Document d = c.get(i);
			String sents = d.toString();
			String[] sentences = pipe.getSentenceDetector().sentDetect(sents);
			for (int j = 0; j < sentences.length; ++j) {
				String sent = sentences[i];
				preprocess(new Document(sent));
			}
			
		}
	}

	private void applyKomoranInToken(Token t)
	{
		List<List<Pair<String,String>>>result =  pipe.getKoreanAnalzer().analyze(t.getToken());

		String first = result.get(0).get(0).getFirst();
		String second = result.get(0).get(0).getSecond();
		
		//System.out.println(first + " :: " + second);
		
		t.setLemma(first);
		t.setPOS(second);
		t.setNER("O");
		t.setStop(isStopword(first) || isStopword(t.getToken()));
		
	}
	
	private void applyKomoranInSentence(Sentence s, boolean isKomoran)
	{
		List<List<Pair<String,String>>>reslut =  pipe.getKoreanAnalzer().analyze(s.getSentence());

		for (List<Pair<String, String>> wordResult : reslut) {
			for (Pair<String, String> pair : wordResult) {
				String first = pair.getFirst();
				String second = pair.getSecond();
				
				//System.out.println(first + " :: " + second);
				
		    	Token t = new Token(first, first, second, "O", first, isStopword(first), 
		    						"ko", isKomoran);
				s.add(t);
			}
		}	
	}
	

	
}
