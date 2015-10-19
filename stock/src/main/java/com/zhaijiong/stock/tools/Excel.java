package com.zhaijiong.stock.tools;

import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-19.
 */
public class Excel {
    private String filePath;
    private HSSFWorkbook workbook;
    private int rowNum = 1;
    private List<String> headers;

    public Excel(String filePath){
        this.filePath = filePath;
        workbook = new HSSFWorkbook();
    }

    public void close(){
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HSSFSheet getSheet(String name){
        HSSFSheet sheet = workbook.getSheet(name);
        if(sheet==null){
            sheet = workbook.createSheet(name);
        }
        return sheet;
    }

    public void setHeader(String sheetName,List<String> columnNames){
        headers = columnNames;
        HSSFSheet sheet = getSheet(sheetName);
        HSSFRow row = sheet.createRow(0);
        for(int i=0;i<columnNames.size();i++){
            row.createCell(i).setCellValue(columnNames.get(i));
        }
    }

    public void writeRow(String sheetName,List<String> fields){
        HSSFSheet sheet = getSheet(sheetName);
        HSSFRow row = sheet.createRow(rowNum++);
        for(int i=0;i<fields.size();i++){
            row.createCell(i).setCellValue(fields.get(i));
        }
    }
}
