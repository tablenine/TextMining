package edu.yonsei.apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class InvertedIndex {
 
	Map<String, List<Tuple>> index = new HashMap<String, List<Tuple>>();
	List<String> items = new ArrayList<String>();
	HashMap<String,ArrayList<String>> userBookMap = new HashMap();
	
	public InvertedIndex()
	{
	}
	
	public void indexFile(File file) throws IOException 
	{
		int pos = 0;
		Scanner s = new Scanner(new FileReader(file));
		while(s.hasNext()) {
			String line = s.nextLine();
			String[] split = line.split(",");
			String id = split[1];
			items.add(id);
			//System.out.println("X " + id);
			int doc_id = items.indexOf(id);
			for (int i = 2; i < split.length; ++i) {
				String word = split[i];
				word = word.trim();
				pos++;

				if (!userBookMap.containsKey(id)) {
					ArrayList<String> books = new ArrayList();
					books.add(word);
					userBookMap.put(id, books);
				} else {
					userBookMap.get(id).add(word);
				}
				
				//List<Tuple> idx = index.get(word);
				if (!index.containsKey(word)) {
					List<Tuple> idx = new LinkedList<Tuple>();
					idx.add(new Tuple(doc_id, pos));
					index.put(word, idx);
				} else {
					index.get(word).add(new Tuple(doc_id, pos));
				}
			}
		}
		System.out.println("indexed " + file.getPath() + " " + pos + " words " + userBookMap.size());
	}
 
	public void search(List<String> words) {
		for (String word : words) {
			Set<String> answer = new HashSet<String>();
		
			List<Tuple> idx = index.get(word);
			
			if (idx != null) {
				for (Tuple t : idx) {
					System.out.println("Search " + t.toString());
					answer.add(items.get(t.fileno));
				}
			}
			System.out.print(word);
			for (String f : answer) {
				System.out.print(" " + f);
			}
			System.out.println("");
		}
	}
 
	public Set<String> searchWords(List<String> words) {
		
		Set<String> answer = new HashSet<String>();
		for (String word : words) {
		
			List<Tuple> idx = index.get(word);
			
			if (idx != null) {
				for (Tuple t : idx) {
					
					System.out.println("Search " + t.toString());
					answer.add(items.get(t.fileno));
				}
			}
			System.out.print(word);
			for (String f : answer) {
				System.out.print(" " + f);
			}
			System.out.println("");
		}
		
		return answer;
	}

	public ArrayList<String> getUserBooks(String userName)
	{
		return userBookMap.get(userName);
	}
	
	public static void main(String[] args) {
		try {
			InvertedIndex idx = new InvertedIndex();
			String file = "D:\\My Documents\\papers\\조연선\\circulation_data.csv";
			idx.indexFile(new File(file));
			List<String> words = new ArrayList();
			words.add("0004119");
			idx.search(words);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	private class Tuple {
		private int fileno;
		private int position;
 
		public Tuple(int fileno, int position) {
			this.fileno = fileno;
			this.position = position;
		}
		
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append(fileno + " " + position);
			
			return sb.toString();
		}
	}
}
 
 