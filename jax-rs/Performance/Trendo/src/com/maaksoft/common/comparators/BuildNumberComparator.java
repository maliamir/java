package com.maaksoft.common.comparators;

import java.io.File;

import java.util.Comparator;

public class BuildNumberComparator implements Comparator<File> {
  
    public int compare(File file1, File file2) {
         
         String file1Name = file1.getName();
         file1Name = file1Name.substring(0, file1Name.indexOf("."));

         String file2Name = file2.getName();
         file2Name = file2Name.substring(0, file2Name.indexOf("."));
         
         Integer buildNumber1 = Integer.parseInt(file1Name);
         Integer buildNumber2 = Integer.parseInt(file2Name);
         
         return (buildNumber2.compareTo(buildNumber1));
         
    }

}