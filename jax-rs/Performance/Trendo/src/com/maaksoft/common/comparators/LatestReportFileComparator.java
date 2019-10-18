package com.maaksoft.common.comparators;

import java.util.Comparator;

import java.io.File;

public class LatestReportFileComparator implements Comparator<File> {
  
    public int compare(File file1, File file2) {
         
         String file1Name = file1.getName();
         file1Name = file1Name.substring(0, file1Name.indexOf("."));

         String file2Name = file2.getName();
         file2Name = file2Name.substring(0, file2Name.indexOf("."));
         
         Long lastModified1 = new Long(file1.lastModified());
         Long lastModified2 = new Long(file2.lastModified());
         
         return (lastModified2.compareTo(lastModified1));
         
    }

}