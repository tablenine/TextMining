package edu.yonsei.preprocess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Pair;
import edu.yonsei.util.Sentence;
import edu.yonsei.util.Token;

public class VectorSpaceModelManager {

	private int numOfWords;
	private double[][] docTermMatrix;
	private String[] wordVector;
	private int[] docLength;
	
	public VectorSpaceModelManager()
	{
	}
	
	public void createDocumentTermMatrix(ArrayList<Document> docs, int noOfVocabularySize)
	{
		//number of documents
		docLength = new int[docs.size()];
		
		//creating vocabulary
		HashMap<String, Integer> mapWordToIdx = new HashMap<String, Integer>();
		int nextIdx = 0;
		int docId = 0;
		for(Document doc : docs) {
			
			int count = 0;
			for(Sentence sent : doc) {
				for(int i=0; i<sent.size(); i++) {
					Token tok = sent.get(i);
					if(tok.isStopword()) continue;
					
					String lemma = tok.getLemma().toLowerCase();
					if (!StringUtils.isAlphanumeric(lemma)) continue;			
					if(lemma.isEmpty()) continue;
					if(!mapWordToIdx.containsKey(lemma)) {
						mapWordToIdx.put(lemma, nextIdx++);
					}
					count++;
				}
			}
			
			docLength[docId++] = count;
		}
		
		numOfWords = mapWordToIdx.size();
	
		//creating word vector
		wordVector = new String[numOfWords];
		for(String word : mapWordToIdx.keySet()) {
			int wordIdx = mapWordToIdx.get(word);
			wordVector[wordIdx] = word;
		}
		
		//creating document term matrix
		docTermMatrix = new double[docs.size()][];
		for(int docIdx=0; docIdx<docs.size(); docIdx++)
			docTermMatrix[docIdx] = new double[numOfWords];
		for(int docIdx=0; docIdx<docs.size(); docIdx++) {
			Document doc = docs.get(docIdx);
			for(Sentence sent : doc)
				for(int i=0; i<sent.size(); i++) {
					Token tok = sent.get(i);
					if(tok.isStopword()) continue;
					String lemma = tok.getLemma().toLowerCase();
					if (!StringUtils.isAlphanumeric(lemma)) continue;			
					if(lemma.isEmpty()) continue;
					String word = lemma;
					int wordIdx = mapWordToIdx.get(word);
					docTermMatrix[docIdx][wordIdx] = docTermMatrix[docIdx][wordIdx] + 1;
				}
		}
		
		//document normalization
		for(int docIdx=0; docIdx<docs.size(); docIdx++) {
			for(int wordIdx=0; wordIdx<numOfWords; wordIdx++) {
				docTermMatrix[docIdx][wordIdx] = docTermMatrix[docIdx][wordIdx] / docLength[docIdx];
			}
		}
		
		System.out.println(docs.size() + " :: " + numOfWords);
	}
	
	public void computerCosineSimilarity()
	{
		for (int i = 0; i < docTermMatrix.length; ++i) {
			double[] doc_a = docTermMatrix[i];
			
			for (int j = i+1; j < docTermMatrix.length; ++j) {
				double[] doc_b = docTermMatrix[j];
				
				//compute cosine similarity
				double score = cosineSimilarity(doc_a,doc_b);
				
				System.out.println("doc_id_" + i + " doc_id_" + j + " 's score is " + score);
			}
		}
	}
	
	/**
	 * 
	 * @param docVector1
	 * @param docVector2
	 * @return
	 */
	public double cosineSimilarity(double[] docVector1, double[] docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += docVector1[i] * docVector2[i];  //a.b
            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
        
        return cosineSimilarity;
    }
	
	public ArrayList<Document> getPseudoCollection() throws Exception
	{
		String doc_a = "최근 소셜미디어는 전세계적 커뮤니케이션 도구로서 사용에 전문적인 지식이나 기술이 필요하지 않기 때문에 이용자들로 하여금 콘텐츠의 실시간 생산과 공유를 가능하게 하여 기존의 커뮤니케이션 양식을 새롭게 변화시키고 있다";
		String doc_b = "토픽 모델링은 그 출현과 함께 텍스트 마이닝 분야에서 큰 관심을 받아온 연구방법론이다.";
		String doc_c = "이 장에서는 본 연구에서 개발한 트위터 마이닝시스템에 대해 소개하고, 이를 위한 실험문헌의 수집과 실험설계에 대해 설명한다.";
		String doc_d = "정보검색과 텍스트 마이닝 등의 영역 등에서 문헌 모델링이란 개별 문헌과 문헌집단을 해당 문헌에 출현한 단어를 통해 표현하는 방법을 의미한다.";
		String doc_e = "소셜 네트워크 분석이란 개인, 집단, 사회의 관계를 네트워크로 파악하는 연구방법론이다.";
		
		ArrayList<String> docs = new ArrayList();
		docs.add(doc_a);
		docs.add(doc_b);
		docs.add(doc_c);
		docs.add(doc_d);
		docs.add(doc_e);
		
		ArrayList<Document> documents = new ArrayList();
		
		String morphData = "";
		String stopword = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			morphData = "datas/";
			stopword = "data/util/stopwords.txt";
		} else {
			morphData = "/home/tsmm/yTextMiner/datas/";
			stopword = "/home/tsmm/yTextMiner/data/util/stopwords.txt";
		}

		Pipeline pipe = new KoreanPipeline(morphData,stopword);
				
		Collection collection = new Collection(docs);
		for(int i=0; i<collection.size(); i++) {
			Document document = collection.get(i);
			document.preprocess("ko",true,pipe);
			System.out.println("Document " + i);
			
			documents.add(document);
		}
		return documents;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{	
		VectorSpaceModelManager vsm = new VectorSpaceModelManager();
		int noOfVocabularySize = 100;
		
		ArrayList<Document> docs = vsm.getPseudoCollection();
		vsm.createDocumentTermMatrix(docs, noOfVocabularySize);
		
		vsm.computerCosineSimilarity();
	}
}
