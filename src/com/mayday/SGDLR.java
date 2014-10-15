package com.mayday;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.statistics.DoubleStatistics;

public class SGDLR {
	static{
		System.out.println("程序运行");
	}
	private static String DATA_DIR = "lily";
	private static Double alpha = 0.01;
	private static final Integer NUM = 50;									//设置迭代的次数，迭代的次数越多，系数的准确率越高
	
	public static void main(String []args) throws FileNotFoundException, IOException{			
		double resultDouble[] = new double[10]; 
		for (int i = 0; i < 10; i++) {	
			Tenfold.setFoldInteger(i+1);			
			long trainTime = 0;												//计算程序运行时间
			long startTime = System.currentTimeMillis();
			
			Double right = 0.0;
			LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> alldata = GetFeatureByTFIDF.getAllData(DATA_DIR);
			LinkedHashMap<String,ArrayList<LinkedHashMap<String,Integer>>> testdata = GetTestData.getAllTestData(DATA_DIR);			
			
			ArrayList<Double[]> trainVector = CalVector.getAllVector(alldata);			
			ArrayList<Double[]> testVector = CalVector.getAllTestVector(alldata,testdata);		
			
			Double coefficient[][]  = getCoefficient(trainVector);						//所有的系数
			
			Double result[] =  new Double[10];																	
			
			for(int j = 0; j < testVector.size(); j++){									//对每一个需要测试的贴子向量
				for (int k = 0; k < 10; k++) {											//初始化						
					result[k]=0.0;												
				}
				for (int k = 0; k < 10; k++) {
					for (int t = 1; t < testVector.get(0).length; t++) {				
						result[k]  +=  coefficient[k][t]*testVector.get(j)[t];
					}				
				}	
				
				Double flagInteger = getMax(result);
				
				if(flagInteger.intValue() == testVector.get(j)[0].intValue()){right++;}						
			}
			long endTime = System.currentTimeMillis();
			trainTime = endTime - startTime;
			//System.out.println("用时"+trainTime/60000 + "分钟"+(trainTime%60000)/1000.0+"秒");	
			System.out.println("第"+(i+1)+"次准确率为 \t"+right/testVector.size()+"\t用时为"+trainTime/60000 + "分钟"+(trainTime%60000)/1000.0+"秒");
			resultDouble[i] = right/testVector.size();

		}	
		System.out.println("平均值为\t"+DoubleStatistics.getAverage(resultDouble));
		System.out.println("方差为\t"+DoubleStatistics.getVariance(resultDouble));
		
	}
		
	public static Double[][] getCoefficient(ArrayList<Double[]> trainVector){
		
		Double coefficient[][] = new Double[10][trainVector.get(0).length];		
		for (int i = 0; i < coefficient.length; i++) {			
			for (int j = 0; j < coefficient[i].length; j++) {
				coefficient[i][j] = 0.0;
			}
		}
		
		Double  y = 0.0;
		
		for (int i = 0; i < 10; i++) {
			coefficient[i][0] = i+1.0;									//标记每一个类			
			for ( int num = 0 ; num < NUM; num++ ){						//迭代计算系数的次数num , 计算每一类的系数
				
				for( int  k = 0;k < trainVector.size();k++){			//每一个向量		
					
					if(trainVector.get(k)[0] == (i+1.0)) {y =1.0;} else y =0.0;					
					coefficient[i] = add(coefficient[i],y,trainVector.get(k));			
					//coefficient[i] = add2(coefficient[i],y,trainVector.get(k));
				}
			}
		}	
		return coefficient;
	}


	private static Double[] add(Double[] w, Double y, Double[] x) {
		Double s = 0.0;															//计算系数和x的内积	
		Double t[] = new Double[w.length];
		t[0] = w[0];
		for (int i = 1; i < x.length; i++) {
			s += w[i]*x[i];
		}		
		Double q = y- ( 1/(1+Math.pow(Math.E, -s)));							//中间存储值，便于计算	
		for (int i = 1; i < x.length; i++) {
			t[i] = w[i] +  alpha*q*x[i];
		}		
		return t;
	}
	
	private static Double[] add2(Double[] w, Double y, Double[] x) {			//计算系数和x的内积
		Double s = 0.0;																
		Double t[] = new Double[w.length];
		t[0] = w[0];
		for (int i = 1; i < x.length; i++) {
			s += w[i]*x[i];
		}	
		s	=	s*y;		
		Double q = Math.pow(Math.E, -s)*( 1/(1+Math.pow(Math.E, -s)))*y;							//中间存储值，便于计算	
				
		for (int i = 1; i < x.length; i++) {
			t[i] = w[i] +  alpha*q*x[i];
		}		
		return t;
	}
	
	
	public static Double getMax(Double[] result){
		Integer xInteger = 0;	
		for (int i = 0; i < result.length; i++) {
			if(result[i]>result[xInteger])xInteger = i;
		}
		return xInteger+1.0;
		
	}
	
	
}
