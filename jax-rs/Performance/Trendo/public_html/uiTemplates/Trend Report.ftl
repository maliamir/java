<html>

    <head>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>API Performance Trend</title>

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

        <script src="http://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
        <script type="text/javascript">

              function showDialog(imgObj) {
                  
                  $("#dialog-modal").html(imgObj)
                  $("#dialog-modal").dialog({
                        width: 1100,
                        height: 800,
                        open: function(event, ui) {
                          var textarea = $('<textarea style="height: 276px;">');
                          $(textarea).redactor({
                              focus: true,
                              autoresize: false,
                              initCallback: function() {
                                  this.set('<p>Lorem...</p>');
                              }
                          });
                        }
                  });
                  
              }

              function loadGraph(testcaseName, graphImgSrcUrl) {
                  
                  var imgSrcObj = document.getElementById(("graphImgSrc_" + testcaseName));
                  var graphImgContainerObj = document.getElementById(("graphImgContainer_" + testcaseName));


                  if (graphImgContainerObj.style.display == "none") {
                      graphImgContainerObj.style.display = "block";
                      imgSrcObj.src = graphImgSrcUrl;
                  } else {
                      graphImgContainerObj.style.display = "none";
                      imgSrcObj.src = "";
                  }
                  
              }

              function loadScenario(testcaseName, graphImgSrcUrl) {
              
                  document.getElementById("scenarioName").value = testcaseName;
                  document.getElementById("graphImageUrl").value = graphImgSrcUrl;
                  
                  document.forms[0].action = "results";                  
                  document.forms[0].target = "_blank";
                  document.forms[0].submit();   
                  
              }
              
	      function loadScenarios(selectObj){  

                  document.getElementById("scenarioName").value = null;
                  document.getElementById("graphImageUrl").value = null;
              
                  document.forms[0].action = "results";
                  document.forms[0].target = "_self";
                  document.forms[0].submit();
                  
              }
              
              function compare(btnObj) {
              
                  var selectedBuildsObj = document.getElementsByName('selectedBuilds');
                  var selectedBuildsArr = [];
                  var selectedBuildNumbers = '';  

                  var arrCount = 0;
                  for (index = 0; index < selectedBuildsObj.length; index++) {
                    
                        if (selectedBuildsObj[index].checked) {
                        
                            selectedBuildsArr[arrCount] = selectedBuildsObj[index].value;                            
                            if (selectedBuildsArr.length > 2) {
                                 break;
                            } else{
                                selectedBuildNumbers += (document.getElementById(('buildNumber_' + selectedBuildsArr[arrCount])).value + ",");
                            }                            
                            arrCount++;
                            
                        }
                        
                  }

                  if (selectedBuildsArr.length != 2) {
                      alert("Please select 2 Builds for Comparison.");
                      return 0;
                  } else if (selectedBuildsArr.length == 2) {
                         
                      var reportObjects = new Array();  
                      var reportObjectsCount = 0;   
                      for (scenarioIndex = 1; scenarioIndex <= ${testRecords?size}; scenarioIndex++) {
                      
                          var reportObject = new Object();
                          reportObject.testRecordName = document.getElementById(('Scenario_' + scenarioIndex)).value;
                          reportObject.builds = new Array();
                          
                          var buildsCount = 0;                  
                          for (index = 0; index < selectedBuildsArr.length; index++) {
                      
                               buildIndex = selectedBuildsArr[index];                               
                               
                               var buildObject = new Object();  
                               buildObject.runId = document.getElementById(('runId_' + buildIndex)).value; 
                               buildObject.buildNumber = document.getElementById(('buildNumber_' + buildIndex)).value; 
                               buildObject.productVersion = document.getElementById(('productVersion_' + buildIndex)).value; 
                               
                               var duarationObject = document.getElementById((scenarioIndex + '_duration_' + buildIndex));
                               if (duarationObject == null) {
                                    buildObject.duration = 0;
                               } else {
                                    buildObject.duration = duarationObject.value;
                               }                             
                               
                               reportObject.builds[buildsCount++] = buildObject;
                           
                          }
                          reportObjects[reportObjectsCount++] = reportObject;
                          
                      }
                      
                      //document.getElementById('selectedBuildNumbers').value = selectedBuildNumbers;
                      document.getElementById('reportJson').value = JSON.stringify(reportObjects);
                      document.forms[0].target = "_blank";
                      document.forms[0].action = "compareResults";
                      //document.forms[0].submit();   
                      
                      var reportType = document.getElementById('reportType').value;
                      var scenarioName = document.getElementById('scenarioName').value;
                      
                      var scenarioParam = "";
                      if (scenarioName.length > 0) {
                          scenarioParam = ("&scenarioName=" + scenarioName);
                      }
                      var comparisonUrl = ("compareBuildResults?reportType=" + reportType + "&selectedBuildNumbers=" + selectedBuildNumbers + scenarioParam);
                      var windowObj = window.open(comparisonUrl, '_blank');
                      windowObj.focus();
                      
                  }
                  
              }
              
              function loadScenarioGraph(scenarioIndex) {
              
                  var reportObject = new Object();
                  reportObject.testRecordName = document.getElementById(('Scenario_' + scenarioIndex)).value;
                  reportObject.builds = new Array();
                  
                  var buildsCount = 0;
                  var selectedBuildsObj = document.getElementsByName('selectedBuilds');
                  for (index = 0; index < selectedBuildsObj.length; index++) {
              
                       var buildObject = new Object();  
                       buildIndex =  selectedBuildsObj[index].value;                               
                       buildObject.runId = document.getElementById(('runId_' + buildIndex)).value; 
                       buildObject.buildNumber = document.getElementById(('buildNumber_' + buildIndex)).value; 
                       
                       var duarationObject = document.getElementById((scenarioIndex + '_duration_' + buildIndex));
                       if (duarationObject == null) {
                            buildObject.duration = 0;
                       } else {
                            buildObject.duration = duarationObject.value;
                       }                                                    

                       reportObject.builds[buildsCount++] = buildObject;
                       
                  }
                  
                  var jsonString = JSON.stringify(reportObject);
                  var graphUrl = new String(window.location.href);
                  graphUrl = (graphUrl.substring(0, (graphUrl.lastIndexOf("/") + 1)) + "scenarioGraph?reportJson=" + jsonString);
                  $.ajax({  url: graphUrl, type: "POST", 
                            success: function(imgObj){
                                showDialog(imgObj);
                            }
                         }
                  );
                        
              }
              
              function loadSlowestTests(buildNumber) {
              
                  /*
                  document.getElementById("buildNumber").value = buildNumber;
              
                  document.forms[0].action = "slowestTests";
                  document.forms[0].target = "_blank";
                  document.forms[0].submit();
                  */
                  
                  var slowestTestsUrl = ("slowestTests?buildNumber=" + buildNumber);
                  var windowObj = window.open(slowestTestsUrl, '_blank');
                  windowObj.focus();
              
              }
              
              function exportResults(btnObj) {
              
                  btnObj.disabled = true;
                  
                  var reportObjects = new Array();  
                  var reportObjectsCount = 0;   
                  for (scenarioIndex = 1; scenarioIndex <= ${testRecords?size}; scenarioIndex++) {
                  
                      var reportObject = new Object();
                      reportObject.testRecordName = document.getElementById(('Scenario_' + scenarioIndex)).value;
                      
                      /*
                      var totalDuration = document.getElementById(('totalDuration_' + scenarioIndex)).value;
                      totalDuration = totalDuration.replace(",", "");
                      reportObject.averageDuration = totalDuration;
                      */
                      
                      reportObject.builds = new Array();
                      
                      var buildsCount = 0;                  
                      for (buildIndex = 1; buildIndex <= ${builds?size}; buildIndex++) {
                  
                           var buildObject = new Object();  
                           buildObject.runId = document.getElementById(('runId_' + buildIndex)).value; 
                           buildObject.buildNumber = document.getElementById(('buildNumber_' + buildIndex)).value; 
                           buildObject.productVersion = document.getElementById(('productVersion_' + buildIndex)).value; 
                           
                           var duarationObject = document.getElementById((scenarioIndex + '_duration_' + buildIndex));
                           if (duarationObject == null) {
                                buildObject.duration = 0;
                           } else {
                                buildObject.duration = duarationObject.value;
                           }
                           
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
            
            <#if testRecords?has_content && (testRecords?size > 0)>
            
                <div id="dialog-modal" title="Performance Trend" style="display: none;"></div>
      
                <input type="hidden" id="fromCompare" name="fromCompare" value="${fromCompare?c}"/>
                <input type="hidden" id="scenarioName" name="scenarioName" value="${testCaseName}"/>
                <input type="hidden" id="graphImageUrl" name="graphImageUrl"/>
                <input type="hidden" id="reportJson" name="reportJson"/>
                <input type="hidden" id="reportType" name="reportType" value="${reportType}"/>
                <input type="hidden" id="selectedBuildNumbers" name="selectedBuildNumbers"/>
                <input type="hidden" id="buildNumber" name="buildNumber"/>
    
                <div align="center">
        
                    <div align="center">
                        <b>${productName} - API Performance Trend<!-- between label:--></b>
                        <!--
                        <select name="productVersion1">
                           <#list productVersions as productVersion>
                              <option value="${productVersion}"<#if productVersion == productVersionStart> selected</#if>>${productVersion}</option>
                           </#list>
                        </select> <b>and</b>
                        <select name="productVersion2">
                           <#list productVersions as productVersion>
                              <option value="${productVersion}"<#if productVersion == productVersionEnd> selected</#if>>${productVersion}</option>
                           </#list>
                        </select>
                        -->
                    </div>
                    <br><br>    
        
                    <div align="center">
                        <select name="numberOfBuilds" onchange="javascript:loadScenarios(this);">
                            <#list 10..30 as i>
                              <#if i%5 == 0>
                                <option value="${i}"<#if i == numberOfBuilds> selected</#if>>${i}</option>
                              </#if>
                            </#list>
                        </select> <b>Most Recent Builds</b>
                    </div>            
                    <img src="${trendGraphFileUrl}"/>
                    <br><br>    
        
                </div>
                
                <div id="btncontainer" style="display: none;">
                    <#if !fromCompare>
                       <input class="ui-button ui-widget ui-corner-all" id="compareBtn" type="button" value="Compare" onclick="javascript:compare(this);"/>
                    </#if>
                    <#if (testRecords?size > 0)>
                        <input class="ui-button ui-widget ui-corner-all" id="exportBtn" type="button" value="Export" onclick="javascript:exportResults(this);"/>
                    </#if>
                    <p></p><p></p><p></p>
                </div>
                        
                <table cellpadding="5">
                    <tr>
                        <td valign="top"><b>${reportType}</b></td>
                        <!--td align="center" valign="top"><b>Total</b></td-->
                        <#list builds as build>  
                            <td align="center">
    
                                <input type="hidden" id="buildNumber_${build_index + 1}" name="buildNumber_${build_index + 1}" value="${build.number}"/>
                                <input type="hidden" id="productVersion_${build_index + 1}" name="buildNumber_${build_index + 1}" value="${build.productVersion}"/>
                                <input type="hidden" id="runId_${build_index + 1}" name="runId_${build_index + 1}" value="${build.runId}"/>
                                
                                <#if !fromCompare>
                                    <input type="checkbox" name="selectedBuilds" value="${build_index + 1}" buildNumber="${build.number}"/>
                                </#if>                        
                                
                                <#assign color = "green">
                                <#if (build.difs > 0)>
                                    <#assign color = "red">
                                <#elseif (build.skips > 0)>
                                    <#assign color = "#E9AB17">
                                </#if>  
                                <a href="javascript:void(0);" onclick="javascript:loadSlowestTests(${build_index + 1});"><label title="Version: ${build.productVersion}; ${build.sucs} sucs; ${build.difs} difs; ${build.skips} skips"><b><font color="${color}">#${build.number}</font></b></label></a>
                                <br>
    
                                <a href="#?runid=${build.runId}" target="_blank"><img src="${rootUrl}/appImages/sapphire.gif" alt="Results at Sapphire"></a>
                                <a href="#&runid=${build.runId}" target="_blank"><img src="${rootUrl}/appImages/folder.gif" alt="Results at TLP"></a>
                                
                            </td>
                        </#list>
                    </tr>
    
                    <tr>
                        <td align="left"><b>Total Duration (${totalBuildDurationFormat})</b></td>
                        <#list formattedBuildTotalDurations as formattedBuildTotalDuration>  
                            <td align="right"><b>${formattedBuildTotalDuration}</b></td>     
                        </#list>                
                    </tr>
                    
                    <#list testRecords as testRecord>  
                        <tr>
                            <td>
                                <input type="hidden" id="Scenario_${testRecord_index+1}" name="Scenario_${testRecord_index+1}" value="${testRecord.name}"/>
                                
                                <!--                            
                                <img src="${rootUrl}/appImages/barChart.png" onclick="javascript:loadGraph('${testRecord.name}', '${testRecord.graphImageFileUrl}');"/>
                                -->
                                
                                <#if !fromCompare> 
                                    <img src="${rootUrl}/appImages/barChart.png" onclick="javascript:loadScenarioGraph(${testRecord_index+1});"/>
                                </#if>                            
                                
                                <#if reportType == "Scenario"> 
                                    <a href="javascript:void(0);" onclick="javascript:loadScenario('${testRecord.name}', '${testRecord.graphImageFileUrl}');">${testRecord.name}</a>
                                <#else>
                                    ${testRecord.name}
                                </#if>                            
                                
                                <div id="graphImgContainer_${testRecord.name}" title="Trend Graph for ${testRecord.name}" style="display: none;">
                                    <img id="graphImgSrc_${testRecord.name}" src="${testRecord.graphImageFileUrl}"/>
                                </div>
                            </td>
                            <!--
                            <td align="right">
                                <input type="hidden" id="totalDuration_${testRecord_index + 1}" name="totalDuration_${testRecord_index + 1}" value="${testRecord.totalDuration}"/>
                                ${testRecord.totalDuration}
                            </td>
                            -->
                            <#list testRecord.durationsByBuild as durationByBuild>
                              <input type="hidden" id="${testRecord_index+1}_duration_${durationByBuild_index + 1}" name="${testRecord_index+1}_duration_${durationByBuild_index + 1}" value="${durationByBuild}"/>
                              <td align="right">${durationByBuild}</td>     
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