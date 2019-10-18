package com.maaksoft.common.junit.report;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import org.jfree.data.category.DefaultCategoryDataset;

import com.maaksoft.common.XmlFileFilter;

import com.maaksoft.common.comparators.LatestReportFileComparator;
import com.maaksoft.common.comparators.DurationComparator;

import com.maaksoft.rest.representations.ITrendInfo;
import com.maaksoft.rest.representations.Build;
import com.maaksoft.rest.representations.TestRecord;

import com.maaksoft.service.TrendReportGenerator;

/**
 * RecordsLoader class to load Test Results records from Report XML files across multiple Builds.
 */
public class RecordsLoader {
    
    private static Properties properties = new Properties();

    public static String reportFilesDirectory;
    public static String productName;
  
    private String runId;        
    private String changeNumber;        

    private List<Long> buildTotalDurations = new LinkedList<Long>();          
    private List<Double> buildAverageDurations = new LinkedList<Double>();
    private List<String> productVersionsList = new LinkedList<String>();
    private List<ITrendInfo> buildsList = new LinkedList<ITrendInfo>();     
    private Map<String, TestRecord> testRecordsMap = new LinkedHashMap<String, TestRecord>();
    
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    static {
         
         try {
             
             URL resource = TrendReportGenerator.class.getResource("/");
             String propertiesFilePath = resource.getPath();
             propertiesFilePath = propertiesFilePath.replace("WEB-INF/classes/", "");
             propertiesFilePath += (File.separator + "configuration/conf.properties");             

             FileInputStream fileInputStream = new FileInputStream(new File(propertiesFilePath));              
             properties.load(fileInputStream);             
             fileInputStream.close();
             
             System.out.println("Configuration Properties:\n" + properties);       
             
             reportFilesDirectory = properties.getProperty("report.files.directory");
             productName = properties.getProperty("product.name");
             
         } catch (IOException iOException) {
             System.out.println("IOException occurred while loading properties file due to - " + iOException.getMessage());       
         }
      
     }

     /**
     * Only and Default constructor to instantiate RecordsLoader object.
     * @throws Exception
     */
     public RecordsLoader() throws Exception {
         this.initialize();
     }    
   
     public List<String> getProductVersionsList() {
         return productVersionsList;
     }

     public List<ITrendInfo> getBuildsList() {
         return buildsList;
     }

     public Map<String, TestRecord> getTestRecordsMap() {
         return testRecordsMap;
     }

     public DefaultCategoryDataset getDataset() {
        return dataset;
     }   

     public String getChangeNumber() {
        return changeNumber;
     }

     public List<Long> getBuildTotalDurations() {
       return buildTotalDurations;
     }
 
     public List<Double> getBuildAverageDurations() {
        return buildAverageDurations;
     }
  
    /**
     * Parses the provided consolidated Report XML file using JDOM and returns the List of Test Result <test-result> XML Elements from it.
     * @param reportXmlFile Report XML file from XML elements need to be loaded.
     * @return the List of Test Result <test-result> XML Elements from the provided consolidated Report XML file.
     * @throws JDOMException
     * @throws IOException 
     */
    private List<Element> getTestResultXmlElements(File reportXmlFile) throws JDOMException, IOException {  
        
        List<Element> testResultElementsList = null;
            
        SAXBuilder saxBuilder = new SAXBuilder();
        Document reportDocument = saxBuilder.build(new FileInputStream(reportXmlFile));            
        Element jTestLogElement = reportDocument.getRootElement();    
        
        testResultElementsList = jTestLogElement.getChildren("test-result");    
        this.changeNumber = jTestLogElement.getAttributeValue("changenumber");    
        this.runId = jTestLogElement.getAttributeValue("runid");    
        
        this.changeNumber = Build.trimBuildNumber(this.changeNumber);
        if (this.changeNumber.indexOf(":") > 0) {
            this.changeNumber = this.changeNumber.split(":")[1];
        }
        this.productVersionsList.add(this.changeNumber);   
        
        return testResultElementsList;
        
    }
    
    /**
     * Called by Constructor to initialize required attributes and validating them. 
     * @throws Exception
     */    
    private void initialize() throws Exception {
        
        if(reportFilesDirectory == null || (reportFilesDirectory = reportFilesDirectory.trim()).length() <= 0){        
            throw new Exception("Invalid Report XML Files Directory.");
        } 

        File reportXMLFileObj = new File(reportFilesDirectory);          
        if(!reportXMLFileObj.exists()){
            throw new Exception("Report XML Files Directory: \"" + reportFilesDirectory + "\" doesn't exist.");
        } else if(!reportXMLFileObj.isDirectory()){
            throw new Exception("Report XML Files Directory: \"" + reportFilesDirectory + "\" doesn't point to a Directory.");
        } 
        
    }
    /**
     * Loads Slowest Tests in the provided Build.
     * @param buildNumber Build Number in which slowest tests need to be loaded..
     * @throws JDOMException
     * @throws IOException 
     */
     public List<TestRecord> loadSlowestTests(int buildNumber) throws JDOMException, IOException {
        
        File[] scenarioFiles = (new File(reportFilesDirectory)).listFiles(new XmlFileFilter()); 
        List<File> reportXmlFilesList = Arrays.asList(scenarioFiles);      
        Collections.sort(reportXmlFilesList, new LatestReportFileComparator());
        
        File reportXmlFile = reportXmlFilesList.get((buildNumber - 1));
        List<Element> testResultElementsList = this.getTestResultXmlElements(reportXmlFile);            
        List<TestRecord> testRecordsList = new LinkedList<TestRecord>();
        if(testResultElementsList != null && testResultElementsList.size() > 0){
            
            for(Element testResultElement : testResultElementsList) {

                String testLogicalName = testResultElement.getAttributeValue("logicalname");                    
                    
                String testResultElementLogicalName = testLogicalName;
                testResultElementLogicalName = testResultElementLogicalName.substring(0, testResultElementLogicalName.lastIndexOf("/"));
                String scenarioName = testResultElementLogicalName.substring(0, testResultElementLogicalName.lastIndexOf("/"));
                String useCasePath = scenarioName.substring(0, scenarioName.lastIndexOf("/"));
                scenarioName = scenarioName.substring((scenarioName.lastIndexOf("/") + 1));
                                                
                String scenarioPath = (useCasePath + "/" + scenarioName);                    
                String testcase = testLogicalName.substring((scenarioPath.length() + 1));

                String testResultDurationStr = testResultElement.getAttributeValue("duration");
                double testResultDuration = ((double)((testResultDurationStr == null || testResultDurationStr.isEmpty()) ?  0.00 : Long.parseLong(testResultDurationStr))) / 1000;
            
                testRecordsList.add(new TestRecord(scenarioPath, testcase, testResultDuration));
                
            }
            
        }
        
        Collections.sort(testRecordsList, new DurationComparator());
        
        return testRecordsList;
        
    }

    /**
     * Loads Test Results records from Report XML files across multiple Builds.
     * @param numberOfBuilds Number of Builds to load from the Directory.
     * @param scenario Scenario whose Records to be loaded. If null then Records for all Scenarios are loaded.
     * @param selectedBuildNumbersList List of Selected Build Numbers (IF ANY).
     * @throws JDOMException
     * @throws IOException 
     */
     public void load(int numberOfBuilds, String scenario, List<String> selectedBuildNumbersList) throws JDOMException, IOException {
        
        File[] scenarioFiles = (new File(reportFilesDirectory)).listFiles(new XmlFileFilter()); 
        List<File> reportXmlFilesList = Arrays.asList(scenarioFiles);      
        Collections.sort(reportXmlFilesList, new LatestReportFileComparator());
        
        int fetchCount = 0, buildCount = reportXmlFilesList.size();  
        boolean forScenarios = (scenario == null || scenario.isEmpty());
        for (File reportXmlFile: reportXmlFilesList) {
            
            if (selectedBuildNumbersList != null) {
                
                if (!selectedBuildNumbersList.contains(("" + buildCount))) {
                    buildCount--;
                    continue;//SKIPPING the Report XML file if doesn't match to the selected Build Number.
                }
                
            }
            
            List<Element> testResultElementsList = this.getTestResultXmlElements(reportXmlFile);            
            if(testResultElementsList != null && testResultElementsList.size() > 0){
                
                if ((++fetchCount) > numberOfBuilds) {
                    break;
                }
                
                double totalScenariosDurationPerBuild = 0;    
                for(Element testResultElement : testResultElementsList) {

                    String testLogicalName = testResultElement.getAttributeValue("logicalname");                    
                    if (forScenarios || (!forScenarios && testLogicalName.startsWith(scenario))) {
                        
                        String testResultElementLogicalName = testLogicalName;
                        testResultElementLogicalName = testResultElementLogicalName.substring(0, testResultElementLogicalName.lastIndexOf("/"));
                        String scenarioName = testResultElementLogicalName.substring(0, testResultElementLogicalName.lastIndexOf("/"));
                        String useCasePath = scenarioName.substring(0, scenarioName.lastIndexOf("/"));
                        scenarioName = scenarioName.substring((scenarioName.lastIndexOf("/") + 1));
                        
                        String scenarioPath = (useCasePath + "/" + scenarioName);
                        String testResultDurationStr = testResultElement.getAttributeValue("duration");
                        double testResultDuration = ((double)((testResultDurationStr == null || testResultDurationStr.isEmpty()) ?  0.00 : Long.parseLong(testResultDurationStr))) / 1000;
                        String testResult = testResultElement.getAttributeValue("result");
                        
                        String mapKey = testLogicalName;
                        if (forScenarios) {
                            mapKey = scenarioPath;
                        } else {
                            mapKey = mapKey.substring((scenarioPath.length() + 1));
                        }
                        TestRecord testRecord = testRecordsMap.get(mapKey);
                        if (testRecord == null) {
                            testRecord = new TestRecord(mapKey);
                        }
                        
                        if (forScenarios) {
                            testRecord.setDuration((testRecord.getDuration() + testResultDuration));
                        } else {
                            testRecord.setDuration(testResultDuration);
                        }
                        
                        if (!testRecord.getStatus().equalsIgnoreCase("FAILURE") && testResult.equalsIgnoreCase("SKIP")) {
                            testRecord.setStatus("SKIP");
                        } else if (testResult.equalsIgnoreCase("FAILURE") || testResult.equalsIgnoreCase("ABORT")) {                             
                            testRecord.setStatus("FAILURE");
                        }
                        
                        testRecordsMap.put(mapKey, testRecord);
                    
                    }

                }
                
                Build build = new Build(("" + (buildCount--)), this.changeNumber, this.runId);
                Collection<TestRecord> testRecords = new LinkedList<TestRecord>(testRecordsMap.values());                
                for (TestRecord testRecord: testRecords) {
                    
                    testRecord.getDurationsByBuild().add(testRecord.getDuration());
                    totalScenariosDurationPerBuild += testRecord.getDuration();
                                        
                    if (testRecord.getStatus().equalsIgnoreCase("SUCCESS")) {
                        build.addSucs(1);
                    } else if (testRecord.getStatus().equalsIgnoreCase("SKIP")) {
                        build.addSkips(1);
                    } else if (testRecord.getStatus().equalsIgnoreCase("FAILURE") || testRecord.getStatus().equalsIgnoreCase("ABORT")) {
                        build.addDifs(1);
                    }
                    
                    testRecord.setDuration(0);//Resetting duration for Scenario Total duration in next Report XML file                    
                    testRecord.setStatus("SUCCESS");//Resetting status next Report XML file                    

                }
                buildsList.add(build);
                    
                double avgScenariosDurationPerBuild = (((double)totalScenariosDurationPerBuild)/testRecords.size());
                dataset.addValue(totalScenariosDurationPerBuild, build.getProductVersion(), ("#" + build.getNumber()));
                buildTotalDurations.add(new Long((long)(totalScenariosDurationPerBuild * 1000)));
                buildAverageDurations.add(new Double(avgScenariosDurationPerBuild));
                
            }   
            
        }

        Collections.sort(this.productVersionsList);
        
    }    
        
    //For Unit-Testing purpose.
    /**
     * main method for unit testing.
     * @param args arguments.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    
        System.out.println("args: " + args.length);
               
        RecordsLoader recordsLoader = new RecordsLoader();
        recordsLoader.load(10, null, null);
        
        /*
        String s = "Main_Node_MWE,MWE,Dept Rollup,Dept Rollup\n";
        for (int i = 0; i <= 10000; i++) {
            s += "Node_MWE,MWE,Dept Rollup,Dept Rollup\n";
        }
        */
        
    }

}