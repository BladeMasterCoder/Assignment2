package com.mayday;

import java.io.*;
import java.util.*;

public class ReadFiles {	
	
	public static List<String> readDirs(String filepath) 
    {
		ArrayList<String> FileList = new ArrayList<String>();
        
            File file = new File(filepath);
          
            if(!file.isDirectory())
            {
                System.out.println("输入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            }
            else
            {
                String[] flist = file.list();
                for(int i = 0; i < flist.length; i++)
                {
                    File newfile = new File(filepath + "\\" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
                    {
                        readDirs(filepath + "\\" + flist[i]);
                    }                    
                }
            }
        
        return FileList;
    }

}