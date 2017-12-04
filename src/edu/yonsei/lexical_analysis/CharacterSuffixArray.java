package edu.yonsei.lexical_analysis;

import java.util.List;

import com.aliasi.suffixarray.CharSuffixArray;

public class CharacterSuffixArray {

	public CharacterSuffixArray()
	{
	}
	
	public void printSuffixArray(String text)
	{
		System.out.printf("\n%5s %5s  %s\n", "Idx", "Pos", "Suffix");
        
        /*x CharSuffixArrayDemo.1 */
        CharSuffixArray csa = new CharSuffixArray(text);
        
        for (int i = 0; i < csa.suffixArrayLength(); ++i) {
            int pos = csa.suffixArray(i);
            String suffix = csa.suffix(pos,Integer.MAX_VALUE);
            System.out.printf("%5d %5d  %s\n",i,pos,suffix);
        }        

        System.out.printf("\nRepeated Substrings of Length > 1\n");
        for (int len = csa.suffixArrayLength() - 1; len > 1; --len) {
            List<int[]> matchSpans = csa.prefixMatches(len);
            if (matchSpans.size() == 0) continue;
            for (int[] matchSpan : matchSpans) {
                int first = matchSpan[0];
                int last = matchSpan[1];
                for (int i = first; i < last; ++i) {
                    int pos = csa.suffixArray(i);
                    String match = csa.suffix(pos,len);
                    System.out.printf("start=%3d    len=%3d    %s\n", pos, len, match);
                }
                System.out.println();
            }
        }
	}
	
	public void printTextPos(String text) {
        System.out.println("TEXT:\n" + text);
        for (int i = 0; i <= text.length(); ++i)
            System.out.print(Integer.toString(i%10));
        System.out.println();
        if (text.length() > 10) 
            for (int i = 0; i <= text.length(); ++i)
                System.out.print((i % 10 == 0) ? Integer.toString(i/10 % 10) : " ");
        System.out.println();
    }
	
	public static void main(String[] args)
	{
		String text = "오늘 우리에게 주어진 추억의 시간";
		CharacterSuffixArray suffix_array = new CharacterSuffixArray();
		suffix_array.printSuffixArray(text);
	}

}
