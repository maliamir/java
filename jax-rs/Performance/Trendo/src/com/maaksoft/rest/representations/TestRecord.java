package com.maaksoft.rest.representations;

import java.util.List;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestRecord implements ITrendInfo {
    
    private double averageDuration;
    private double totalDuration;
    private double duration;
    
    private String name;
    private String path;
    private String status = "SUCCESS";
    private String graphImageFileUrl = "";
    
    private List<Double> durationsByBuild = new LinkedList<Double>();

    @JsonCreator
    public TestRecord(@JsonProperty("name") String name) {        
        this.name = name;
    }

    @JsonCreator
    public TestRecord(@JsonProperty("path") String path, @JsonProperty("name") String name, @JsonProperty("duration") Double duration) {
        
        this.path = path;
        this.name = name;
        
        //Yes it is duration not averageDuration. This constructor is used to instantiate Objects for Slowest Tests Report.
        //Setting it to agerageDuration field to re-use DurationComparator which works on this fields.
        this.averageDuration = duration;
        
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDurationsByBuild(List<Double> durationsByBuild) {
        this.durationsByBuild = durationsByBuild;
    }

    public List<Double> getDurationsByBuild() {
        return durationsByBuild;
    }

    public void setGraphImageFileUrl(String graphImageFileUrl) {
        this.graphImageFileUrl = graphImageFileUrl;
    }

    public String getGraphImageFileUrl() {
        return graphImageFileUrl;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }

    public double getTotalDuration() {
        
        if (totalDuration <=0 && durationsByBuild != null && durationsByBuild.size() > 0) { 

            totalDuration = 0;
            for (Double durationByBuild: durationsByBuild) {
                totalDuration += durationByBuild;
            }

        }
        
        return totalDuration;
    }

    public double getAverageDuration() {
        
        if (averageDuration <=0 && durationsByBuild != null && durationsByBuild.size() > 0) { 
            averageDuration = (this.getTotalDuration() / durationsByBuild.size());
        }
        
        return averageDuration;

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    
}