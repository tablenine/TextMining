package edu.yonsei.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.njit.textmining.distance.lookup.SoftDictionary.MyWrapper;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CoreNLPNERExtractor {

	AbstractSequenceClassifier<CoreLabel> classifier = null;

	ArrayList<String> majorCountryList = new ArrayList();
	String cityCountryMappingFile = null;
	HashMap<String,String> cityCountryMap = new HashMap();
	HashMap<String,String> countryList = new HashMap();
	ArrayList<String> usaStateList = new ArrayList();
	ArrayList<String> ucCityList = new ArrayList();
	
	public CoreNLPNERExtractor(String config)
	{
		classifier = CRFClassifier.getClassifierNoExceptions(config);
		//
		buildMajorCountryList();
		
		//
		buildUCCityList();
		String dic_file = "topN_organization.list";
		String org_only_dic_file = "org_only_topN_organization.list";
		try {
			SecondStringWrapper.init(org_only_dic_file);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public void buildCityCountryMapping(String cityCountryMappingFile) throws Exception
	{
		FileInputStream fstream = new FileInputStream(cityCountryMappingFile);
	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String sent_line;
	    //Read File Line By Line

	    while ((sent_line = br.readLine()) != null)   {
	    	if (sent_line.length() > 0) {
	    		String[] split = sent_line.split(",");
	    		String country = split[1];
	    		String city = split[3];
	    		if (city.trim().length() > 0) {
		    		if (!cityCountryMap.containsKey(city.trim())) {
		    			cityCountryMap.put(city.trim(), country.trim());
		    		} else {
		    			if (country.trim().equals("US")) {
		    				cityCountryMap.remove(city.trim());
		    				cityCountryMap.put(city.trim(), country.trim());
		    			}
		    		}
	    		}
	    	}
	    }
	    
	    FileInputStream fstream1 = new FileInputStream("./country_two_digit.csv");
	    // Get the object of DataInputStream
	    DataInputStream in1 = new DataInputStream(fstream1);
	    BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
	    sent_line = "";
	    //Read File Line By Line

	    while ((sent_line = br1.readLine()) != null)   {
	    	if (sent_line.length() > 0) {
	    		String[] split = sent_line.split(",");
	    		if (split.length > 1) {
	    			String name = split[0];
	    			name = name.replaceAll("\"", "");
	    			String code = split[1];
	    			code = code.replaceAll("\"","");
	    			countryList.put(code.trim(), name.trim());
	    		}
	    	}
	    }
	    
	    br1.close();
	    
	    FileInputStream fstream2 = new FileInputStream("usa_states.csv");
	    // Get the object of DataInputStream
	    DataInputStream in2 = new DataInputStream(fstream2);
	    BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
	    sent_line = "";
	    //Read File Line By Line

	    while ((sent_line = br2.readLine()) != null)   {
	    	if (sent_line.length() > 0) {
	    		//System.out.println("name " + sent_line);
	    		String[] split = sent_line.split(",");
	    		if (split.length > 1) {
	    			String name = split[0];
	    			name = name.replaceAll("\"", "");
	    			String abbrev = split[1];
	    			abbrev = abbrev.replaceAll("\"", "");
	    			usaStateList.add(name.trim());
	    			usaStateList.add(abbrev.trim());
	    		}
	    	}
	    }
	    
	    br2.close();
	    
	    
	}
	
	public String getLocationOrganization(String affiliation)
	{
		String result = "";
		Collection<String> coll = getLocationOrganizationList(affiliation).values();
		
		if (coll.size()>0) {
			ArrayList<String> list = new ArrayList(coll);
			for (int i = 0; i < list.size(); ++i) {
				result += list.get(i) + "\t";
			}
		}
		
		return result;
	}
	
	public HashMap<String,String> getLocationOrganizationList(String affiliation)
	{
		HashMap<String,String> results = new HashMap();
    	String ner_results = classifier.classifyWithInlineXML(affiliation);
		//System.out.println(ner_results);
		
		//organization match
		Pattern pattern = Pattern.compile("<ORGANIZATION>(.+?)</ORGANIZATION>");
		Matcher matcher = pattern.matcher(ner_results);
		
		String organization = "";
		while (matcher.find()) {
			if (!matcher.group(1).equals("null") && matcher.group(1).trim().length() > 0) {
				organization = matcher.group(1);
				if (organization.toLowerCase().contains("univ") 
						|| organization.toLowerCase().contains("founda")
						|| organization.toLowerCase().contains("institute of")) {
					break;
				}
				
				if (organization.toLowerCase().contains("acade") && !affiliation.toLowerCase().contains("univer")) {
					break;
				}
				
				if (organization.toLowerCase().contains("instit") 
						&& (!affiliation.toLowerCase().contains("univer") && !affiliation.toLowerCase().contains("institute of"))) {
					break;
				}
				
				if (organization.toLowerCase().contains("corporation")) {
					break;
				}
			}
		}
		
		//person match
		String person = "";
		Pattern _pattern = Pattern.compile("<PERSON>(.+?)</PERSON>");
		Matcher _matcher = _pattern.matcher(ner_results);

		while (_matcher.find()) {
			if (!_matcher.group(1).equals("null") && _matcher.group(1).trim().length() > 0) {
				person = _matcher.group(1);
			}
		}
		
		if (person.length() > 0) {
			results.put("per",person);
		}
		
		//location match
		pattern = Pattern.compile("<LOCATION>(.+?)</LOCATION>");
		matcher = pattern.matcher(ner_results);
		String location = "";
		while (matcher.find()) {
			if (!matcher.group(1).equals("null") && matcher.group(1).trim().length() > 0) {
				location = matcher.group(1);			
			}
			
			if (location.toLowerCase().contains("hospi") && organization.length() < 1) {
				organization = location;
			}
		}

		if (organization.length() < 1) {
			String[] split = affiliation.split(",");
			for (int i = 0; i < split.length; ++i) {
				if (split[i].toLowerCase().contains("cent") || split[i].toLowerCase().contains("univ")) {
					organization = split[i];
					break;
				} else {
					organization = split[0];
				}
			}
		}
		
		if (organization.contains("Paris")) {
			location = "France";
		}
		
		if (organization.length() > 0) {
			organization = organization.replaceAll("\\.", "");
			
			if (organization.equals("University of California")) {
				for (int i = 0; i < ucCityList.size(); ++i) {
					String uc = ucCityList.get(i);
					if (affiliation.contains(uc)) {
						organization = organization + " " + uc;
					}
				}
			}
			
			results.put("org",organization);
		}
		
		if (location.length() > 0) {
			if (location.equals("Korea")) {
				location = "South Korea";
			}
			
			if (location.toLowerCase().equals("united kingdom")) {
				location = "UK";
			}
			
			if (location.toLowerCase().contains("united states")) {
				location = "USA";
			}
			
			if (location.toLowerCase().contains("u.s.a.")) {
				location = "USA";
			}
			
			if (location.toLowerCase().contains("china")) {
				location = "China";
			}
			
			if (location.toLowerCase().contains("deutschland")) {
				location = "Germany";
			}
			
			if (!majorCountryList.contains(location)) {
				if (cityCountryMap.containsKey(location)) {
					location = cityCountryMap.get(location);
					
					if (countryList.containsKey(location)) {
						location = countryList.get(location);
					} else if (usaStateList.contains(location)) {
						location = "USA";
					}
					
 				}
			}
			
			results.put("loc",location);
		}
		
		System.out.println(organization + "\t" + location); 
		
		return results;
	}

	private String postprocess(String affiliation)
	{
		String post_processed = affiliation;
		
		if(affiliation.contains("Unive rsity")) {
			post_processed = affiliation.replaceAll("Unive rsity", "University");
		}

		if (affiliation.length() > 1 && StringUtils.isNumeric(affiliation.substring(0,1))) {
			post_processed = affiliation.replaceAll("\\d+\\s{0,}\\d{0,}","");
			//System.out.println("XXX " + affiliation.trim());	
		}
		
		return post_processed;
	}
	
	public HashMap<String,String> getLocationOrganizationListWithID(String id, String affiliation)
	{
		//postprocessing for bad tokens
		affiliation = postprocess(affiliation);
		
		//System.out.println("AFF " + affiliation);
		HashMap<String,String> results = new HashMap();
    	String ner_results = classifier.classifyWithInlineXML(affiliation);
		//System.out.println(ner_results);
		
		//organization match
		Pattern pattern = Pattern.compile("<ORGANIZATION>(.+?)</ORGANIZATION>");
		Matcher matcher = pattern.matcher(ner_results);
		
		String organization = "";
		while (matcher.find()) {
			if (!matcher.group(1).equals("null") && matcher.group(1).trim().length() > 0) {
				organization = matcher.group(1);
				
				//System.out.println("XXXX " + matcher.group());
				
				if (organization.toLowerCase().contains("univ") 
						|| organization.toLowerCase().contains("founda")
						|| organization.toLowerCase().contains("institute of")) {
					break;
				}
				
				if (organization.toLowerCase().contains("acade") && !affiliation.toLowerCase().contains("univer")) {
					break;
				}
				
				if (organization.toLowerCase().contains("instit") 
						&& (!affiliation.toLowerCase().contains("univer") && !affiliation.toLowerCase().contains("institute of"))) {
					break;
				}
				
				if (organization.toLowerCase().contains("corporation")) {
					break;
				}
			}
		}
	
		if (organization.length() < 1) {
			String[] split = affiliation.split(",");
			for (int i = 0; i < split.length; ++i) {
				if (split[i].toLowerCase().contains("cent") || split[i].toLowerCase().contains("univ")) {
					organization = split[i];
					break;
				} else {
					organization = split[0];
				}
			}
		}
		
		//person match
		String person = "";
		Pattern _pattern = Pattern.compile("<PERSON>(.+?)</PERSON>");
		Matcher _matcher = _pattern.matcher(ner_results);

		while (_matcher.find()) {
			if (!_matcher.group(1).equals("null") && _matcher.group(1).trim().length() > 0) {
				person = _matcher.group(1);
			}
		}
		
		results.put("per",person);

		//location match
		pattern = Pattern.compile("<LOCATION>(.+?)</LOCATION>");
		matcher = pattern.matcher(ner_results);
		String location = "";
		while (matcher.find()) {
			if (!matcher.group(1).equals("null") && matcher.group(1).trim().length() > 0) {
				location = matcher.group(1);			
			}
			
			if (location.toLowerCase().contains("hospi") && organization.length() < 1) {
				organization = location;
			}
		}

		if (organization.length() > 0) {
			organization = organization.replaceAll("\\.", "");
			
			if (organization.equals("University of California")) {
				for (int i = 0; i < ucCityList.size(); ++i) {
					String uc = ucCityList.get(i);
					if (affiliation.contains(uc)) {
						organization = organization + " " + uc;
					}
				}
			}
			
			if (organization.equals("Yonsei University College of Medicine")) {
				organization = "Yonsei University";
			}
			
			if (organization.equals("UCLA")) {
				organization = "University of California Los Angles";
			}
		
			if (organization.equals("National Institute")) {
				organization = "National Institute of Health";
			}
			
			if (organization.equals("NIH")) {
				organization = "National Institute of Health";
			}
			
			if (organization.equals("Inc")) {
				organization = affiliation.substring(0, affiliation.indexOf("Inc"));
			}
			
			try {
				if (organization.startsWith("Depart") || organization.startsWith("Dept")) {
					//organization = organization.replace("\\(", "").replace("\\)","");
					//String _org = affiliation.replace("\\(", "").replace("\\)","").replaceAll(organization, "");
					organization = organization.replaceAll("\\(", "").replaceAll("\\)","");
			
					if (affiliation.contains("(")) {
						affiliation = affiliation.replaceAll("\\(", "");
					}
					
					if (affiliation.contains(")")) {
						affiliation = affiliation.replaceAll("\\)", "");
					}
					
					String _org = affiliation.replaceAll(organization, "");
					_org = _org.trim();
					if (_org.startsWith(",")) {
						_org = _org.replaceFirst(",", "").trim();
					}
					
					//System.out.println("XXX " + _org);
					if (_org.contains("Center") || _org.contains("Centre") || _org.contains("Univ")
							|| _org.contains("Instit") || _org.contains("Hospi") || _org.contains("College")) {
						int pos = _org.indexOf(",");
						if (pos < 0) {
							pos = _org.lastIndexOf("\\s+");
						}
						
						if (pos < 0) {
							pos = _org.length()-1;
						}
						
						organization = _org.substring(0, pos);
						organization.replaceAll(",", "");
						organization = organization.trim();
					}	
				}
				
				if (organization.equals("University Hospital")) {
					String[] a_split = affiliation.split("\\s+");
					for (int i = 0; i < a_split.length; ++i) {
						if (cityCountryMap.containsKey(a_split[i])) {
							organization = organization + " " + a_split[i];
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (organization.equals("The University of Texas M D Anderson Cancer Center")) {
				organization = "The University of Texas MD Anderson Cancer Center";
			}
			
			if (organization.equals("University of Texas MD Anderson Cancer Center")) {
				organization = "The University of Texas MD Anderson Cancer Center";					
			}
	
			double result = SecondStringWrapper.dict.lookupDistance(organization);
	 		if (result > 0.85) {
	 			organization = ((MyWrapper)SecondStringWrapper.dict.lookup(organization)).unwrap();
	 		}
	
			
			results.put("org",organization);
		}
		
		//if (organization.contains("Paris") || location.length() < 1) {
			//location = "France";
		//}
		
		//System.out.println(id + "::" +organization + "::" + location); 
		
		if (location.length() > 0) {
			if (location.equals("South Korea")) {
				location = "Korea";
			}
			
			if (location.toLowerCase().equals("united kingdom")) {
				location = "UK";
			}
			
			if (location.toLowerCase().contains("united states")) {
				location = "USA";
			}
			
			if (location.toLowerCase().contains("u.s.a.")) {
				location = "USA";
			}
			
			if (location.toLowerCase().contains("u.k.")) {
				location = "UK";
			}
			
			if (location.toLowerCase().contains("china")) {
				location = "China";
			}
			
			if (location.toLowerCase().contains("deutschland")) {
				location = "Germany";
			}
			
			if (usaStateList.contains(location)) {
				location = "USA";
			}
			
			if (location.toLowerCase().contains("netherlands")) {
				location = "The Netherlands";
			}
			
			if (!majorCountryList.contains(location)) {
				if (cityCountryMap.containsKey(location)) {
					location = cityCountryMap.get(location);
					
					if (countryList.containsKey(location)) {
						location = countryList.get(location);
					} 
					
					if (usaStateList.contains(location)) {
						location = "USA";
					}
					
					if (location.split("\\s+").length > 1) {
						if (usaStateList.contains(location.split("\\s+")[0])) {
							location = "USA";
						}
					}
					
					//System.out.println(" YYYYYYYYYYYY " + location);
 				}
			}
			
			
			results.put("loc",location);
			
			
		}
		
		//System.out.println(id + "::" +organization + "::" + location); 
		
		return results;
	}
	
	public void buildMajorCountryList()
	{
		majorCountryList.add("USA");
		majorCountryList.add("UK");
		majorCountryList.add("France");
		majorCountryList.add("China");
		majorCountryList.add("Spain");
		majorCountryList.add("Germany");
		majorCountryList.add("The Netherlands");
		majorCountryList.add("Japan");
		majorCountryList.add("South Korea");
		majorCountryList.add("Italy");
		majorCountryList.add("Sweden");
		majorCountryList.add("Canada");
		majorCountryList.add("Mexico");
		majorCountryList.add("Switzerland");
		majorCountryList.add("India");
		majorCountryList.add("Australia");
		majorCountryList.add("Singapore");
		majorCountryList.add("Taiwan");
		majorCountryList.add("Finland");
		majorCountryList.add("Poland");
		majorCountryList.add("Brasil");
		majorCountryList.add("Belgium");
	}
	
	public void buildUCCityList()
	{
		ucCityList.add("Berkeley");
		ucCityList.add("Davis");
		ucCityList.add("Irvine");
		ucCityList.add("Los Angeles");
		ucCityList.add("Merced");
		ucCityList.add("Riverside");
		ucCityList.add("La Jolla, San Diego");
		ucCityList.add("Santa Barbara-Goleta");
		ucCityList.add("Santa Cruz");
		ucCityList.add("San Francisco");
	}
	
	public boolean isMajorCountry(String country)
	{
		return majorCountryList.contains(country);
	}
	
	public boolean isUSState(String city)
	{
		return usaStateList.contains(city);
	}
	
	public boolean isWorldCity(String city)
	{
		return cityCountryMap.containsKey(city);
	}
	
	public String getWorldCountry(String city)
	{
		return cityCountryMap.get(city);
	}
	
	public boolean isFullCountryName(String abbrevName)
	{
		return countryList.containsKey(abbrevName);
	}
	
	public String getFullCountryName(String abbrevName)
	{
		return countryList.get(abbrevName);
	}
	
	public ArrayList<String> getUSStateList()
	{
		return usaStateList;
	}
	
	public static void main(String[] args) throws Exception
	{
		/**
		String inputFile = "./mesh_trends_year_ranks.txt";
		VisualizationHelper helper = new VisualizationHelper();
		helper.readMeshYearRankingFile(inputFile);
		helper.print();
		**/
		
		String regex = "[\\d{1,3}\\s{1,1}]";
		
		String affiliation = "Department of (Nuclear Medicine), PET Center, Shin Kong Wu Ho-Su Memorial Hospital, Taipei, Taiwan.";
		
		String config = "aff_models/english.all.3class.distsim.crf.ser.gz";
		CoreNLPNERExtractor extractor = new CoreNLPNERExtractor(config);
		
		//extractor.buildCityCountryMapping("GeoLiteCity-Location.csv");
		
		extractor.getLocationOrganizationList(affiliation);
		
		String sent_line = "24496016::University of Texas MD Anderson Cancer Center::PA USA";
		String[] split = sent_line.split("::");
		if (split.length > 2) {
			String pmid = split[0];
			String _org = split[1].replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit} ]", "");
			_org = _org.trim();
			
			String _loc = split[2];
			_loc = _loc.trim();
			if (_loc.split("\\s+").length > 1) {
				
				if (extractor.isUSState(_loc.split("\\s+")[0])) {
					_loc = "USA";
				}
			} else {
				if (extractor.isUSState(_loc)) {
					_loc = "USA";
				}
				
				if (extractor.isWorldCity(_loc)) {
					_loc = extractor.getWorldCountry(_loc);
				}
			}
		
		
			double d = 0.85;
			double result = SecondStringWrapper.dict.lookupDistance(_org);
	 		if (result > 0.85) {
	 			_org = ((MyWrapper)SecondStringWrapper.dict.lookup(_org)).unwrap();
	 		}
			
			System.out.println(result + " :: " + _org + "==" + _loc + " :: " + _loc.split("\\s+")[0]);
			System.out.println(extractor.getUSStateList());
		}
		
		
	}
}
