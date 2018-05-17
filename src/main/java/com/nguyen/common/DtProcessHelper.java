package com.nguyen.common;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.util.List;

/**
 * @author RWM
 * @date 2018/5/14
 * @description:
 */
public final class DtProcessHelper {

    /**
     * @param workbook  创建的excel文件
     * @param sheetNum  sheet的位置，0表示第一个表格中的第一个sheet
     * @param sheetTitle    sheet的名称
     * @param headers   表格的标题
     * @param result    表格的数据
     */
    public static void exportExcel(HSSFWorkbook workbook, int sheetNum, String sheetTitle, String[] headers, List<List<String>> result) {
        // 新建一个表格sheet
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle);
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth(20);
        writeHeader(workbook, sheet, headers);
        writeRow(sheet, result);
    }

    private static void writeRow(HSSFSheet sheet, List<List<String>> result) {
        // 遍历集合数据，产生数据行
        if (result != null) {
            int index = 1;
            for (List<String> m : result) {
                // 产生表格标题行
                HSSFRow row = sheet.createRow(index);
                int cellIndex = 0;
                for (String str : m) {
                    writeCell(row, cellIndex, str);
                    cellIndex++;
                }
                index++;
            }
        }
    }

    private static void writeHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headers) {
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i, CellType.STRING);
            cell.setCellStyle(createStyle(workbook));
            cell.setCellValue(headers[i]);
        }
    }

    private static HSSFCellStyle createStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static void writeCell(HSSFRow row, int index, String value) {
        HSSFCell cell = row.createCell(index, CellType.STRING);
        cell.setCellValue(value);
    }
}
