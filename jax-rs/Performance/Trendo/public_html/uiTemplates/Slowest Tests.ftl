<html>

   <head>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Slowest Tests in ${productName}</title>

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
        
            function loadSlowestTests() {
              
                 var btnObj = document.getElementById("fetchBtn");
                 btnObj.disabled = true;
                 
                 var testCasesCountObj = document.getElementById("testCasesCount");
                 if (isNaN(testCasesCount.value)) {
                 
                     alert("ONLY numeric value is allowed for Testcases Count.");
                     btnObj.disabled = false;
                     testCasesCountObj.focus();
                     return false;
                     
                 } else {                 
                     
                     /*
                     document.forms[0].action = "slowestTests";                  
                     document.forms[0].target = "_self";
                     document.forms[0].submit();   
                     */
                     
                     var buildNumber = document.getElementById("buildNumber").value;
                     var testCasesCountValue = testCasesCount.value;
                     
                     var slowestTestsUrl = ("slowestTests?buildNumber=" + buildNumber + "&testCasesCount=" + testCasesCountValue);
                     location.href = slowestTestsUrl;
                     
                     return false;
                     
                 }
                 
            }

            function loadScenario(scenarioName) {
              
                 document.getElementById("scenarioName").value = scenarioName;                 
                  
                 document.forms[0].action = "results";                  
                 document.forms[0].target = "_blank";
                 document.forms[0].submit();   
                  
            }
            
        </script>
        
    </head>
    
    <body>
    
        <form action="slowestTests" method="POST" onsubmit="return loadSlowestTests();">

            <#if testRecords?has_content && (testRecords?size > 0)>
            
                <div align="center" valign="bottom">
                    <input id="testCasesCount" name="testCasesCount" value="${testCasesCount}" maxlength="3" style="width: 30px"><b>Slowest Tests in ${productName}</b>
                    <input class="ui-button ui-widget ui-corner-all" id="fetchBtn" type="button" value="Fetch" onclick="javascript:loadSlowestTests();" style="width: 90px"/> 
                </div>
    
                <input type="hidden" id="buildNumber" name="buildNumber" value="${buildNumber}"/>
                <input type="hidden" id="scenarioName" name="scenarioName"/>
                
                <table cellpadding="5" witdth="90%">
    
                    <tr>
                        <td><b>#</td>
                        <td><b>Duration (ms)</b></td>
                        <td><b>Testcase</b></td>
                        <td><b>Scenario</b></td>
                    </tr>
                
                    <#list testRecords as testRecord>  
                        <tr>
                            <td>${testRecord_index + 1}</td>
                            <td>${testRecord.averageDuration}</td>
                            <#assign testCaseFullPath = (testRecord.path + "/" + testRecord.name)>
                            <td><a href="javascript:void(0);" onclick="javascript:loadScenario('${testCaseFullPath}');">${testRecord.name}</a></td>
                            <td><a href="javascript:void(0);" onclick="javascript:loadScenario('${testRecord.path}');">${testRecord.path}</a></td>
                        </tr>
                    </#list>
                    
                    <br><br>
                    
                </table>

            <#else>
            
                <font color="red"><b>No Test Records found !!!</b></b>

            </#if>
            
        </form>
        
    </body>
    
</html>