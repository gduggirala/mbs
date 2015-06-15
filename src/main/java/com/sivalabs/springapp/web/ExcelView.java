package com.sivalabs.springapp.web;

import com.sivalabs.springapp.reports.pojo.DailyOrderGround;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * User: duggirag
 * Date: 1/18/15
 * Time: 6:26 PM
 */
public class ExcelView extends AbstractExcelView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<DailyOrderGround> dailyOrderGroundList = (List<DailyOrderGround>) model.get("dailyOrderGroundList");
/*        Group<DailyOrderGround> dailyOrderGroundGroup = Lambda.group(dailyOrderGroundList, by(on(DailyOrderGround.class).getSector()));
        Double totalBmOrderFromList = sum(dailyOrderGroundList, on(DailyOrderGround.class).getBmOrder());
        Double totalCmOrderFromList = sum(dailyOrderGroundList, on(DailyOrderGround.class).getCmOrder());*/
        response.setHeader("content-disposition", "Daily Order - " + model.get("formattedDateForReport"));
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Pragma", "public");

        HSSFSheet sheet = workbook.createSheet("Day " + model.get("formattedDateForReport"));
        HSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Serial #");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Address");
        header.createCell(3).setCellValue("Phone");
        header.createCell(4).setCellValue("BM Order");
        header.createCell(5).setCellValue("CM Order");
        header.createCell(6).setCellValue("Sector");

        int rowNumber = 1;
        for (DailyOrderGround dailyOrderGround : dailyOrderGroundList) {
            HSSFRow row = sheet.createRow(rowNumber++);
            HSSFCell serialNumberCell = row.createCell(0);
            serialNumberCell.setCellType(Cell.CELL_TYPE_NUMERIC);
            serialNumberCell.setCellValue(new Integer(dailyOrderGround.getGivenSerialNumber()));
            row.createCell(1).setCellValue(dailyOrderGround.getName());
            row.createCell(2).setCellValue(dailyOrderGround.getAddress1());
            HSSFCell phoneCell = row.createCell(3);
            phoneCell.setCellType(Cell.CELL_TYPE_STRING);
            phoneCell.setCellValue("'"+dailyOrderGround.getPhone()+"'");
            row.createCell(4).setCellValue(dailyOrderGround.getBmOrder());
            row.createCell(5).setCellValue(dailyOrderGround.getCmOrder());
            row.createCell(6).setCellValue(dailyOrderGround.getSector());
        }

    }
}
