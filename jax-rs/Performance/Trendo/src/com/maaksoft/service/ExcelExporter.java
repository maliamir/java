package com.maaksoft.service;

import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.maaksoft.rest.representations.ReportBuildInfo;
import com.maaksoft.rest.representations.ReportObject;

import com.maaksoft.common.junit.report.RecordsLoader;

public class ExcelExporter {

    public File export(List<ReportObject> reportObjectsList, String exportedResultsFolderPath, String calculatedColumnName) throws Exception {
    
        boolean showDifference = calculatedColumnName.equalsIgnoreCase("Difference");
        
        //Create blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet spreadsheet = workbook.createSheet(RecordsLoader.productName);

        //Create row object
        XSSFRow row = null;

        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Arial");
        font.setBold(true);
        font.setItalic(false);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(font);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        
        XSSFDataFormat dataFormat = workbook.createDataFormat();
        
        int rowIndex = 0;    
        List<ReportBuildInfo> buildsList = reportObjectsList.get(0).getBuilds();
        row = spreadsheet.createRow(rowIndex++);

        int cellIndex = 0;
        Cell cell = row.createCell(cellIndex++);
        cell.setCellValue("Test Record");
        cell.setCellStyle(cellStyle);

        if (showDifference) {
            cell = row.createCell(cellIndex++);
            cell.setCellValue(calculatedColumnName);
            cell.setCellStyle(cellStyle);
        }
        
        for (ReportBuildInfo reportBuildInfo: buildsList) {            
            
            cell = row.createCell(cellIndex++);
            cell.setCellValue(reportBuildInfo.getProductVersion());
            cell.setCellStyle(cellStyle);
            
        }
        
        cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(dataFormat.getFormat("#,##0.000;[Red](#,##0.000)"));
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);        
        for (ReportObject reportObject: reportObjectsList) {
            
            row = spreadsheet.createRow(rowIndex++);
            
            cellIndex = 0;
            cell = row.createCell(cellIndex++);
            cell.setCellValue(reportObject.getTestRecordName());
            cell.setCellStyle(cellStyle);

            if (showDifference) {
                cell = row.createCell(cellIndex++);
                cell.setCellValue(reportObject.getAverageDuration());
                cell.setCellStyle(cellStyle);
            }
            
            buildsList = reportObject.getBuilds();
            for (ReportBuildInfo reportBuildInfo: buildsList) {
                cell = row.createCell(cellIndex++);
                cell.setCellValue(reportBuildInfo.getDuration());
                cell.setCellStyle(cellStyle);
            }

        }
        
        int limit = (buildsList.size() + 1);
        for (int columnIndex = 0; columnIndex <= limit; columnIndex++) {
            spreadsheet.autoSizeColumn(columnIndex);
        }
        spreadsheet.createFreezePane(1, 1);
        
        //Write the workbook in file system
        File exportedFile = new File((exportedResultsFolderPath + "/Exported_Test_Results_" + (new Date()).getTime() + ".xlsx"));
        exportedFile.delete();
        FileOutputStream fileOutputStream = new FileOutputStream(exportedFile);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        
        System.out.println((exportedFile.getAbsolutePath() + " written successfully."));
        
        return exportedFile;
        
    }
    
}