package com.nguyen.springbootdocker.utils;

import com.nguyen.springbootdocker.common.DtProcessHelper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RWM
 * @date 2018/5/14
 * @description:
 */
public class PoiTest {

    public static void main(String[] args) {
        try {
            OutputStream out = new FileOutputStream("E:\\门诊预约成功名单.xls");
            List<List<String>> data = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                List rowData = new ArrayList();
                rowData.add("阮威敏");
                rowData.add("362329199409141137");
                rowData.add("邵志敏");
                rowData.add("随访预约");
                rowData.add("2018-05-14");
                rowData.add("09:00-11:30");
                data.add(rowData);
            }
            String[] headers = { "患者姓名", "证件号码", "预约医生", "预约类型", "门诊日期", "门诊时间" };

            HSSFWorkbook workbook = new HSSFWorkbook();
            DtProcessHelper.exportExcel(workbook, 0, "上海", headers, data);
            DtProcessHelper.exportExcel(workbook, 1, "深圳", headers, data);
            DtProcessHelper.exportExcel(workbook, 2, "广州", headers, data);
            //原理就是将所有的数据一起写入，然后再关闭输入流。
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
