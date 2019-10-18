package com.maaksoft.rest.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Build implements ITrendInfo {
   
    private int sucs;
    private int difs;
    private int skips;

    private String number;
    private String productVersion;
    private String runId;
    
    public static String trimBuildNumber(String buildNumber) {
        
        if(buildNumber.lastIndexOf("_") > 0) {
            buildNumber = buildNumber.substring((buildNumber.lastIndexOf("_") + 1));
            //buildNumber = buildNumber.replaceAll("-", "");
        }
        
        return buildNumber;
        
    }
    
    @JsonCreator
    public Build(@JsonProperty("number") String number, @JsonProperty("productVersion") String productVersion, @JsonProperty("runId") String runId) {
        this.number = number;
        this.productVersion = productVersion;
        this.runId = runId;        
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getProductVersion() {
        productVersion = trimBuildNumber(productVersion);
        return productVersion;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void addSucs(int sucs) {
        this.sucs += sucs;
    }

    public int getSucs() {
        return sucs;
    }

    public void addDifs(int difs) {
        this.difs += difs;
    }

    public int getDifs() {
        return difs;
    }

    public void addSkips(int skips) {
        this.skips += skips;
    }

    public int getSkips() {
        return skips;
    }
    
}