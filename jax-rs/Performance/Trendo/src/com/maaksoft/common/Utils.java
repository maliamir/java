package com.maaksoft.common;

import java.text.SimpleDateFormat;

public class Utils {
   
    /**
     * Format for Test Duration.
     */
    public static final String TEST_DURATION_FORMAT_STR = "mm:ss:SSS";
    
    /**
     * SimpleDateFormat for Test Duration.
     */
    public static final SimpleDateFormat TEST_DURATION_FORMAT = new SimpleDateFormat(TEST_DURATION_FORMAT_STR);
    
    public static String getFormattedTestDuration(long testDuration) {
        
        String formattedDuration = TEST_DURATION_FORMAT.format(testDuration);
        
        long hours = (testDuration / (60 * 60 * 1000));
        if (hours > 0) {
            
            String minutesStr = formattedDuration.substring(formattedDuration.indexOf(":") + 1);
            int indexForSeconds = formattedDuration.indexOf(":");
            minutesStr = formattedDuration.substring(0, formattedDuration.indexOf(":"));
            int minutes = Integer.parseInt(minutesStr);
            long totalMinutes = ((60 * hours) + minutes);
            formattedDuration = (totalMinutes + formattedDuration.substring(indexForSeconds));
            
        }
        
        return formattedDuration;
        
    }  
    
    //For Unit-Testing purpose.
    /**
     * main method for unit testing.
     * @param args arguments.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    
        System.out.println("args: " + args.length);
               
        System.out.println("Formatted Duration: " + getFormattedTestDuration((long)(10242.392*1000)));
        
    }
    
    
}
