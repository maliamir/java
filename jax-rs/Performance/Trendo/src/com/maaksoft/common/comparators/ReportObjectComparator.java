package com.maaksoft.common.comparators;

import java.util.Comparator;

import com.maaksoft.rest.representations.ITrendInfo;
import com.maaksoft.rest.representations.ReportObject;

public class ReportObjectComparator implements Comparator<ITrendInfo> {
  
    public int compare(ITrendInfo reportObject1, ITrendInfo reportObject2) {
                    
         Double duration1 = ((ReportObject)reportObject1).getDurationDifference();
         Double duration2 = ((ReportObject)reportObject2).getDurationDifference();
         
         return (duration1.compareTo(duration2));
         
    }

}