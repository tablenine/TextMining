package edu.yonsei.test.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;


public class NaverAPIManager {

	final String key = "";
	public NaverAPIManager()
	{
	
	}
	
	public void collectDataV2(String query) throws Exception
	{
		query = URLEncoder.encode(query,"UTF-8");
		
		String clientId = "3XEpv6fTKdWh_Hr3tn8P";
	    String clientSecret = "rKsyEHWcLf";
	    //REST API
	    //String url = "https://openapi.naver.com/v1/search/movie.xml?display=100&start=1&genre=1&query="+ query;
	    String url = "https://openapi.naver.com/v1/search/blog.xml?display=100&query="+query;
	    for (int i = 0; i < 100; ++i) {
	    	//String url = "https://openapi.naver.com/v1/search/news.xml?display=100&query="+query+"&start="+new Integer(i).toString();
	      
		    HttpClient client = new DefaultHttpClient();
		    HttpGet request = new HttpGet(url);
		     
		    request.addHeader("X-Naver-Client-Id", clientId);
		    request.addHeader("X-Naver-Client-Secret", clientSecret);
		    HttpResponse response = client.execute(request);	
		     
		    BufferedReader rd = new BufferedReader(
		                new InputStreamReader(response.getEntity().getContent()));
	
		    StringBuffer result = new StringBuffer();
		    String line = "";
		    while ((line = rd.readLine()) != null) {
		    	 result.append(line);
		    }
		     
		     System.out.println("Result" + result.toString());
		     //for html data
		     //Document rss = Jsoup.parse(result.toString());
		     
		     //for xml
		     parseBlogXML(result.toString());
	    }
	}
	
	public void collectData(String query, String outputFile) throws Exception
	{		
		String initUrl = "http://openapi.naver.com/search?"
				+ "key=" + key
				+ "&query=" + query
				+ "&target=news&start=1&display=1";
		
		// RSS feed default parsing
		Document rss = Jsoup.connect(initUrl).get();
		String s = rss.select("total").text();
		int total = Integer.parseInt(s);
		
		System.out.println("Total results >> " + total);

		String fileDir = "./data/news/" + query + ".txt";
		
		for (int i=1; i<=total; i++) {
			try {
				String parseUrl = "http://openapi.naver.com/search?"
						+ "key=" + key
						+ "&query=" + query
						+ "&target=news&start=" + i
						+ "&display=1";
				rss = Jsoup.connect(parseUrl).get();
				String title = rss.select("item > title").text();
				String originallink = rss.select("item > originallink").text();
				String link = rss.select("item > link").text();
				String pubDate = rss.select("item > pubDate").text();
				
				// Matching keyword extraction from RSS description between triple dot (...)
				String desc = rss.select("item > description").text().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
				desc = desc.replaceAll("&quot;", "\"");
				String desc2 = desc.replaceAll("[.]{2,}", "");
					
				System.out.println("=====================");
				System.out.println(desc2);
				
				Thread.sleep(3000);
				
				Document article = Jsoup.connect(originallink).get();
				Elements node = article.getElementsContainingOwnText(desc2);
				Element item = node.first();
				
				if (item == null) continue;
				
				String text = item.parent().text().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
				
				System.out.println("original link >> " + originallink);
				
				FileWriter fstream = new FileWriter(fileDir, true);
				BufferedWriter writer = new BufferedWriter(fstream);
					
				writer.write(title + "$$" + originallink + "$$" + link + "$$" + text + "$$" + pubDate + "$$" + query + "\n");
				writer.close();
					
				System.out.println("Completed!");	
				
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
	}
	
	public void parseXML(String contents)
	{
		try {	
			File field_file = 
					new File("./naver_results.txt");
			FileWriter fstream = 
					new FileWriter(field_file,true);
			BufferedWriter out = 
					new BufferedWriter(fstream);
			
	        DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         //org.w3c.dom.Document doc = dBuilder.parse(contents);
	         InputSource is = new InputSource();
	         is.setCharacterStream(new StringReader(contents));
	         org.w3c.dom.Document doc = dBuilder.parse(is);
	         
	         doc.getDocumentElement().normalize();
	         System.out.println("Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("item");
	         System.out.println("----------------------------");
	         
	         String link, title, userRating, actor, 
	                 pubDate = "";
	         
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            System.out.println("\nCurrent Element :" 
	               + nNode.getNodeName());
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
	               //System.out.println("Student roll no : "  + eElement.getAttribute("rollno"));
	               link = eElement
	 	                  .getElementsByTagName("link")
		                  .item(0)
		                  .getTextContent();
	            	System.out.println("Link : " 
	                  + link);
	               
	               //get full html
	               String summary = getFullHtml(link);
	               System.out.println("summary " + summary);
	               
	               userRating = eElement
	 	                  .getElementsByTagName("userRating")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("userRating : " 
	               + userRating);
	               
	               title = eElement
	 	                  .getElementsByTagName("title")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("Title : " 
	               + title);
	               
	               pubDate = eElement
	 	                  .getElementsByTagName("pubDate")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("pubDate : " 
	               + pubDate);
	               
	               actor = eElement
	 	                  .getElementsByTagName("actor")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("Actor : " 
	               + actor);
	               
	               if (link.length() > 0 && title.length() > 0 
	            		    && pubDate.length() > 0
	            		    && actor.length() > 0) {	
	            	   out.write(link + "\t" + title 
	            			   + "\t" + pubDate 
	            			   + "\t" + actor 
	            			   + "\t" + summary +"\n");	
	               }
	               
	            } // end of if 
	         } //end of outer for loop
	         
	         out.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }	
	}
	

	
	
	public void parseBlogXML(String contents)
	{
		try {	
			File field_file = 
					new File("./naver_blog_results.txt");
			FileWriter fstream = 
					new FileWriter(field_file,true);
			BufferedWriter out = 
					new BufferedWriter(fstream);
			
	        DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         //org.w3c.dom.Document doc = dBuilder.parse(contents);
	         InputSource is = new InputSource();
	         is.setCharacterStream(new StringReader(contents));
	         org.w3c.dom.Document doc = dBuilder.parse(is);
	         
	         doc.getDocumentElement().normalize();
	         System.out.println("Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("item");
	         System.out.println("----------------------------");
	         
	         String link, title, description, bloggername, 
	                 bloggerlink = "";
	         
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            System.out.println("\nCurrent Element :" 
	               + nNode.getNodeName());
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
	               //System.out.println("Student roll no : "  + eElement.getAttribute("rollno"));
	               link = eElement
	 	                  .getElementsByTagName("link")
		                  .item(0)
		                  .getTextContent();
	            	System.out.println("Link : " 
	                  + link);
	               
	               //get full html
	               String summary = getFullHtml(link);
	               System.out.println("summary " + summary);
	               
	               description = eElement
	 	                  .getElementsByTagName("description")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("description : " 
	               + description);
	               
	               title = eElement
	 	                  .getElementsByTagName("title")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("Title : " 
	               + title);
	               
	               bloggername = eElement
	 	                  .getElementsByTagName("bloggername")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("bloggername : " 
	               + bloggername);
	               
	               bloggerlink = eElement
	 	                  .getElementsByTagName("bloggerlink")
		                  .item(0)
		                  .getTextContent();
	               System.out.println("bloggerlink : " 
	               + bloggerlink);
	               

	            	   out.write(link + "\t" + title 
	            			   + "\t" + description 
	            			   + "\t" + bloggername  
	            			   + "\t" + bloggerlink
	            			   +"\n");	
	               	               
	            } // end of if 
	         } //end of outer for loop
	         
	         out.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }	
	}
	
	
	public String getFullHtml(String url) throws Exception
	{
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		String title = doc.title();
		Elements links = doc.select("a[href]");
		System.out.println("Links:" + links.size());
        for (Element link : links) {
        	System.out.println(" Text " + link.text() 
        	                    + " Link " + link.attr("href"));
        }
	    //System.out.println("Full HTML Page" + doc.toString());
	    
        Element summary = doc.select("p.con_tx").first();
        if (summary != null) {
        	return summary.text();
        }
        
        return "";
	}
	
	public void getMusic() throws Exception
	{
		String url = "http://apis.skplanetx.com/melon/albums?version=1&page=1&count=10&searchKeyword=love";
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		String title = doc.title();
		Elements links = doc.select("a[href]");
		System.out.println("Doc " + doc.text());
	}
	
	public static void main(String[] args)
	{
		try {
			NaverAPIManager napim = new NaverAPIManager();
			
			String query = "김영란법 시행";
			
			napim.collectDataV2(query);
			//napim.getMusic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}










