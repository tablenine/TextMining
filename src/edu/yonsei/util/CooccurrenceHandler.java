package edu.yonsei.util;

import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntIterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class CooccurrenceHandler {

	TObjectIntHashMap trie = null;
	
	int coOccurrenceVectorSize = 2;
	public CooccurrenceHandler(int coOccurrenceVectorSize)
	{
		this.coOccurrenceVectorSize = coOccurrenceVectorSize;
		trie = new TObjectIntHashMap();
	}
	
	/**
	 * 
	 * @param words
	 */
	public void makeCooccurrenceVector(List<String> words)
	{
		ICombinatoricsVector<String> co_vector = Factory.createVector(words);
		
		// Create a simple combination generator to generate n-combinations of the initial vector
		Generator<String> gen = Factory.createSimpleCombinationGenerator(co_vector, coOccurrenceVectorSize);
		
		for (ICombinatoricsVector<String> combination : gen) {
			List<String> vec = combination.getVector();
			
			Pair<String,String> pair = new Pair(vec.get(0), vec.get(1));
			if (!trie.containsKey(pair)) {
				trie.put(pair, new Integer(1));
			} else {
				trie.put(pair, (Integer)trie.get(pair)+1);
			}
		}
	}
	
	/**
	 * 
	 * @param inputFile
	 */
	public void makeCooccurrenceVector(String inputFile)
	{
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(inputFile));
			while ((line = br.readLine()) != null) {
				String[] lines = line.split("\\s+");
				List<String> _lines = Arrays.asList(lines); 
				makeCooccurrenceVector(_lines);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * print helper function
	 */
	public void print()
	{
		TObjectIntIterator it = (TObjectIntIterator)trie.iterator();
		
		while (it.hasNext()) {
			it.advance();
				Pair pair = (Pair)it.key();
				Integer freq = it.value();
				System.out.println(pair.getKey() + " : " + pair.getValue() + " : " + freq);
			
		}
		//System.out.println(trie);
	}
	
	public TObjectIntHashMap getMap()
	{
		return trie;
	}
	
	/**
	 * print to file helper function
	 * @param outFile
	 */
	public void print(File outFile)
	{
		try {
			FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			TObjectIntIterator it = (TObjectIntIterator)trie.iterator();
			
			while (it.hasNext()) {
				it.advance();
				Pair pair = (Pair)it.key();
				Integer freq = it.value();
				//System.out.println(pair.getFirst() + "\t" + pair.getSecond() + "\t" + freq);
				if (freq > 5) {
					bw.write(pair.getKey() + "\t" + pair.getValue() + "\t" + freq + "\n");
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void print(File outFile, int threshold)
	{
		try {
			FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			TObjectIntIterator it = (TObjectIntIterator)trie.iterator();
			
			while (it.hasNext()) {
				it.advance();
				Pair pair = (Pair)it.key();
				Integer freq = it.value();
				//System.out.println(pair.getFirst() + "\t" + pair.getSecond() + "\t" + freq);
				if (freq > threshold) {
					bw.write(pair.getKey() + "\t" + pair.getValue() + "\t" + freq + "\n");
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printWithLabel(File outFile)
	{
		try {
			FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			TObjectIntIterator it = (TObjectIntIterator)trie.iterator();
			
			bw.write("Source\tTarget\tCount\n");
			while (it.hasNext()) {
				it.advance();
				Pair pair = (Pair)it.key();
				Integer freq = it.value();
				System.out.println(pair.getKey() + "\t" + pair.getValue() + "\t" + freq);
				if (freq > 5) {
					bw.write(pair.getKey() + "\t" + pair.getValue() + "\t" + freq + "\n");
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * test function
	 */
	public void test()
	{
		// Create the initial vector
		ICombinatoricsVector<String> initialVector = Factory.createVector(
		      new String[] { "red", "black", "white", "green", "blue" } );

		// Create a simple combination generator to generate n-combinations of the initial vector
		Generator<String> gen = Factory.createSimpleCombinationGenerator(initialVector, 2);
		
		// Print all possible combinations
		for (ICombinatoricsVector<String> combination : gen) {
			System.out.println(combination.getVector());
			System.out.println(combination);
		}
	}
	
	public static void main(String[] args)
	{
		ArrayList words = new ArrayList();
		words.add("sdaaaa");
		words.add("aaaa");
		words.add("aadaa");
		words.add("adaaa");
		words.add("saaaa");
		words.add("sdaaaa");
		words.add("aafssaa");
		words.add("aaadsda");
		
		int co_occurence_vec_size = 2;
		CooccurrenceHandler ch = new CooccurrenceHandler(co_occurence_vec_size);
		ch.makeCooccurrenceVector(words);
		ch.print();
	}
}
