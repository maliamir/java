package com.maaksoft.rest.representations;

import java.util.List;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.maaksoft.common.Utils;

public class TrendReport {
    
    private String totalBuildDurationFormat = Utils.TEST_DURATION_FORMAT_STR;
    
    private boolean fromCompare = false;
    private Integer numberOfBuilds;
    
    private String productName;
    private String reportType = "Scenario";
    private String testCaseName;
    private String rootUrl;
    private String trendGraphFileUrl;    
    private String productVersionStart;
    private String productVersionEnd;
    
    private List<String> productVersions;
    private List<Long> buildTotalDurations;
    private List<Double> buildAverageDurations;      
    private List<ITrendInfo> builds;
    private List<ITrendInfo> testRecords;
    private List<String> formattedBuildTotalDurations = new LinkedList<String>();
    
    @JsonCreator
    public TrendReport(@JsonProperty("productName") String productName, @JsonProperty("rootUrl") String rootUrl, @JsonProperty("trendGraphFileUrl") String trendGraphFileUrl, 
                       @JsonProperty("numberOfBuilds") Integer numberOfBuilds, @JsonProperty("productVersionStart") String productVersionStart, 
                       @JsonProperty("productVersionEnd") String productVersionEnd, @JsonProperty("productVersions") List<String> productVersions, 
                       @JsonProperty("builds") List<ITrendInfo> builds, @JsonProperty("builds") List<ITrendInfo> testRecords) {
        this.productName = productName;
        this.rootUrl = rootUrl;
        this.trendGraphFileUrl = trendGraphFileUrl;
        this.numberOfBuilds = numberOfBuilds;
        this.productVersionStart = productVersionStart;
        this.productVersionEnd = productVersionEnd;
        this.productVersions = productVersions;
        this.builds = builds;
        this.testRecords = testRecords;
    }

    public void setTrendGraphFileUrl(String trendGraphFileUrl) {
        this.trendGraphFileUrl = trendGraphFileUrl;
    }

    public String getTrendGraphFileUrl() {
        return trendGraphFileUrl;
    }

    public void setBuilds(List<ITrendInfo> builds) {
        this.builds = builds;
    }

    public List<ITrendInfo> getBuilds() {
        return builds;
    }

    public List<String> getProductVersions() {
        return productVersions;
    }

    public void setProductVersionStart(String productVersionStart) {
        this.productVersionStart = productVersionStart;
    }

    public String getProductVersionStart() {
        return productVersionStart;
    }

    public void setProductVersionEnd(String productVersionEnd) {
        this.productVersionEnd = productVersionEnd;
    }

    public String getProductVersionEnd() {
        return productVersionEnd;
    }

    public void setProductVersions(List<String> productVersions) {
        this.productVersions = productVersions;
    }

    public void setNumberOfBuilds(Integer numberOfBuilds) {
        this.numberOfBuilds = numberOfBuilds;
    }

    public Integer getNumberOfBuilds() {
        return numberOfBuilds;
    }

    public void setTestRecords(List<ITrendInfo> testRecords) {
        this.testRecords = testRecords;
    }

    public List<ITrendInfo> getTestRecords() {
        return testRecords;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportType() {
        return reportType;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setFromCompare(boolean fromCompare) {
        this.fromCompare = fromCompare;
    }

    public boolean isFromCompare() {
        return fromCompare;
    }

    public void setBuildAverageDurations(List<Double> buildAverageDurations) {
        this.buildAverageDurations = buildAverageDurations;
    }

    public List<Double> getBuildAverageDurations() {
        return buildAverageDurations;
    }

    public void setBuildTotalDurations(List<Long> buildTotalDurations) {

        this.buildTotalDurations = buildTotalDurations;

        for (Long buildTotalDuration: buildTotalDurations) {
            formattedBuildTotalDurations.add(Utils.getFormattedTestDuration(buildTotalDuration));
        }

    }

    public List<Long> getBuildTotalDurations() {
        return buildTotalDurations;
    }

    public List<String> getFormattedBuildTotalDurations() {
        return formattedBuildTotalDurations;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getTestCaseName() {
        
        if (testCaseName == null)
            return "";
        else
            return testCaseName;
        
    }

    public String getTotalBuildDurationFormat() {
        return totalBuildDurationFormat;
    }
    
}