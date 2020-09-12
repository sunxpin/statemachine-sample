package com.baoxian.common.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ExcelUtil {
	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class); 

	
	public static SXSSFWorkbook writeRecords(SXSSFWorkbook wb, String records, int col) {
		CellStyle styleBorderBold = getDataCellStyle(wb);
		SXSSFSheet sheet = (SXSSFSheet) wb.getSheetAt(0);
		int rowId = sheet.getPhysicalNumberOfRows();

		int i = 0;
		int j = 0;
		String[] record = records.split(",");
		Row row = null;
		for (String data : record) {
			if (i % col == 0) {
				row = sheet.createRow(rowId++);
				j = 0;
			}
			Cell cell = row.createCell(j);
			cell.setCellStyle(styleBorderBold);
			cell.setCellValue(data == null ? "" : data.replaceAll("%2C", ","));
			i++;
			j++;
		}
		for(; j < col; j++){
			Cell cell = row.createCell(j);
			cell.setCellStyle(styleBorderBold);
			cell.setCellValue("");
			j++;
		}
		return wb;
	}
	
	public static SXSSFWorkbook writeRecords(SXSSFWorkbook wb, Object[][] records) {
		CellStyle styleBorderBold = getDataCellStyle(wb);
		SXSSFSheet sheet = (SXSSFSheet) wb.getSheetAt(0);
		int rowId = sheet.getPhysicalNumberOfRows();
		for(Object record[] : records){
			Row row = sheet.createRow(rowId++);
			int i = 0;
			for (Object data : record) {
				Cell cell = row.createCell(i++);
				cell.setCellStyle(styleBorderBold);
				cell.setCellValue(data != null ? String.valueOf(data) : "");
			}
		}
		try {
			if(rowId % 1000 == 0)
				sheet.flushRows();
		} catch (IOException e) {
			logger.error("", e);
		}
		return wb;
	}

	public static SXSSFWorkbook writeRecord(SXSSFWorkbook wb, Object[] record) {
		CellStyle styleBorderBold = getDataCellStyle(wb);
		SXSSFSheet sheet = (SXSSFSheet) wb.getSheetAt(0);
		int rowId = sheet.getPhysicalNumberOfRows();
		Row row = sheet.createRow(rowId++);
		int i = 0;
		for (Object data : record) {
			Cell cell = row.createCell(i++);
			cell.setCellStyle(styleBorderBold);
			cell.setCellValue(data != null ? String.valueOf(data) : "");
		}
		try {
			if(rowId % 1000 == 0)
				sheet.flushRows();
		} catch (IOException e) {
			logger.error("", e);
		}
		return wb;
	}

	public static SXSSFWorkbook createWorkbook(String titles, String widths) {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		SXSSFSheet sheet = (SXSSFSheet) wb.createSheet();

		try {
			CellStyle styleBorderBold = getHeaderCellStyle(wb);
			Row firstRow = sheet.createRow(0);
			firstRow.setHeightInPoints(23);
			String title[] = titles.split(",");
			String width[] = widths.split(",");
			for (int i = 0; i < title.length; i++) {
				Cell cell = firstRow.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleBorderBold);
				cell.setCellValue(title[i]);
				sheet.setColumnWidth(i, new Double(Double.parseDouble(width[i]) * 50).intValue());
			}

		} catch (Exception e) {
			logger.error("导出excel异常", e);
		}
		return wb;
	}

	private static CellStyle getHeaderCellStyle(SXSSFWorkbook wb) {
		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 10);
		fontBold.setFontName("宋体");
		fontBold.setBold(true);
		CellStyle styleBorderBold = wb.createCellStyle();
		styleBorderBold.setBorderBottom(BorderStyle.THIN);
		styleBorderBold.setBorderLeft(BorderStyle.THIN);
		styleBorderBold.setBorderRight(BorderStyle.THIN);
		styleBorderBold.setBorderTop(BorderStyle.THIN);
		styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBorderBold.setAlignment(HorizontalAlignment.CENTER);
		styleBorderBold.setWrapText(true);
		styleBorderBold.setFont(fontBold);
		return styleBorderBold;
	}

	private static CellStyle getDataCellStyle(SXSSFWorkbook wb) {
		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 10);
		fontBold.setFontName("宋体");
		fontBold.setBold(false);
		CellStyle styleBorderBold = wb.createCellStyle();
		styleBorderBold.setBorderBottom(BorderStyle.THIN);
		styleBorderBold.setBorderLeft(BorderStyle.THIN);
		styleBorderBold.setBorderRight(BorderStyle.THIN);
		styleBorderBold.setBorderTop(BorderStyle.THIN);
		styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBorderBold.setAlignment(HorizontalAlignment.LEFT);
		styleBorderBold.setWrapText(false);
		styleBorderBold.setFont(fontBold);
		return styleBorderBold;
	}
}
