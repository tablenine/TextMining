package edu.yonsei.wsd;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.KnnClassifier;
import com.aliasi.classify.NaiveBayesClassifier;
import com.aliasi.classify.TfIdfClassifierTrainer;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.lm.LanguageModel;
import com.aliasi.lm.NGramBoundaryLM;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.lm.TokenizedLM;
import com.aliasi.matrix.CosineDistance;
import com.aliasi.tokenizer.TokenFeatureExtractor;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.FeatureExtractor;

import edu.yonsei.wsd.LingPipeWSDManager.SenseEvalDict;
import edu.yonsei.wsd.LingPipeWSDManager.TrainingData;

public class SenseEvalModel extends HashMap<String,BaseClassifier<CharSequence>> {
    static final long serialVersionUID = -6343177898894927184L;

    SenseEvalModel(SenseEvalDict dict, TrainingData trainingData)
        throws ClassNotFoundException, IOException  {

        for (String wordPlusCat : trainingData.keySet()) {
            Map<String,List<String>> senseToTextList = trainingData.get(wordPlusCat);
            String[] senseIds = senseToTextList.keySet().<String>toArray(new String[0]);
            System.out.println("    " + wordPlusCat + " [" + senseIds.length + " senses]");
            ObjectHandler<Classified<CharSequence>> trainer
                = createClassifierTrainer(senseIds);
            for (String senseId : senseToTextList.keySet()) {
                Classification classificationForSenseId = new Classification(senseId);
                List<String> trainingTextList = senseToTextList.get(senseId);
                for (String trainingText : trainingTextList) {
                    Classified<CharSequence> classified
                        = new Classified<CharSequence>(trainingText,classificationForSenseId);
                    trainer.handle(classified);
                }
            }
            @SuppressWarnings("unchecked")
            BaseClassifier<CharSequence> classifier
                = (BaseClassifier<CharSequence>)
                AbstractExternalizable.compile((Compilable)trainer);
            put(wordPlusCat,classifier);
        }
    }

    ObjectHandler<Classified<CharSequence>> createClassifierTrainer(String[] senseIds) {

        switch (LingPipeWSDManager.sClassifierNumber) {

        case 0:  // DEFAULT CHARACTER LM CLASSIFIER
            return DynamicLMClassifier.createNGramProcess(senseIds,5);

        case 1:  // CONFIGURABLE CHARACTER LM CLASSIFIER
            LanguageModel.Dynamic[] lms5 = new LanguageModel.Dynamic[senseIds.length];
            for (int i = 0; i < lms5.length; ++i)
                lms5[i] = new NGramProcessLM(6,     // n-gram
                                             128,   // num chars
                                             1.0);  // interpolation ratio
            return new DynamicLMClassifier<LanguageModel.Dynamic>(senseIds,lms5);

        case 2:  // DEFAULT NAIVE BAYES CLASSIFIER
            return new NaiveBayesClassifier(senseIds,LingPipeWSDManager.NORM_TOKENIZER_FACTORY);

        case 3: // DEFAULT TOKEN UNIGRAM LM CLASSIFIER
            return DynamicLMClassifier.createTokenized(senseIds,
            		LingPipeWSDManager.NORM_TOKENIZER_FACTORY,
                                                       1);

        case 4: // DEFAULT TOKEN BIGRAM LM CLASSIFIER
            return DynamicLMClassifier.createTokenized(senseIds,
            		LingPipeWSDManager.NORM_TOKENIZER_FACTORY,
                                                       2);

        case 5:  // CONFIGURABLE TOKENIZED LM CLASSIFIER W. CHARACTER BOUNDARY LM SMOOTHING
            LanguageModel.Dynamic[] lms2 = new LanguageModel.Dynamic[senseIds.length];
            for (int i = 0; i < lms2.length; ++i)
                lms2[i] = new TokenizedLM(LingPipeWSDManager.NORM_TOKENIZER_FACTORY,
                                          3, // n-gram length
                                          new NGramBoundaryLM(4,128,0.5,'\uFFFF'),
                                          new NGramBoundaryLM(4,128,0.5,'\uFFFF'),
                                          0.1); // interpolation param
            return new DynamicLMClassifier<LanguageModel.Dynamic>(senseIds,lms2);

        case 6:  // TF-IDF CLASSIFIER
            FeatureExtractor<CharSequence> featureExtractor5
                = new TokenFeatureExtractor(LingPipeWSDManager.SPACE_TOKENIZER_FACTORY);
            return new TfIdfClassifierTrainer<CharSequence>(featureExtractor5);

        case 7:  // K-NEAREST NEIGHBORS DEFAULT CLASSIFIER (EUCLIDEAN DISTANCE)
            FeatureExtractor<CharSequence> featureExtractor7
                = new TokenFeatureExtractor(LingPipeWSDManager.SPACE_TOKENIZER_FACTORY);
            return new KnnClassifier<CharSequence>(featureExtractor7,
                                                   16);  // num neighbors to average

        case 8:  // K-NEAREST NEIGHBORS DEFAULT CLASSIFIER (COSINE DISTANCE)
            FeatureExtractor<CharSequence> featureExtractor8
                = new TokenFeatureExtractor(LingPipeWSDManager.NGRAM_TOKENIZER_FACTORY);
            return new KnnClassifier<CharSequence>(featureExtractor8,
                                                   5, // num neighbors to average
                                                   new CosineDistance(),
                                                   true);

        default:
            String msg = "classifier id must be between 0 and 3."
                + " found id=" + LingPipeWSDManager.sClassifierNumber;
            throw new IllegalArgumentException(msg);
        }
    }

}
