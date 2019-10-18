package com.maaksoft.common;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A specialized FilenameFilter for just filtering the XML file types.
 */
public class XmlFileFilter implements FilenameFilter {
   
    /**
     * Overriden method which flags whether File is an XML file or not.
     * @param dir Directory on which Filter is applied.
     * @param name File Name whose extension is to be checked.
     * @return flag whether the given file is an XML file o not.
     */
    @Override
    public boolean accept(File dir, String name) { 
        return name.endsWith(".xml"); 
    } 
    
    /**
     * Checks whether provided File instance is an XML file or not.
     * @param file File instance to be checked whether it is an XML file or not.
     * @return flag whether the given file is an XML file o not.
     */
    public boolean isXMLFile(File file){
        return this.accept(file, file.getName());
    }
        
}