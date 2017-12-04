package edu.yonsei.test.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OneHtmlParser {

	public OneHtmlParser()
	{	
	}
	
	public void parseHtml(String htmlFile) throws Exception
	{
		URL url = new URL(htmlFile);
		Document doc = Jsoup.parse(url, 1000);
		
		System.out.println(doc.html());
		
		Elements links = doc.getElementsByTag("a");
		//for (Element link : links) {
	    for (int i = 0; i < links.size(); ++i) {
	      Element link = links.get(i);
		  String linkHref = link.attr("href");
		  String linkText = link.text();
		  System.out.println("LinK " + linkHref + " Text " + linkText);
		}		
		
	}
	
	public static void main(String[] args) throws Exception {
		//File input = new File("./input.html");

		String inputFile = "./url_list.txt";
		FileInputStream fstream = new FileInputStream(inputFile);
	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String sent_line;
	    //Read File Line By Line

	    OneHtmlParser ohp = new OneHtmlParser();
	    
	    while ((sent_line = br.readLine()) != null)   {
	    	if (sent_line.length() > 0) {
	    	
	    		System.out.println("Line: " + sent_line);
	    		ohp.parseHtml(sent_line);
	    	}
	    		
	    }	
	}
}
