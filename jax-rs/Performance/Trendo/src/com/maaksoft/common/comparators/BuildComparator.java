package com.maaksoft.common.comparators;

import java.util.Comparator;

import com.maaksoft.rest.representations.Build;
import com.maaksoft.rest.representations.ITrendInfo;

public class BuildComparator implements Comparator<ITrendInfo> {
  
    public int compare(ITrendInfo build1, ITrendInfo build2) {
         return (((Build)build1).getProductVersion().compareTo(((Build)build2).getProductVersion()));
    }

}