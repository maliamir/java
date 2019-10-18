package com.maaksoft.rest.representations;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportObject implements ITrendInfo {

    private boolean differenceCalculated = false;
    private double durationDifference;
    private double averageDuration;
   
    private String testRecordName;
    private List<ReportBuildInfo> builds;
    
    @JsonCreator
    public ReportObject(@JsonProperty("testRecordName") String scenario, @JsonProperty("builds") List<ReportBuildInfo> builds) {
        this.testRecordName = scenario;
        this.builds = builds;
    }

    public void setTestRecordName(String testRecordName) {
        this.testRecordName = testRecordName;
    }

    public String getTestRecordName() {
        return testRecordName;
    }

    public void setBuilds(List<ReportBuildInfo> builds) {
        this.builds = builds;
    }

    public List<ReportBuildInfo> getBuilds() {
        return builds;
    }

    public boolean hasDifferenceCalculated() {
        return differenceCalculated;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public double getDurationDifference() {
        
        if (!this.differenceCalculated) {
            this.durationDifference = builds.get(1).getDuration() - builds.get(0).getDuration();
            this.differenceCalculated = true;
        }
        
        return durationDifference;
        
    }

    public String getDurationDifferenceFormated() {
        
        if (durationDifference < 0) {
            return "(" + DECIMAL_FORMAT.format(Math.abs(durationDifference)) + ")";
        } else{
            return DECIMAL_FORMAT.format(durationDifference);
        }
        
    }
    
}