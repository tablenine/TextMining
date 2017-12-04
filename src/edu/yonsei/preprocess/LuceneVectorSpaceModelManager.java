package edu.yonsei.preprocess;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class LuceneVectorSpaceModelManager {
	
	DirectoryReader reader = null;
	IndexSearcher searcher = null;
	File corpus = null;
	Map<String,Integer> termIdMap = null;
	
	public LuceneVectorSpaceModelManager(File index, File corpus) throws IOException{
	    reader = DirectoryReader.open(FSDirectory.open(index));
	    searcher = new IndexSearcher(reader);
	    this.corpus = corpus;
	    termIdMap = computeTermIdMap(reader);
	}   
	/**
	*  Map term to a fix integer so that we can build document matrix later.
	*  It's used to assign term to specific row in Term-Document matrix
	*/
	private Map<String, Integer> computeTermIdMap(IndexReader reader) throws IOException {
	    Map<String,Integer> termIdMap = new HashMap<String,Integer>();
	    int id = 0;
	    Fields fields = MultiFields.getFields(reader);
	    Terms terms = fields.terms("contents");
	    TermsEnum itr = terms.iterator(null);
	    BytesRef term = null;
	    while ((term = itr.next()) != null) {               
	        String termText = term.utf8ToString();              
	        if (termIdMap.containsKey(termText))
	            continue;
	        //System.out.println(termText); 
	        termIdMap.put(termText, id++);
	    }
	
	    return termIdMap;
	}
	
	/**
	*  build term-document matrix for the given directory
	*/
	public RealMatrix buildTermDocumentMatrix () throws IOException {
	    //iterate through directory to work with each doc
	    int col = 0;
	    //int numDocs = countDocs(corpus);            //get the number of documents here      
	    int numDocs = 100;
	    int numTerms = termIdMap.size();    //total number of terms     
	    RealMatrix tdMatrix = new Array2DRowRealMatrix(numTerms, numDocs);
	
	    for (File f : corpus.listFiles()) {
	        if (!f.isHidden() && f.canRead()) {
	            //I build term document matrix for a subset of corpus so
	            //I need to lookup document by path name. 
	            //If you build for the whole corpus, just iterate through all documents
	            String path = f.getPath();
	            BooleanQuery pathQuery = new BooleanQuery();
	            pathQuery.add(new TermQuery(new Term("path", path)), null);
	            TopDocs hits = searcher.search(pathQuery, 1);
	
	            //get term vector
	            Terms termVector = reader.getTermVector(hits.scoreDocs[0].doc, "contents");
	            TermsEnum itr = termVector.iterator(null);
	            BytesRef term = null;
	
	            //compute term weight
	            while ((term = itr.next()) != null) {               
	                String termText = term.utf8ToString();              
	                int row = termIdMap.get(termText);
	                long termFreq = itr.totalTermFreq();
	                long docCount = itr.docFreq();
	                //double weight = computeTfIdfWeight(termFreq, docCount, numDocs);
	                double weight = 0.0;
	                tdMatrix.setEntry(row, col, weight);
	                
	            }
	            col++;
	        }
	    }       
	    return tdMatrix;
	}
}   