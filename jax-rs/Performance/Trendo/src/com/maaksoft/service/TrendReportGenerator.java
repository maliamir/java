package com.maaksoft.service;

import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedList;

import java.io.File;
import java.io.FileOutputStream;

import java.net.URL;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.jfree.data.category.DefaultCategoryDataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.minidev.json.JSONObject;

import net.minidev.json.parser.JSONParser;

import com.maaksoft.common.comparators.DurationComparator;

import com.maaksoft.common.junit.report.RecordsLoader;

import com.maaksoft.rest.representations.ITrendInfo;
import com.maaksoft.rest.representations.Build;
import com.maaksoft.rest.representations.TestRecord;
import com.maaksoft.rest.representations.TrendReport;

public class TrendReportGenerator {

   private static final String TREND_REPORT_IMAGES_FOLDER_NAME = "trendReportImages";   
   private static final String EXPORTED_RESULTS_FOLDER_NAME = "exportedResults";   
    
   public static String trendReportImagesFolderPath = "";
   public static String exportedResultsFolderPath = "";
   
   private boolean fromCompare = false;
    
   private String rootUrl;   
   private String trendReportJsonString;
   private String numberOfBuilds = "10";
   private String trendReportImagesFolderUrl;
   
   private String trendReportImageUrl;
   
   private File outputGraphFile;

   private JSONObject trendReportJsonObject;    
   
   static {
        
        URL resource = TrendReportGenerator.class.getResource("/");
        String rootPath = resource.getPath();
        rootPath = rootPath.replace("WEB-INF/classes/", "");
        
        trendReportImagesFolderPath = (rootPath + File.separator + TREND_REPORT_IMAGES_FOLDER_NAME);
        File trendReportImagesFile = new File(trendReportImagesFolderPath);
        if (!trendReportImagesFile.exists()) {
            trendReportImagesFile.mkdirs();
        }

        exportedResultsFolderPath = (rootPath+ File.separator + EXPORTED_RESULTS_FOLDER_NAME);
        File exportedResultsFile = new File(exportedResultsFolderPath);
        if (!exportedResultsFile.exists()) {
            exportedResultsFile.mkdirs();
        }

        System.out.println("Trend Report Images File Path: " + trendReportImagesFile);
     
    }

    public String getTrendReportJsonString() {
        return trendReportJsonString;
    }

    public JSONObject getTrendReportJsonObject() {
        return trendReportJsonObject;
    }

    public void setTrendReportImageUrl(String trendReportImageUrl) {
        this.trendReportImageUrl = trendReportImageUrl;
    }

    public String getTrendReportImageUrl() {
        return trendReportImageUrl;
    }

    public String getTrendReportImagesFolderUrl() {
        return trendReportImagesFolderUrl;
    }

    public TrendReportGenerator() {
        
    }
    
    public TrendReportGenerator(String rootUrl) {
        this.rootUrl = rootUrl;
        this.trendReportImagesFolderUrl = (this.rootUrl + "/" + TREND_REPORT_IMAGES_FOLDER_NAME + "/");
    }

    public void setFromCompare(boolean fromCompare) {
        this.fromCompare = fromCompare;
    }

    public boolean isFromCompare() {
        return fromCompare;
    }
   
    public File generate(String graphTitle, File outputGraphFile, DefaultCategoryDataset dataset) throws Exception {
       
        JFreeChart chart = ChartFactory.createBarChart(graphTitle, "Build#", "Total Build Duration (seconds)", dataset);
       
        FileOutputStream graphFileOutputStream = new FileOutputStream(outputGraphFile);
        ChartUtilities.writeChartAsPNG(graphFileOutputStream, chart, 1000, 700);
        graphFileOutputStream.close();
       
        return outputGraphFile;
       
    }
       
    public void trend(String graphTitle, String numberOfBuildsStr, String scenario, String selectedBuildNumbers) throws Exception {

        if (numberOfBuildsStr != null) { 
           this.numberOfBuilds = numberOfBuildsStr; 
        }
        
        Integer numberOfBuilds = Integer.parseInt(this.numberOfBuilds);
        List<String> selectedBuildNumbersList = null;
        if (selectedBuildNumbers != null && (selectedBuildNumbers = selectedBuildNumbers.trim()).length() > 0) {
            selectedBuildNumbersList = Arrays.asList(selectedBuildNumbers.split(","));
        }

        RecordsLoader recordsLoader = new RecordsLoader();
        recordsLoader.load(numberOfBuilds, scenario, selectedBuildNumbersList);

        List<ITrendInfo> buildsList = recordsLoader.getBuildsList();
        Map<String, TestRecord> testRecordsMap = recordsLoader.getTestRecordsMap();

        String productVersionEnd = ((Build)buildsList.get(0)).getProductVersion();
        String productVersionStart = ((Build)buildsList.get((buildsList.size() - 1))).getProductVersion();
        
        List<ITrendInfo> testRecordsList = new LinkedList<ITrendInfo>(testRecordsMap.values());
        Collections.sort(testRecordsList, new DurationComparator());        
        
        if (trendReportImageUrl == null || (trendReportImageUrl = trendReportImageUrl.trim()).isEmpty()) {
            
            this.outputGraphFile = new File((trendReportImagesFolderPath + File.separator + (new Date()).getTime() + ".png"));
            this.outputGraphFile = this.generate(((scenario != null) ? scenario : graphTitle), this.outputGraphFile, recordsLoader.getDataset());
            this.trendReportImageUrl = (this.trendReportImagesFolderUrl + outputGraphFile.getName());
            
        }

        System.out.println("Product Name: " + RecordsLoader.productName);  
        
        TrendReport trendReport = new TrendReport(RecordsLoader.productName, this.rootUrl, this.trendReportImageUrl, numberOfBuilds, productVersionStart, 
                                                  productVersionEnd, recordsLoader.getProductVersionsList(), buildsList, testRecordsList);
        if (scenario != null && !(scenario = scenario.trim()).isEmpty()) {
            trendReport.setReportType("Testcase"); 
            trendReport.setTestCaseName(scenario);
        }        
        trendReport.setFromCompare(fromCompare);
        trendReport.setBuildTotalDurations(recordsLoader.getBuildTotalDurations());
        trendReport.setBuildAverageDurations(recordsLoader.getBuildAverageDurations());
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        trendReportJsonString = objectMapper.writeValueAsString(trendReport);       
        trendReportJsonObject = (JSONObject)new JSONParser().parse(trendReportJsonString);
        
        //System.out.println("Trend Report JSON:\n"  + trendReportJsonString);      
        
    }

    public void test() throws Exception {
        final StackTraceElement[] ste = new Throwable().getStackTrace();
        Object obj = Class.forName(ste[0].getClassName()).newInstance();
        System.out.println(obj.getClass() + " " + ste[0].getMethodName());
    }
    
    public static void main(String[ ] args) throws Exception {
       
        TrendReportGenerator trendReportGenerator = new TrendReportGenerator("");
        trendReportGenerator.test();
        /*
        trendReportGenerator.trend("Average Scenarios Duration", "15", null, null);
        
        Configuration configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File("D:/DMCS/Jenkins/JFreeChart/JFreeChart/public_html/uiTemplates"));

        Template scenariosTemplate = configuration.getTemplate("Trend Report.ftl");
        StringWriter stringWriter = new StringWriter();
        scenariosTemplate.process(trendReportGenerator.getTrendReportJsonObject(), stringWriter);
        
        System.out.println("HTML Response:\n" + stringWriter);       
        */
        //FileUtils.writeByteArrayToFile(new File("D:/test.xls"), stringWriter.toString().getBytes());
        
    }

}