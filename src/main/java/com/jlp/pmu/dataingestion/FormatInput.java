package com.jlp.pmu.dataingestion;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jlp.pmu.utility.Utility;

public class FormatInput {

	public FormatInput() {
		// TODO Auto-generated constructor stub
	}
	
	public String refineAscciFileContent(String absPathAsciiFileBeforeFinalFormat)
	{
		Utility utilObj = new Utility();
		String asciiFileContent = utilObj.readFileContent(absPathAsciiFileBeforeFinalFormat);
		
		
		//String asciiFileContent = utilObj.readFileContent("C:\\projects\\PMU\\dev\\ascii\\CBS\\PKPRT3A.23393820.PROCESSED");
		
		//List listOfAllWords = utilObj.getTheListOfAllWordsInLine(asciiFileContent);
		
		
		List listOfAllWords = utilObj.getTheListOfAllWordsInLineWithSpaceAsDelimiter(asciiFileContent);
		Iterator<String> wordsIter = listOfAllWords.iterator();
		String eachWord,wordAfterReplacementOfSpecialCharacter;
		int countOfCapitalLetters,countOfSmallLetters;
		char c;
		int[] specialCharacterIndexArray;
		int indexOfSmallLetters=0,stringLength; 
		
		while(wordsIter.hasNext())
		{
			countOfCapitalLetters=0;
			countOfSmallLetters=0;
			specialCharacterIndexArray = new int[20];
			indexOfSmallLetters=0;
			
			eachWord = wordsIter.next();
			stringLength = StringUtils.length(eachWord);
			
			
			
			if(stringLength>1)
				eachWord=	StringUtils.substringBeforeLast(eachWord, String.valueOf(eachWord.charAt(stringLength-1)));
			wordAfterReplacementOfSpecialCharacter= eachWord;
			
			
			for (int i = 0, n = eachWord.length(); i < n; i++) {
			     c = eachWord.charAt(i);
			     if(Character.isLowerCase(c))
			     {
			    	 countOfSmallLetters++;
			    	 specialCharacterIndexArray[indexOfSmallLetters]=i;
			    	 indexOfSmallLetters++;
			    	 
			     }
			     if(Character.isUpperCase(c))
			    	 countOfCapitalLetters++; 
			}
			
			if(countOfCapitalLetters>countOfSmallLetters&&countOfSmallLetters>0)
			{
				for(int counter =0;counter<specialCharacterIndexArray.length;counter++)
				{
					if(specialCharacterIndexArray[counter]==0&&counter==countOfSmallLetters)
						break;
					wordAfterReplacementOfSpecialCharacter=StringUtils.replace(wordAfterReplacementOfSpecialCharacter, String.valueOf(eachWord.charAt(specialCharacterIndexArray[counter])), " ");
						
				}
				asciiFileContent=StringUtils.replace(asciiFileContent, eachWord, wordAfterReplacementOfSpecialCharacter);
			}
			
		}
		
		
		return asciiFileContent;

	}
/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Utility utilObj = new Utility();
		String asciiFileContent = utilObj.readFileContent("C:\\projects\\PMU\\dev\\ascii\\CBS\\PKPRT3A.23410816.PROCESSED");
		
		
		//String asciiFileContent = utilObj.readFileContent("C:\\projects\\PMU\\dev\\ascii\\CBS\\PKPRT3A.23393820.PROCESSED");
		
		//List listOfAllWords = utilObj.getTheListOfAllWordsInLine(asciiFileContent);
		
		
		List listOfAllWords = utilObj.getTheListOfAllWordsInLineWithSpaceAsDelimiter(asciiFileContent);
		Iterator<String> wordsIter = listOfAllWords.iterator();
		String eachWord,wordAfterReplacementOfSpecialCharacter;
		int countOfCapitalLetters,countOfSmallLetters;
		char c;
		int[] specialCharacterIndexArray;
		int indexOfSmallLetters=0,stringLength; 
		
		while(wordsIter.hasNext())
		{
			countOfCapitalLetters=0;
			countOfSmallLetters=0;
			specialCharacterIndexArray = new int[20];
			indexOfSmallLetters=0;
			
			eachWord = wordsIter.next();
			stringLength = StringUtils.length(eachWord);
			
			
			
			if(stringLength>1)
				eachWord=	StringUtils.substringBeforeLast(eachWord, String.valueOf(eachWord.charAt(stringLength-1)));
			wordAfterReplacementOfSpecialCharacter= eachWord;
			
			
			for (int i = 0, n = eachWord.length(); i < n; i++) {
			     c = eachWord.charAt(i);
			     if(Character.isLowerCase(c))
			     {
			    	 countOfSmallLetters++;
			    	 specialCharacterIndexArray[indexOfSmallLetters]=i;
			    	 indexOfSmallLetters++;
			    	 
			     }
			     if(Character.isUpperCase(c))
			    	 countOfCapitalLetters++; 
			}
			
			if(countOfCapitalLetters>countOfSmallLetters&&countOfSmallLetters>0)
			{
				for(int counter =0;counter<specialCharacterIndexArray.length;counter++)
				{
					if(specialCharacterIndexArray[counter]==0)
						break;
					wordAfterReplacementOfSpecialCharacter=StringUtils.replace(wordAfterReplacementOfSpecialCharacter, String.valueOf(eachWord.charAt(specialCharacterIndexArray[counter])), " ");
						}
				asciiFileContent=StringUtils.replace(asciiFileContent, eachWord, wordAfterReplacementOfSpecialCharacter);
			}
			
		}
	
		
		
	}*/

}
