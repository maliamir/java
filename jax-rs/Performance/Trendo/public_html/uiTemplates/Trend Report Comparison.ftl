<html>

    <head>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>API Performance Trend - Comparison</title>
        
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css">        
        <style type="text/css">
            table {
                border-collapse: collapse;
            }
            
            table, th, td {
                border: 1px solid black;
            }

            body {
              font-family: 'Open Sans', sans-serif;
            }            
    </style>

        <script type="text/javascript">
                 
              function loadScenario(testcaseName, graphImgSrcUrl) {
              
                  document.getElementById("scenarioName").value = testcaseName;
                  document.getElementById("graphImageUrl").value = graphImgSrcUrl;
                  
                  document.forms[0].target = "_blank";
                  document.forms[0].action = "results";
                  document.forms[0].submit();   
                  
              }
              
              function compare(scenarioName) {
                                    
                  var selectedBuildNumbers = document.getElementById('selectedBuildNumbers').value;
                  var comparisonUrl = ("compareBuildResults?reportType=Testcase&selectedBuildNumbers=" + selectedBuildNumbers + "&scenarioName=" + scenarioName);
                  var windowObj = window.open(comparisonUrl, '_blank');
                  windowObj.focus();
                  
              }
              
              function exportResults(btnObj) {
              
                  btnObj.disabled = true;
                  
                  var reportObjects = new Array();  
                  var reportObjectsCount = 0;   
                  for (reportObjectIndex = 1; reportObjectIndex <= ${reportObjects?size}; reportObjectIndex++) {
                  
                      var reportObject = new Object();
                      reportObject.testRecordName = document.getElementById(('ReportObject_' + reportObjectIndex)).value;
                      reportObject.averageDuration = document.getElementById(('difference_' + reportObjectIndex)).value;
                      reportObject.builds = new Array();
                      
                      var buildsCount = 0;                  
                      for (buildIndex = 1; buildIndex <= 2; buildIndex++) {
                  
                           var buildObject = new Object();  
                           buildObject.runId = document.getElementById(('runId_' + buildIndex)).value; 
                           buildObject.buildNumber = document.getElementById(('buildNumber_' + buildIndex)).value; 
                           buildObject.productVersion = document.getElementById(('productVersion_' + buildIndex)).value; 
                           buildObject.duration = document.getElementById((reportObjectIndex + '_duration_' + buildIndex)).value;; 
                           reportObject.builds[buildsCount++] = buildObject;
                       
                      }
                      reportObjects[reportObjectsCount++] = reportObject;
                      
                  }
                  
                  document.getElementById('reportJson').value = JSON.stringify(reportObjects);
                  document.forms[0].target = "_blank";
                  document.forms[0].action = "exportResults";
                  document.forms[0].submit(); 
                  
                  btnObj.disabled = false;
                  
              }   
              
              function showButtons() {
                  document.getElementById('btncontainer').style.display = "block";
              }              

        </script>
        
    </head>
    
    <body onload="javascript:showButtons();">
    
        <form action="results" method="POST">

            <#if reportObjects?has_content && (reportObjects?size > 0)>
            
                <input type="hidden" id="fromCompare" name="fromCompare" value="true"/>
                <input type="hidden" id="scenarioName" name="scenarioName"/>
                <input type="hidden" id="graphImageUrl" name="graphImageUrl"/>
                <input type="hidden" id="selectedBuildNumbers" name="selectedBuildNumbers" value="${selectedBuildNumbers}"/>
                <input type="hidden" id="reportJson" name="reportJson"/>
                
                <img src="${trendGraphFileUrl}"/>
                <br>               
            
                <div id="btncontainer" style="display: none;">
                    <input class="ui-button ui-widget ui-corner-all" type="button" value="Export" onclick="javascript:exportResults(this);"/>
                    <p></p><p></p><p></p>
                </div>
                
                <table cellpadding="5">
                
                    <tr>
                        <td><b>${reportType}</b></td>
                        <td align="center"><b>Difference</b></td>
                        <#list reportObjects[0..*1] as reportObject>
                            <#list reportObject.builds as build>
                                <td align="center">
                                    <label title="Version: ${build.productVersion}">
                                        <b>#${build.buildNumber}</b>
                                    </label>
                                    
                                    <input type="hidden" id="buildNumber_${build_index + 1}" name="buildNumber_${build_index + 1}" value="${build.buildNumber}"/>
                                    <input type="hidden" id="productVersion_${build_index + 1}" name="buildNumber_${build_index + 1}" value="${build.productVersion}"/>
                                    <input type="hidden" id="runId_${build_index + 1}" name="runId_${build_index + 1}" value="${build.runId}"/>
    
                                    <a href="#?runid=${build.runId}" target="_blank"><img src="${rootUrl}/appImages/sapphire.gif" alt="Results at Sapphire"></a>
                                    <a href="#&runid=${build.runId}" target="_blank"><img src="${rootUrl}/appImages/folder.gif" alt="Results at TLP"></a>
                                </td>
                            </#list>
                        </#list>
                    </tr>
                    
                    <#list reportObjects as reportObject>  
                        <#assign color = "green">
                        <#if (reportObject.durationDifference < 0)>
                            <#assign color = "red">
                        </#if>                    
                        <tr>
                            <td>
                                <input type="hidden" id="ReportObject_${reportObject_index+1}" name="ReportObject_${reportObject_index+1}" value="${reportObject.testRecordName}"/>
                                <#if reportType == "Scenario" || reportType == "scenario"> 
                                    <!--
                                    <a href="javascript:void(0);" onclick="javascript:loadScenario('${reportObject.testRecordName}', '');"><font color="${color}">${reportObject.testRecordName}</font></a>
                                    -->
                                    <a href="javascript:void(0);" onclick="compare('${reportObject.testRecordName}');"><font color="${color}">${reportObject.testRecordName}</font></a>
                                <#else>
                                    <font color="${color}">${reportObject.testRecordName}</font>
                                </#if>
                            </td>
                            <td align="right">
                                <input type="hidden" id="difference_${reportObject_index+1}" name="difference_${reportObject_index+1}" value="${reportObject.durationDifference}"/>
                                <b><font color="${color}">${reportObject.durationDifferenceFormated}</font></b>
                            </td>
                            <#list reportObject.builds as build>                          
                              <td align="right">
                                <input type="hidden" id="${reportObject_index+1}_duration_${build_index+1}" name="${reportObject_index+1}_duration_${reportObject_index+1}" value="${build.duration}"/>
                                ${build.duration}
                              </td>     
                            </#list>
                        </tr>
                    </#list>
                    
                </table>
                    
                <br><br><br><br>                

            <#else>
            
                <font color="red"><b>No Test Records found !!!</b></b>
                
            </#if>
            
        </form>
        
    </body>
    
</html>