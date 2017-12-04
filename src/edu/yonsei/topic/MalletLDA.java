package edu.yonsei.topic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.yonsei.util.Collection;
import edu.yonsei.util.Document;
import edu.yonsei.util.Pair;
import edu.yonsei.util.Serialization;
import edu.yonsei.util.Topic;

/**
 * @author Reinald Kim Amplayo & Min Song
 */
public class MalletLDA {
	
	private ParallelTopicModel model;
	private InstanceList instances;
	
	public MalletLDA(Collection collection, int noTopics, int iteration) throws Exception {
		collection.createLDA(noTopics, iteration);
		model = ParallelTopicModel.read(new File("model/mallet/lda.model"));
		instances = (InstanceList) Serialization.deserialize("model/mallet/instances.model");
	}
	
	public MalletLDA(String folder) throws Exception {
		model = ParallelTopicModel.read(new File(folder + "/lda.model"));
		instances = (InstanceList) Serialization.deserialize(folder + "/instances.model");
	}
	
	public Topic getTopic(Document d) {
		String document = d.getDocument();
		Alphabet dataAlphabet = model.getAlphabet();
		
		TopicInferencer inferencer = model.getInferencer();
		
		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(document, null, "test instance", null));
		
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		Vector<Pair<Double, Integer>> list = new Vector<Pair<Double, Integer>>();
		for(int i=0; i<testProbabilities.length; i++) {
			Pair<Double, Integer> p = new Pair<Double, Integer>(testProbabilities[i], i);
			list.add(p);
		}
		Collections.sort(list);
		
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		int topicId = list.get(list.size()-1).right;
		return new Topic(topicId, dataAlphabet, topicSortedWords.get(topicId).iterator());
	}
	
	public Topic getTopicFromNewDocument(Document d) {
		String document = d.getDocument();
		
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File("data/util/stopwords.txt"), "UTF-8", false, false, false));
		pipeList.add(new TokenSequence2FeatureSequence());
		

		String[] array = new String[1];
		array[0] = document;
		Alphabet dataAlphabet = model.getAlphabet();
		
		TopicInferencer inferencer = model.getInferencer();
		
		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(document, null, "test instance", null));
		
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		Vector<Pair<Double, Integer>> list = new Vector<Pair<Double, Integer>>();
		for(int i=0; i<testProbabilities.length; i++) {
			Pair<Double, Integer> p = new Pair<Double, Integer>(testProbabilities[i], i);
			list.add(p);
		}
		Collections.sort(list);
		
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		int topicId = list.get(list.size()-1).right;
		return new Topic(topicId, dataAlphabet, topicSortedWords.get(topicId).iterator());
	}

}
