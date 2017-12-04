package edu.yonsei.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

////////////////////////////////////////////////////////////////////////
// 한글 문자열을 초성/중성/종성으로 분리하여 출력 (UTF-8 버전)
// v1.0
////////////////////////////////////////////////////////////////////////

public class Jaso {

                                 // ㄱ      ㄲ      ㄴ      ㄷ      ㄸ      ㄹ      ㅁ      ㅂ      ㅃ      ㅅ      ㅆ      ㅇ      ㅈ      ㅉ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
  final static char[] ChoSung   = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
                                 // ㅏ      ㅐ      ㅑ      ㅒ      ㅓ      ㅔ      ㅕ      ㅖ      ㅗ      ㅘ      ㅙ      ㅚ      ㅛ      ㅜ      ㅝ      ㅞ      ㅟ      ㅠ      ㅡ      ㅢ      ㅣ
  final static char[] JwungSung = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
                                 //         ㄱ      ㄲ      ㄳ      ㄴ      ㄵ      ㄶ      ㄷ      ㄹ      ㄺ      ㄻ      ㄼ      ㄽ      ㄾ      ㄿ      ㅀ      ㅁ      ㅂ      ㅄ      ㅅ      ㅆ      ㅇ      ㅈ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
  final static char[] JongSung  = { 0,      0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };


  public static final char[] CHO = 
			/*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
		{0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
			0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
  public static final char[] JUN = 
			/*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
		{0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
			0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160,	0x3161,	0x3162,
			0x3163};
			/*X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
  public static final char[] JON = 
		{0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
			0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
			0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

  private ArrayList<String> jonsungList = new ArrayList();		
  
  public Jaso()
  { 
	  //ㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ
	  jonsungList.add("X");
	  jonsungList.add("ㄱ");
	  jonsungList.add("ㄲ");
	  jonsungList.add("ㄳ");
	  jonsungList.add("ㄴ");
	  jonsungList.add("ㄵ");
	  jonsungList.add("ㄶ");
	  jonsungList.add("ㄷ");
	  jonsungList.add("ㄹ");
	  jonsungList.add("ㄺ");
	  jonsungList.add("ㄻ");
	  jonsungList.add("ㄼ");
	  jonsungList.add("ㄽ");
	  jonsungList.add("ㄾ");
	  jonsungList.add("ㄿ");
	  jonsungList.add("ㅀ");
	  jonsungList.add("ㅁ");
	  jonsungList.add("ㅂ");
	  jonsungList.add("ㅄ");
	  jonsungList.add("ㅅ");
	  jonsungList.add("ㅆ");
	  jonsungList.add("ㅇ");
	  jonsungList.add("ㅈ");
	  jonsungList.add("ㅊ");
	  jonsungList.add("ㅋ");
	  jonsungList.add("ㅌ");
	  jonsungList.add("ㅍ");
	  jonsungList.add("ㅎ");
  }
  
  public int getIndex(String a)
  {
	  return jonsungList.indexOf(a);
  }
  
  public boolean isJongSungOrChoSung(String a)
  {
	  return jonsungList.contains(a);
  }
  
  public String concatedString(String word1, String word2)
  {
	  List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
	  String concatedString = "";
	  
	  char temp = 0;
	  System.out.println(word1);
	  for(int i = 0 ; i < word1.length();i++)
		{
			Map<String, Integer> map = new HashMap<String, Integer>();
			char test = word1.charAt(i);
						
			if(test >= 0xAC00)
			{
				char uniVal = (char) (test - 0xAC00);
				
				char cho = (char) (((uniVal - (uniVal % 28))/28)/21);
				char jun = (char) (((uniVal - (uniVal % 28))/28)%21);
				char jon = (char) (uniVal %28);
				
				map.put("cho", (int) cho);
				map.put("jun", (int) jun);
				map.put("jon", (int) jon);
				list.add(map);
				
				temp = (char)(0xAC00 + 28 * 21 *(cho) + 28 * (jun) + (jon));
				concatedString.concat(String.valueOf(temp));	
			}
		}


		for(int i = 0; i < list.size()-1; i++)
		{
			int a = (int)(list.get(i)).get("cho");
			int b = (int)(list.get(i)).get("jun");
			int c = (int)(list.get(i)).get("jon");
			
			char temp1 = (char)(0xAC00 + 28 * 21 *(a) + 28 * (b) + (c) );
			
			concatedString = concatedString.concat(String.valueOf(temp1));			
		}
		
		//System.out.println(":: "+ concatedString);
		char i = (char)getIndex(word2);
		char tem = new Character(i).charValue();
		
		int a = (int)(list.get(list.size()-1)).get("cho");
		int b = (int)(list.get(list.size()-1)).get("jun");
		int c = (int)(list.get(list.size()-1)).get("jon");
		char temp1 = (char)(0xAC00 + 28 * 21 *(a) + 28 * (b) + (c) );
		char d = (char)temp1;
		
		int temp2 = d + tem;
		concatedString = concatedString.concat(String.valueOf((char)temp2));		
		//System.out.println(""+ concatedString);
		
	  
	  return concatedString;
  }
  
  public static void main( String [] arg )throws Exception {
		
	  	Jaso jaso = new Jaso();

		//String tempStr = "안녕하세요";
		String word1 = "동우";
		String word2 = "ㄱ";
		
		String y = jaso.hangulToJaso(word1);
		boolean jongOrCho = jaso.isJongSungOrChoSung(word2);
		
		if (jongOrCho) {
			String concated = jaso.concatedString(word1, word2);
			System.out.println("XXX " + concated + " :: " + jongOrCho + " :: " + y);
		}
	}
  
	public String hangulToJaso(String s) { // 유니코드 한글 문자열을 입력 받음
	  int a, b, c; // 자소 버퍼: 초성/중성/종성 순
	  String result = "";
	
	  for (int i = 0; i < s.length(); i++) {
	    char ch = s.charAt(i);
	
	    if (ch >= 0xAC00 && ch <= 0xD7A3) { // "AC00:가" ~ "D7A3:힣" 에 속한 글자면 분해
	
		      c = ch - 0xAC00;
		      a = c / (21 * 28);
		      c = c % (21 * 28);
		      b = c / 28;
		      c = c % 28;
		
		      result = result + ChoSung[a] + JwungSung[b];
		      if (c != 0) result = result + JongSung[c] ; // c가 0이 아니면, 즉 받침이 있으면
	    } else {
	      result = result + ch;
	    }
	  }
	  return result;
	}


}