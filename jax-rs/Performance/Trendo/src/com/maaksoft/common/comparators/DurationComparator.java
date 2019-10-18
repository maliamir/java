package com.maaksoft.common.comparators;

import java.util.Comparator;

import com.maaksoft.rest.representations.ITrendInfo;
import com.maaksoft.rest.representations.TestRecord;

public class DurationComparator implements Comparator<ITrendInfo> {
  
    public int compare(ITrendInfo testRecord1, ITrendInfo testRecord2) {
                    
         Double duration1 = ((TestRecord)testRecord1).getAverageDuration();
         Double duration2 = ((TestRecord)testRecord2).getAverageDuration();
         
         return (duration2.compareTo(duration1));
         
    }

}