package com.mayday;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class GetTestData {
	public static LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> getAllTestData(String path) throws FileNotFoundException, IOException{
		LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> alldataMap = new LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>>();	
		List<String> dirs = ReadFiles.readDirs(path);
		for (int i = 0; i < dirs.size(); i++) {
			int loc = dirs.get(i).lastIndexOf("\\");
			String name = dirs.get(i).substring(loc+1);				//类别名		
			alldataMap.put(name,getTestData(dirs.get(i)));
		}	
		return alldataMap;
	}	
	public static ArrayList<LinkedHashMap<String, Integer>> getTestData(String path) throws IOException {	
		ArrayList<String> list= readFile(path);
		int ten = list.size()/10;  
		int jiaocha = Tenfold.getFoldInteger();
		ArrayList<LinkedHashMap<String, Integer>> listword = new ArrayList<LinkedHashMap<String, Integer>>();
		String text = null;	
		for (int i = 0; i < list.size(); i++) {
			if(i<ten*jiaocha&&i>ten*(jiaocha-1))	{
		    text = list.get(i);
		    //System.out.println(i);
		    LinkedHashMap<String, Integer> words = new LinkedHashMap<String, Integer>();
			IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
			Lexeme lexeme = null;
			while ((lexeme = ikSegmenter.next()) != null) {
				if(lexeme.getLexemeText().matches("[0-9][0-9.?-]+"))	continue;
				if(lexeme.getLexemeText().endsWith("jpg")||lexeme.getLexemeText().endsWith("png"))				continue; 
				if(lexeme.getLexemeText().endsWith("--"))				continue; 
				if(lexeme.getLexemeText().endsWith("html")||lexeme.getLexemeText().endsWith("com"))				continue; 
				if(lexeme.getLexemeText().endsWith("cn"))				continue; 
				if(lexeme.getLexemeText().startsWith("qq"))				continue;
				if(lexeme.getLexemeText().endsWith("http"))				continue; 
				if(lexeme.getLexemeText().length()>1){
					if (words.containsKey(lexeme.getLexemeText())) {
						words.put(lexeme.getLexemeText(),words.get(lexeme.getLexemeText()) + 1);
					} else {
						words.put(lexeme.getLexemeText(), 1);
				    }					
				}
			}		
			listword.add(words);
			}}	
		return listword;
	}
	
	public static ArrayList<String> readFile(String path) {
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		String string = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			while ((string = reader.readLine()) != null) {
				if (string.equals("")||string.equals(null))continue;
				list.add(string);			
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
			}
		}
		return list;
	}
	
	
}
