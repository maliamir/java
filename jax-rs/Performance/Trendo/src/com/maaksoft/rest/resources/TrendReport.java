package com.maaksoft.rest.resources;

import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.StringWriter;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jfree.data.category.DefaultCategoryDataset;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.DefaultObjectWrapper;

import java.util.Arrays;

import java.util.Iterator;

import java.util.LinkedList;

import com.maaksoft.common.comparators.ReportObjectComparator;

import com.maaksoft.common.junit.report.RecordsLoader;

import com.maaksoft.rest.representations.Build;
import com.maaksoft.rest.representations.ITrendInfo;
import com.maaksoft.rest.representations.ReportBuildInfo;
import com.maaksoft.rest.representations.ReportObject;
import com.maaksoft.rest.representations.TestRecord;

import com.maaksoft.service.TrendReportGenerator;
import com.maaksoft.service.ExcelExporter;

@Path("/trendReport")
public class TrendReport {
    
    @Context
    private ServletContext servletContext; 

    private String getRootUrl(HttpServletRequest request) {
        
        String rootUrl = request.getRequestURL().toString();
        String contextPath = servletContext.getContextPath();
        rootUrl = rootUrl.substring(0, (rootUrl.indexOf(contextPath) + contextPath.length()));
        System.out.println("Root URL: " + rootUrl);
        
        return rootUrl;
        
    }

    private String generateContentFromTemplate(String temmplateName, Object objectToRender) throws Exception {
    
        StringWriter stringWriter = new StringWriter();

        Configuration configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File((servletContext.getRealPath("/") + "uiTemplates")));
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        
        Template template = configuration.getTemplate(temmplateName);
        template.process(objectToRender, stringWriter);
        
        return stringWriter.toString();

    }
    
    private TrendReportGenerator getTrend(HttpServletRequest request, String numberOfBuilds, String scenarioName, String graphImageUrl, 
                              String selectedBuildNumbers, String fromCompareStr) throws Exception {

        System.out.println("Number of Builds to load: " + numberOfBuilds);
        
        String rootUrl = this.getRootUrl(request);
        String graphTitle = "Total Build Duration";        
        
        /*
        String graphTitle = "Total Scenarios Duration";        
        if (scenarioName != null) {
            graphTitle = "Total Testcases Duration";
        } 
        */
        
        boolean fromCompare = false;
        if (fromCompareStr != null && (fromCompareStr = fromCompareStr.trim()).length() > 0) {
            fromCompare = Boolean.parseBoolean(fromCompareStr);
        }
        
        TrendReportGenerator trendReportGenerator = new TrendReportGenerator(rootUrl);
        trendReportGenerator.setTrendReportImageUrl(graphImageUrl);
        trendReportGenerator.setFromCompare(fromCompare);
        trendReportGenerator.trend(graphTitle, numberOfBuilds, scenarioName, selectedBuildNumbers);
        
        return trendReportGenerator;
        
    }
    
    @GET
    @Path("/resultsJson") 
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrend(@Context HttpServletRequest request) throws Exception {

        TrendReportGenerator trendReportGenerator = this.getTrend(request, null, null, null, null, null);

        String jsonPayload = trendReportGenerator.getTrendReportJsonString();        
        return Response.ok(jsonPayload).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").header("Content-Length", jsonPayload.getBytes().length).build();

    }

    @GET
    @Path("/results") 
    @Produces(MediaType.TEXT_HTML)
    public Response getTrendJson(@Context HttpServletRequest request) throws Exception {

        TrendReportGenerator trendReportGenerator = this.getTrend(request, null, null, null, null, null);

        String repsonse = generateContentFromTemplate("Trend Report.ftl", trendReportGenerator.getTrendReportJsonObject());
        return Response.ok(repsonse).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").header("Content-Length", repsonse.getBytes().length).build();

    }

    @POST
    @Path("/results") 
    @Produces(MediaType.TEXT_HTML)
    public Response getTrendByBuilds(@Context HttpServletRequest request, @FormParam("numberOfBuilds") String numberOfBuilds,
                                     @FormParam("scenarioName") String scenarioName, @FormParam("graphImageUrl") String graphImageUrl,
                                     @FormParam("selectedBuildNumbers") String selectedBuildNumbers, @FormParam("fromCompare") String fromCompare) throws Exception {
        
        System.out.println("scenarioName: " + scenarioName + "; graphImageUrl: " + graphImageUrl + "; selectedBuildNumbers: " + selectedBuildNumbers + 
                           "; fromCompare: " + fromCompare);
        TrendReportGenerator trendReportGenerator = this.getTrend(request, numberOfBuilds, scenarioName, graphImageUrl, selectedBuildNumbers, fromCompare);
        
        String repsonse = generateContentFromTemplate("Trend Report.ftl", trendReportGenerator.getTrendReportJsonObject());
        return Response.ok(repsonse).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").header("Content-Length", repsonse.getBytes().length).build();
        
    }

    @GET
    @Path("/compareBuildResults") 
    @Produces(MediaType.TEXT_HTML)
    public Response compareBuildResults(@Context HttpServletRequest request, @QueryParam("reportType") String reportType,
                                        @QueryParam("scenarioName") String scenarioName, @QueryParam("selectedBuildNumbers") String selectedBuildNumbers) throws Exception {
        
        if (reportType == null || (reportType = reportType.trim()).isEmpty()) {
            reportType = "Scenario";
        }
        
        if (reportType.equalsIgnoreCase("Scenario")) {
            scenarioName = null;
        }
        
        List<String> selectedBuildsList = null;
        if (selectedBuildNumbers != null && (selectedBuildNumbers = selectedBuildNumbers.trim()).length() > 0) {
            
            String[] selectedBuildNumbersArr = selectedBuildNumbers.split(",");            
            if (selectedBuildNumbersArr.length >= 2) { 
                selectedBuildsList = Arrays.asList(selectedBuildNumbersArr);
                selectedBuildNumbers = (selectedBuildsList.get(0) + "," + selectedBuildsList.get(1));
            } else{
                selectedBuildNumbers = null;
            }
            
        }
        
        RecordsLoader recordsLoader = new RecordsLoader();
        recordsLoader.load(2, scenarioName, selectedBuildsList);
        
        List<ITrendInfo> buildsList = recordsLoader.getBuildsList();
        Map<String, TestRecord> testRecordsMap = recordsLoader.getTestRecordsMap();
        List<ReportObject> reportObjectsList = new LinkedList<ReportObject>();
        
        Iterator<String> testRecordsMapKeysItr = testRecordsMap.keySet().iterator();
        while (testRecordsMapKeysItr.hasNext()) {
            
            String testcase = testRecordsMapKeysItr.next();

            TestRecord testRecord = testRecordsMap.get(testcase);
            List<Double> durationsByBuild = testRecord.getDurationsByBuild();

            List<ReportBuildInfo> reportBuildInfoList = new LinkedList<ReportBuildInfo>();
            for (int index = 0; index < buildsList.size(); index++) {

                Build build = (Build)buildsList.get(index);
                
                double durationByBuild = 0;
                if (index < durationsByBuild.size()) {
                    durationByBuild = durationsByBuild.get(index);
                }
                
                reportBuildInfoList.add(new ReportBuildInfo(build.getRunId(), build.getNumber(), build.getProductVersion(), durationByBuild));

            }
            reportObjectsList.add(new ReportObject(testRecord.getName(), reportBuildInfoList));
            
        }
        Collections.sort(reportObjectsList, new ReportObjectComparator());
        
        List<Long> buildTotalDurations = recordsLoader.getBuildTotalDurations();
        double build1TotalDuration = buildTotalDurations.get(0), build2TotalDuration = buildTotalDurations.get(1);   
        
        Build build1 = (Build)buildsList.get(0);
        Build build2 = (Build)buildsList.get(1);
        
        String build1Number = build1.getNumber();
        String build2Number = build2.getNumber();
        selectedBuildNumbers = (build1Number + "," + build2Number);
        String graphTitle = ("Total Duration between Build# " + build1Number + " and " + build2Number);
        if (selectedBuildNumbers == null || selectedBuildNumbers.isEmpty()) {            
            graphTitle = ("Total Duration between LATEST 2 BUILDS i.e. Build# " + build1Number + " and " + build2Number);
        }

        if (scenarioName != null && !(scenarioName = scenarioName.trim()).isEmpty()) {
            graphTitle += (" for " + scenarioName);
        }

        String version1Number = build1.getProductVersion();
        String version2Number = build2.getProductVersion();
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(build1TotalDuration, version1Number, ("#" + build1Number));
        dataset.addValue(build2TotalDuration, version2Number, ("#" + build2Number));
        
        String rootUrl = this.getRootUrl(request);
        TrendReportGenerator trendReportGenerator = new TrendReportGenerator(rootUrl);
        File outputGraphFile = new File((TrendReportGenerator.trendReportImagesFolderPath + File.separator + (new Date()).getTime() + ".png"));
        trendReportGenerator.generate(graphTitle, outputGraphFile, dataset);
        
        Map<String, Object> rootObject = new HashMap<String, Object>();
        rootObject.put("rootUrl", rootUrl);
        rootObject.put("reportType", reportType);        
        rootObject.put("selectedBuildNumbers", selectedBuildNumbers);
        rootObject.put("trendGraphFileUrl", (trendReportGenerator.getTrendReportImagesFolderUrl() + "/" + outputGraphFile.getName()));
        rootObject.put("reportObjects", reportObjectsList);        
                
        String repsonse = generateContentFromTemplate("Trend Report Comparison.ftl", rootObject);
        return Response.ok(repsonse).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").header("Content-Length", repsonse.getBytes().length).build();
        
    }

    @POST
    @Path("/compareResults") 
    @Produces(MediaType.TEXT_HTML)
    public Response compareBuilds(@Context HttpServletRequest request, @FormParam("reportType") String reportType, @FormParam("reportJson") String reportJson, 
                                  @FormParam("selectedBuildNumbers") String selectedBuildNumbers) throws Exception {
        
        ObjectMapper objectMapper = new ObjectMapper();
        List<ReportObject> reportObjectsList = (List<ReportObject>)objectMapper.readValue(reportJson, new TypeReference<List<ReportObject>>(){});
        Collections.sort(reportObjectsList, new ReportObjectComparator());
                
        double build1TotalDuration = 0, build2TotalDuration = 0;   
        List<ReportBuildInfo> buildsList = reportObjectsList.get(0).getBuilds();
        
        String build1Number = buildsList.get(0).getBuildNumber();
        String build2Number = buildsList.get(1).getBuildNumber();
        for (ReportObject reportObject: reportObjectsList) {
            
            buildsList = reportObject.getBuilds();
            build1TotalDuration += buildsList.get(0).getDuration();
            build2TotalDuration += buildsList.get(1).getDuration();
            
        }
        //build1TotalDuration /= reportObjectsList.size();   
        //build2TotalDuration /= reportObjectsList.size();   
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(build1TotalDuration, ("#" + build1Number), ("#" + build1Number));
        dataset.addValue(build2TotalDuration, ("#" + build2Number), ("#" + build2Number));
        
        String rootUrl = this.getRootUrl(request);
        TrendReportGenerator trendReportGenerator = new TrendReportGenerator(rootUrl);
        File outputGraphFile = new File((TrendReportGenerator.trendReportImagesFolderPath + File.separator + (new Date()).getTime() + ".png"));
        //trendReportGenerator.generate(("Average " + reportType + "s Duration between Build# " + build1Number + " and " + build2Number), outputGraphFile, dataset);
        trendReportGenerator.generate(("Total Duration between Build# " + build1Number + " and " + build2Number), outputGraphFile, dataset);
        
        Map<String, Object> rootObject = new HashMap<String, Object>();
        rootObject.put("rootUrl", rootUrl);
        rootObject.put("reportType", reportType);        
        rootObject.put("selectedBuildNumbers", selectedBuildNumbers);
        rootObject.put("trendGraphFileUrl", (trendReportGenerator.getTrendReportImagesFolderUrl() + "/" + outputGraphFile.getName()));
        rootObject.put("reportObjects", reportObjectsList);        
                
        String repsonse = generateContentFromTemplate("Trend Report Comparison.ftl", rootObject);
        return Response.ok(repsonse).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").header("Content-Length", repsonse.getBytes().length).build();
        
    }

    @POST
    @Path("/scenarioGraph") 
    @Produces(MediaType.TEXT_HTML)
    public Response generateScenarioGraph(@Context HttpServletRequest request, @QueryParam("reportJson") String reportJson) throws Exception {
        
        ReportObject reportObject = (new ObjectMapper()).readValue(reportJson, ReportObject.class);                
        List<ReportBuildInfo> buildsList = reportObject.getBuilds();  
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ReportBuildInfo reportBuildInfo: buildsList) {
            dataset.addValue(reportBuildInfo.getDuration(), ("#" + reportBuildInfo.getBuildNumber()), ("#" + reportBuildInfo.getBuildNumber()));
        }
        
        String rootUrl = this.getRootUrl(request);
        TrendReportGenerator trendReportGenerator = new TrendReportGenerator(rootUrl);
        File outputGraphFile = new File((TrendReportGenerator.trendReportImagesFolderPath + File.separator + (new Date()).getTime() + ".png"));
        trendReportGenerator.generate(reportObject.getTestRecordName(), outputGraphFile, dataset);
        
        String imageHtmlObject = ("<img src='" + (trendReportGenerator.getTrendReportImagesFolderUrl() + outputGraphFile.getName()) + "'/>");
        System.out.println("Image HTML Obbject generated:\n" + imageHtmlObject);

        return Response.ok(imageHtmlObject).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").build();
        
    }

    @GET
    @Path("/slowestTests") 
    @Produces(MediaType.TEXT_HTML)
    public Response loadSlowestTests(@QueryParam("buildNumber") String buildNumber, @QueryParam("testCasesCount") String testCasesCount) throws Exception {
        
        System.out.println("Load Slowest " + testCasesCount + " Tests for Build Number: " + buildNumber);

        int buildNumberInt = -1;
        String productName = "N/A";
        Map<String, Object> rootObject = new HashMap<String, Object>();
        if (buildNumber != null) {
            try {
                buildNumberInt = Integer.parseInt(buildNumber);
            } catch (NumberFormatException numberFormatException) {
                System.out.println("NumberFormatException occurred due to buildNumber being " + buildNumber + ", so not loading Test Records");
            }
        }
        
        if (buildNumberInt > 0) {
            
            RecordsLoader recordsLoader = new RecordsLoader();
            List<TestRecord> testRecordsList = recordsLoader.loadSlowestTests(buildNumberInt);
            
            int testCasesCountInt = 10;
            if (testCasesCount != null) {
                try {
                    testCasesCountInt = Integer.parseInt(testCasesCount);
                } catch (NumberFormatException numberFormatException) {
                    System.out.println("NumberFormatException occurred due to testCasesCount being " + testCasesCount + ", so going with default value " + testCasesCountInt);
                }
            }
            
            productName = (RecordsLoader.productName + "_" + recordsLoader.getChangeNumber());
            
            rootObject.put("testCasesCount", testCasesCountInt);
            rootObject.put("buildNumber", buildNumber);
            rootObject.put("testRecords", testRecordsList.subList(0, testCasesCountInt));        
            
        }
        
        rootObject.put("productName", productName);
        String repsonse = generateContentFromTemplate("Slowest Tests.ftl", rootObject);

        return Response.ok(repsonse).header("Content-Encoding", "UTF-8").header("Content-Language", "en-US").header("Content-Length", repsonse.getBytes().length).build();
        
    }

    @POST
    @Path("/exportResults") 
    @Produces(MediaType.TEXT_HTML)
    public Response exportResults(@FormParam("reportJson") String reportJson, @FormParam("fromCompare") String fromCompare) throws Exception {
        
        String calculatedColumnName = "Total";
        if (fromCompare != null && fromCompare.equalsIgnoreCase("true")) {
            calculatedColumnName = "Difference";
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        List<ReportObject> reportObjectsList = (List<ReportObject>)objectMapper.readValue(reportJson, new TypeReference<List<ReportObject>>(){});
        File exportedFile = (new ExcelExporter()).export(reportObjectsList, TrendReportGenerator.exportedResultsFolderPath, calculatedColumnName);
                
        return Response.ok(exportedFile).header("Content-Disposition", 
                                                "attachment;filename=" + exportedFile.getName()).header("Content-Type", 
                                                                                                        "application/octet-streamn").header("Content-Length", 
                                                                                                                                            exportedFile.length()).build();
        
    }
    
}