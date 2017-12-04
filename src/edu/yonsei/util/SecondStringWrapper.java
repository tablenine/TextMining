package edu.yonsei.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.njit.textmining.distance.SoftPulling4HistDiff;
import edu.njit.textmining.distance.lookup.SoftDictionary;
import edu.njit.textmining.distance.lookup.SoftDictionary.MyWrapper;

public class SecondStringWrapper {
	public static SoftDictionary dict = null;
	
	static public void init(String fileName) throws IOException,ClassNotFoundException
    {
        long start0 = System.currentTimeMillis();
        if (fileName.endsWith(".list")) {
            System.out.println("loading aliases...");
            dict = new SoftDictionary();
            dict.load(new File(fileName));
            
        } 
        
        double elapsedSec0 = (System.currentTimeMillis()-start0) / 1000.0;
        System.out.println("loaded in "+elapsedSec0+" sec");
        long start1 = System.currentTimeMillis();
        
    }
	
	
	public static void main(String[] args) throws Exception
	{		
		String query = "Yonsei University Severance Hospital";
		String dic_file = "D:\\workspace\\pubmed_central_miner\\topN_organization.list";

		String org_only_dic_file = "org_only_topN_organization.list";
	
		init(org_only_dic_file);

		System.out.println(dict.lookupDistance(query) + " : " + ((MyWrapper)dict.lookup(query)).unwrap());
		
	}
}
