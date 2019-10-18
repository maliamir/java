<html>

    <body>
    
        <form action="results" method="POST">
            
            <table cellpadding="5">
                <tr>
                    <td valign="top"><b>${reportType}</b></td>
                    <td align="center" valign="top"><b>Average</b></td>
                    <#list builds as build>  
                        <td align="center">
                            <label title="Version: ${build.productVersion}; ${build.sucs} sucs; ${build.difs} difs; ${build.skips} skips"><b>#${build.number}</b></label>
                            <br>
                        </td>
                    </#list>
                </tr>

                <tr>
                    <td align="left" colspan="2"><b>Average Build Duration</b></td>
                    <#list buildAverageDurations as buildAverageDuration>  
                         <td align="right"><b>${buildAverageDuration}</b></td>     
                    </#list>                
                </tr>
                
                <#list testRecords as testRecord>  
                    <tr>
                        <td>
                            ${testRecord.name}
                        </td>
                        <td align="right">${testRecord.averageDuration}</td>
                        <#list testRecord.durationsByBuild as durationByBuild>
                          <td align="right">${durationByBuild}</td>     
                        </#list>
                    </tr>
                </#list>
                
                <br><br>
                
            </table>
                
        </form>
        
    </body>
    
</html>