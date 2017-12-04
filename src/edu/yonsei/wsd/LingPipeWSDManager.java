package edu.yonsei.wsd;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.KnnClassifier;
import com.aliasi.classify.NaiveBayesClassifier;
import com.aliasi.classify.ScoredClassification;
import com.aliasi.classify.TfIdfClassifierTrainer;

import com.aliasi.corpus.ObjectHandler;

import com.aliasi.io.FileLineReader;

import com.aliasi.lm.LanguageModel;
import com.aliasi.lm.NGramBoundaryLM;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.lm.TokenizedLM;

import com.aliasi.matrix.CosineDistance;
import com.aliasi.matrix.TaxicabDistance;

import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.FeatureExtractor;
import com.aliasi.util.Files;
import com.aliasi.util.Proximity;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.NGramTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.TokenFeatureExtractor;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LingPipeWSDManager {

    // label for unknown sense
    static final String UNKNOWN_SENSE = "U";

    static SenseEvalDict mDict;

    static int sClassifierNumber = -1;

    static public final TokenizerFactory NGRAM_TOKENIZER_FACTORY
        = new NGramTokenizerFactory(4,6);

    static public final TokenizerFactory SPACE_TOKENIZER_FACTORY
        = new RegExTokenizerFactory("\\S+");

    static public final TokenizerFactory NORM_TOKENIZER_FACTORY
        = normTokenizerFactory();

    public LingPipeWSDManager()
    {
    }
    
    static public TokenizerFactory normTokenizerFactory() {
        TokenizerFactory factory = SPACE_TOKENIZER_FACTORY;
        factory = new LowerCaseTokenizerFactory(factory);
        // factory = EnglishStopTokenizerFactory(factory);
        // factory = PorterStemmerTokenizerFactory(factory);
        return factory;
    }
    
    /**
     * 
     * @param model
     * @param testData
     * @param file
     * @throws IOException
     */
    public void respond(SenseEvalModel model, TestData testData, File file)
            throws IOException {

            FileOutputStream fileOut = new FileOutputStream(file);
            OutputStreamWriter osWriter = new OutputStreamWriter(fileOut,"ISO-8859-1");
            BufferedWriter bufWriter = new BufferedWriter(osWriter);

            for (int i = 0; i < testData.mWordsPlusCats.size(); ++i) {
                String wordPlusCat = testData.mWordsPlusCats.get(i);
                BaseClassifier<CharSequence> classifier = model.get(wordPlusCat);
                String instanceId = testData.mInstanceIds.get(i);
                String textToClassify = testData.mTextsToClassify.get(i);

                Classification classification = classifier.classify(textToClassify);
                bufWriter.write(wordPlusCat + " " + wordPlusCat + ".bnc." + instanceId);
                if (classification instanceof ConditionalClassification) {
                    ConditionalClassification condClassification
                        = (ConditionalClassification) classification;
                    for (int rank = 0; rank < condClassification.size(); ++rank) {
                        int conditionalProb = (int) java.lang.Math.round(1000.0
                                                                         * condClassification.conditionalProbability(rank));
                        if (rank > 0 && conditionalProb < 1) break;
                        String category = condClassification.category(rank);
                        bufWriter.write(" " + category + "/" + conditionalProb);
                    }
                } else {
                    bufWriter.write(" " + classification.bestCategory());
                }
                bufWriter.write("\n");
            }
            bufWriter.close();
     }

     /**
      * 
      * @param lineStartString
      * @param lines
      * @param pos
      * @return
      */
     static public int seek(String lineStartString, String[] lines, int pos) {
            if (pos == -1) return -1;
            for ( ; pos < lines.length; ++pos)
                if (lines[pos].startsWith(lineStartString))
                    return pos;
            return -1;
    }

    /**
     * 
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException 
    {
    	LingPipeWSDManager manager = new LingPipeWSDManager();
    	
    	String dictionary_file= "D:\\workspace\\yTextMiner\\wsd\\EnglishLS.train\\EnglishLS.dictionary.xml";
    	String training_file = "D:\\workspace\\yTextMiner\\wsd\\EnglishLS.train\\EnglishLS.train";
    	String testing_file= "D:\\workspace\\yTextMiner\\wsd\\EnglishLS.test\\EnglishLS.test";
    	String answer_key_file= "D:\\workspace\\yTextMiner\\wsd\\EnglishLS.test\\EnglishLS.test.key";
    	String response_file= "answer_prediction_wsd.txt";
    			
        File dictFile = new File(dictionary_file);
        File trainFile = new File(training_file);
        File testFile = new File(testing_file);
        File responseFile = new File(response_file);
        sClassifierNumber = Integer.valueOf("6");
    
        System.out.println("Dictionary File="
                           + dictFile.getCanonicalPath());
        System.out.println("Training File="
                           + trainFile.getCanonicalPath());
        System.out.println("Testing File="
                           + testFile.getCanonicalPath());
        System.out.println("System Response File="
                           + responseFile.getCanonicalPath());
        System.out.println("classifier id=" + sClassifierNumber);

        System.out.println();
        System.out.println("Reading Dictionary.");
        SenseEvalDict dict = new SenseEvalDict(dictFile);
        System.out.println("     #entries=" + dict.size());

        System.out.println();
        System.out.println("Reading Training Data.");
        TrainingData trainingData = new TrainingData(trainFile);
        System.out.println("     #training words=" + trainingData.size());

        System.out.println();
        System.out.println("Reading Test Data.");
        TestData testData = new TestData(testFile);
        System.out.println("     #test cases=" + testData.mWordsPlusCats.size());

        System.out.println();
        System.out.println("Training and Compiling Models.");
        SenseEvalModel model = new SenseEvalModel(dict,trainingData);
        System.out.println("     finished training.");

        System.out.println();
        System.out.println("Running Model over Test Data.");
        manager.respond(model,testData,responseFile);
        System.out.println("     finished test data.");

        System.out.println();
        System.out.println("FINISHED.");

    }


    
    static class TestData {
        List<String> mWordsPlusCats = new ArrayList<String>();
        List<String> mInstanceIds = new ArrayList<String>();
        List<String> mTextsToClassify = new ArrayList<String>();
        TestData(File file) throws IOException {
            String[] lines = FileLineReader.readLineArray(file,"ISO-8859-1");
            int pos = 0;
            while ((pos = seek("<instance",lines,pos)) >= 0) {
                pos = parse(lines,pos);
            }
        }
        int parse(String[] lines, int pos) {
            String id = extractAttribute("id",lines[pos]);
            int endIndex = id.indexOf('.',id.indexOf('.')+1);
            String wordPlusCat = id.substring(0,endIndex);
            int startIndex = id.lastIndexOf('.') + 1;
            String instanceId = id.substring(startIndex);
            String textToClassify = lines[pos+2];
            mWordsPlusCats.add(wordPlusCat);
            mInstanceIds.add(instanceId);
            mTextsToClassify.add(textToClassify);
            return pos + 2;
        }
    }


    
    // wordPlusCat -> senseId -> { training-text }
    static class TrainingData extends HashMap<String,Map<String,List<String>>> {
        static final long serialVersionUID = 8094465899104433829L;
        public TrainingData(File file) throws IOException {
            String[] lines = FileLineReader.readLineArray(file,"ISO-8859-1");
            for (int pos = 0; (pos = seek("<lexelt",lines,pos)) >= 0; )
                pos = trainLexElt(lines,pos);
        }
        int trainLexElt(String[] lines, int pos) {
            String wordPlusCat = extractAttribute("item",lines[pos++]);
            while (pos < lines.length) {
                if (lines[pos].startsWith("</lexelt"))
                    return pos+1;
                if (lines[pos].startsWith("<instance"))
                    pos = trainInstance(wordPlusCat,lines,pos+1);
                else
                    ++pos;
            }
            return pos;
        }
        int trainInstance(String wordPlusCat, String[] lines, int pos) {
            Set<String> idSet = new HashSet<String>();
            for ( ; lines[pos].startsWith("<answer"); ++pos)
                idSet.add(extractAttribute("senseid",lines[pos]));
            if (!lines[pos++].startsWith("<context"))
                throw new IllegalStateException("context missing");
            String text = lines[pos];
            trainInstance(wordPlusCat,text,idSet);
            return pos+1; // skip end context, end instance

        }
        void trainInstance(String wordPlusCat, String trainingText,
                           Set<String> idSet) {
            for (String senseId : idSet) {
                if (senseId.equals(UNKNOWN_SENSE)) {
                    continue;
                }
                Map<String,List<String>> senseToTextListMap = get(wordPlusCat);
                if (senseToTextListMap == null) {
                    senseToTextListMap = new HashMap<String,List<String>>();
                    put(wordPlusCat,senseToTextListMap);
                }
                List<String> trainingTextList = senseToTextListMap.get(senseId);
                if (trainingTextList == null) {
                    trainingTextList = new ArrayList<String>();
                    senseToTextListMap.put(senseId,trainingTextList);
                }
                trainingTextList.add(trainingText);
            }
        }
    }


    // wordpluscat -> sense
    static class SenseEvalDict extends HashMap<String,Sense[]> {
        static final long serialVersionUID = -8332185573089002878L;
        SenseEvalDict(File file) throws IOException {
            String[] lines = FileLineReader.readLineArray(file,"ISO-8859-1");
            for (int pos = 0; (pos = seek("<lexelt",lines,pos)) >= 0; )
                pos = readDictionary(lines,pos);
        }
        int readDictionary(String[] lines, int pos) {
            String wordPlusCat = extractAttribute("item",lines[pos]);

            List<Sense> senseList = new ArrayList<Sense>();
            while (lines[++pos].startsWith("<sense"))
                senseList.add(new Sense(lines[pos]));

            Sense[] senses = senseList.<Sense>toArray(new Sense[senseList.size()]);
            put(wordPlusCat,senses);

            return pos;
        }
    }

    static class Sense {
        String mId;
        String mSource;
        String mSynset;
        String mGloss;
        Sense(String line) {
            mId = extractAttribute("id",line);
            mSource = extractAttribute("source",line);
            mSynset = extractAttribute("synset",line);
            mGloss = extractAttribute("gloss",line);
        }
        public String toString() {
            return "ID=" + mId
                + " SRC=" + mSource
                + " SYNSET=" + mSynset
                + " GLOSS=" + mGloss;
        }
    }

    static String extractAttribute(String att, String line) {
        int start = line.indexOf(att + "=") + att.length()+2;
        int end = line.indexOf('"',start);
        return line.substring(start,end);
    }


}



