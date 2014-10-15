package com.mayday;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class GetFeatureByTFIDF {
		private static final Integer NUM = 150;												//每一类下取NUM个特征词语
		private static LinkedHashSet<String>	TFIDF_FEATURE  =  new LinkedHashSet<String>();			//特征词
		private static Double totalNUM = 0.0 ;												//贴子总数
		
		public static Double getTotalNumDouble(){			//返回贴子总数
			return totalNUM;
		}
				
		
		public static LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> getAllData(String path) throws FileNotFoundException, IOException{	
			totalNUM	=	0.0;
			TFIDF_FEATURE.clear();
			LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> alldataList = new LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>>();	
			List<String> dirs = ReadFiles.readDirs(path);
			for (int i = 0; i < dirs.size(); i++) {		
				int loc = dirs.get(i).lastIndexOf("\\");
				String name = dirs.get(i).substring(loc+1);	
				alldataList.put(name,getTrainData(dirs.get(i)));
			}	
			return alldataList;
		}	
		
		public static ArrayList<LinkedHashMap<String,Integer>> getTrainData(String path) throws IOException {	
			ArrayList<String> list= readFile(path);		
			int ten = list.size()/10;
			int jiaocha = Tenfold.getFoldInteger();
			totalNUM 	+=	 list.size()*9/10;														//计算贴子总数
			ArrayList<LinkedHashMap<String,Integer>> listword = new ArrayList<LinkedHashMap<String,Integer>>();
			String text = null;		
			for (int i = 0; i < list.size(); i++) {											//对每一类下的所有文本							
				if(i>ten * jiaocha || i<ten * (jiaocha-1))	{				
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
				}
			}			
			return listword;		
		}
		
		
		public static LinkedHashSet<String> getFeature(LinkedHashMap<String,ArrayList<LinkedHashMap<String, Integer>>> alldata){
			
			for(Entry<String,ArrayList<LinkedHashMap<String, Integer>>> entry : alldata.entrySet()){//每一类下 
				Map<String, Double> sortword = new HashMap<String, Double>();
				for (int i = 0; i < entry.getValue().size(); i++) {									//每个帖子
					Integer wordTotal = 0 ;
					for (Entry<String, Integer> entry2 : entry.getValue().get(i).entrySet()){
						wordTotal+=entry2.getValue();
					}
				
					for (Entry<String, Integer> entry2 : entry.getValue().get(i).entrySet()){ 		//每个单词
						String key = entry2.getKey(); 
						Integer value = entry2.getValue();	
						
						
						double db =(((double) value) / wordTotal )  ;// * Math.log10(totalNUM/getCountTotal(alldata, key))
						
						for (int j = 0; j < entry.getValue().get(i).size(); j++) {
							
						}
						
						
						if(!sortword.containsKey(key)){
							sortword.put(key, db);
						}
						else if(sortword.get(key)<db){
							sortword.put(key,db);
						}			
					}								
				}			
				List<Map.Entry<String, Double>> list_data = new ArrayList<Map.Entry<String, Double>>(sortword.entrySet());
				Collections.sort(list_data,new Comparator<Map.Entry<String, Double>>() { // 排序
					public int compare(Map.Entry<String, Double> o1,
							Map.Entry<String, Double> o2) {
						if ((o2.getValue() - o1.getValue()) > 0)
							return 1;
						else if ((o2.getValue() - o1.getValue()) == 0)
							return 0;
						else
							return -1;
					}
				});			
				LinkedHashMap<String, Double> newMap = new LinkedHashMap<String, Double>();
				for (Map.Entry<String, Double> entity : list_data) {
					newMap.put(entity.getKey(), entity.getValue());
				}
				Set<String> set = newMap.keySet();
				Iterator<String> its = set.iterator();
				Integer num = 0;
				while (its.hasNext() && num < NUM) {
					String key = its.next();
					TFIDF_FEATURE.add(key);
					num++;
				}		
			}
			return TFIDF_FEATURE;
		}
		
		
		
		public static Double getCountTotal(LinkedHashMap<String,ArrayList<LinkedHashMap<String, Integer>>> list,String str){//总帖子数中，包含str的帖子数目
			Double num = 0.0 ;							
			for(Entry<String,ArrayList<LinkedHashMap<String, Integer>>> entry : list.entrySet()){
				for (int i = 0; i < entry.getValue().size(); i++) {
					if(entry.getValue().get(i).containsKey(str))	num++;
				}			
			}	
			num++;
			return num;		
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
