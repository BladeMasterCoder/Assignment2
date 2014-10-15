package com.mayday;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;


public class CalVector {
		
	
	private static Map<String, Integer> DUIYING = new LinkedHashMap<String, Integer>();	//记录行号和类别的对应关系	

	public static Map<String, Integer> getDuiYing(){
		return DUIYING;
	}
	
	private static void setDuiYing(LinkedHashSet<String> tSet){
		int i = 1;
		for(String str:tSet){
			DUIYING.put(str, i);
			i++;
		}
	}
	
	
	public static ArrayList<Double[]> getAllVector(LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> alldata) throws FileNotFoundException, IOException{
		
		
		LinkedHashSet<String> tSet  = GetFeatureByTFIDF.getFeature(alldata);	//这个set中存得到的特征词的集合
		ArrayList<Double[]> allVector = new ArrayList<Double[]>();
		setDuiYing(tSet);

		Double x[] = new Double[tSet.size()+1];
		
		Double k = 1.0;
		
		for(Entry<String, ArrayList<LinkedHashMap<String, Integer>>> entry : alldata.entrySet()){	//每一类
			
			for (int j = 0; j < entry.getValue().size(); j++) {										//每个帖子						
				x = init(x);				
				x[0]=k;			
				for (Entry<String,Integer> entry2 : entry.getValue().get(j).entrySet()){			//贴子中每个单词			
					String keyString = entry2.getKey();
					if(tSet.contains(keyString)){						
						Integer loc = getDuiYing().get(keyString);						
						x[loc] = 1.0;						
					}
				}	
				Double t[] = x.clone();
				allVector.add(t);				
			}
			k++;
		}
		return allVector;	
	}
	
	public static Double[] init(Double[] s){
		for (int i = 0; i < s.length; i++) {
			s[i]=0.0;
		}
		return s;	
	}
	
	public static ArrayList<Double[]> getAllTestVector(LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> alldata,LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> testdata) throws FileNotFoundException, IOException{
		
		
		LinkedHashSet<String> tSet  = GetFeatureByTFIDF.getFeature(alldata);	//这个set中存得到的特征词的集合
		ArrayList<Double[]> allVector = new ArrayList<Double[]>();
	

		Double x[] = new Double[tSet.size()+1];
		
		Double k = 1.0;
		
		for(Entry<String, ArrayList<LinkedHashMap<String, Integer>>> entry : testdata.entrySet()){	//每一类
			
			for (int j = 0; j < entry.getValue().size(); j++) {										//每个帖子						
				x = init(x);				
				x[0]=k;			
				for (Entry<String,Integer> entry2 : entry.getValue().get(j).entrySet()){			//贴子中每个单词			
					String keyString = entry2.getKey();
					if(tSet.contains(keyString)){						
						Integer loc = getDuiYing().get(keyString);						
						x[loc] = 1.0;						
					}
				}	
				Double t[] = x.clone();
				allVector.add(t);				
			}
			k++;
		}
		return allVector;	
	}
	

}
