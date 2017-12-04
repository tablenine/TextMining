package edu.yonsei.apriori;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class BookRecommendationManager {

	HashMap<String,ArrayList<String>> recommendedBookMap = new HashMap();
	public BookRecommendationManager()
	{
	}
	
	public void parseAprioriResults(String file) throws FileNotFoundException
	{
		Scanner s = new Scanner(new FileReader(file));
		while(s.hasNext()) {
			String line = s.nextLine();
			String[] split = line.split("\t");
			for (int i = 0; i < split.length-1; ++i) {
				String w = split[i];
				
				for (int j = 0; j < split.length-1; ++j) {
					String x = split[j];
					if (!w.equals(x)) {
						if (!recommendedBookMap.containsKey(w)) {
							ArrayList<String> list = new ArrayList();
							list.add(x);
							recommendedBookMap.put(w, list);
						} else {
							if (!recommendedBookMap.get(w).contains(x)) {
								recommendedBookMap.get(w).add(x);
							}
						}
					}
				}
				
			}
		}
	}
	
	public void print()
	{
		System.out.println(recommendedBookMap);
	}
	
	public boolean isContained(String id)
	{
		return recommendedBookMap.containsKey(id);
	}
	
	public ArrayList<String> getRecommendedBooks(String id)
	{
		return recommendedBookMap.get(id);
	}
	
	
	public static void main(String[] args) throws Exception
	{
		String file = "C:\\Program Files (x86)\\Apache Software Foundation\\Tomcat 8.5\\webapps\\lis3306\\recommendation_lib.txt";
		BookRecommendationManager manager = new BookRecommendationManager();
		manager.parseAprioriResults(file);
		
		String user_id = "박준수";
		user_id = user_id.trim();
		ArrayList<String> books = null;
		if (manager.isContained(user_id)) {
			books = manager.getRecommendedBooks(user_id);
		} else {
			InvertedIndex idx = new InvertedIndex();
			String d_file = "C:\\Program Files (x86)\\Apache Software Foundation\\Tomcat 8.5\\webapps\\lis3306\\circulation_data.csv";
			idx.indexFile(new File(d_file));
			
			books = idx.getUserBooks(user_id);
			System.out.println("Books by the USER ==> " + books);
			
			if (books != null) {
				Set<String> users = idx.searchWords(books);
				
				for (String user : users) {
					if (manager.isContained(user)) {
						books = manager.getRecommendedBooks(user);
						break;
					}
				}
			}	
		}
		
		
		String html_result = "";
		
		if (books != null) {
			for (String book : books) {
				html_result += book + "\t";
			}
		} else {
			html_result = "추천할 책이 없습니다.";
		}
		
		System.out.println("Recommended books ==> " + html_result);
		
	}
}
