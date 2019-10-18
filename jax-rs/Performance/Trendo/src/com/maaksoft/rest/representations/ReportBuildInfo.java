package com.maaksoft.rest.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportBuildInfo implements ITrendInfo {
   
    private String runId;
    private String buildNumber;
    private String productVersion;
    private double duration;

    @JsonCreator
    public ReportBuildInfo(@JsonProperty("runId") String runId, @JsonProperty("buildNumber") String buildNumber, @JsonProperty("productVersion") String productVersion,
                           @JsonProperty("duration") double duration) {
        this.runId = runId;
        this.buildNumber = buildNumber;
        this.productVersion = productVersion;
        this.duration = duration;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getProductVersion() {
        return productVersion;
    }
    
}