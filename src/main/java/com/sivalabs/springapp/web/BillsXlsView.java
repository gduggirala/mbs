package com.sivalabs.springapp.web;

import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.Bill;
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
 * Created by duggirag on 6/24/2015 for Amazon.
 */
public class BillsXlsView extends AbstractExcelView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Bill> billListReports = (List<Bill>) model.get("billListReports");
        response.setHeader("content-disposition", "Bills for "+(String)model.get("billingMonth"));
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Pragma", "public");

        HSSFSheet sheet = workbook.createSheet((String)model.get("billingMonth"));
        HSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Serial #");
        header.createCell(1).setCellValue("Bill Id");
        header.createCell(2).setCellValue("From date");
        header.createCell(3).setCellValue("To Date");
        header.createCell(4).setCellValue("Generation Date");
        header.createCell(5).setCellValue("Total CM Qty");
        header.createCell(6).setCellValue("Total BM Qty");
        header.createCell(7).setCellValue("Total CM Price");
        header.createCell(8).setCellValue("Total BM Price");
        header.createCell(9).setCellValue("Discount");
        header.createCell(10).setCellValue("Other charges");
        header.createCell(11).setCellValue("Total amount");
        header.createCell(12).setCellValue("Discount reason");
        header.createCell(13).setCellValue("Paid amount");
        header.createCell(14).setCellValue("Balance amount");
        header.createCell(15).setCellValue("Pre. month Bal. amt");
        header.createCell(16).setCellValue("Is closed");
        header.createCell(17).setCellValue("BM Price Qty");
        header.createCell(18).setCellValue("CM Price Qty");
        header.createCell(19).setCellValue("Comment");
        header.createCell(20).setCellValue("Month");
        header.createCell(21).setCellValue("Billable amount");
        header.createCell(22).setCellValue("Payable Amount");
        header.createCell(23).setCellValue("Name");
        header.createCell(24).setCellValue("Sector");
        header.createCell(25).setCellValue("Address 1");
        header.createCell(26).setCellValue("Given Serial #");
        header.createCell(27).setCellValue("Phone");
        header.createCell(28).setCellValue("Daily BM Order");
        header.createCell(29).setCellValue("Daily CM Order");
        header.createCell(30).setCellValue("Customer Id");

        int rowNumber = 1;

        for (Bill blr:billListReports){
            HSSFRow row = sheet.createRow(rowNumber);
            HSSFCell serialNumberCell = row.createCell(0);
            serialNumberCell.setCellType(Cell.CELL_TYPE_NUMERIC);
            serialNumberCell.setCellValue(new Integer(rowNumber));
            row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getId());
            row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(DateUtils.getFormattedDateForReport(blr.getFromDate()));
            row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(DateUtils.getFormattedDateForReport(blr.getToDate()));
            row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(DateUtils.getFormattedDateForReport(blr.getGenerationDate()));
            row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getTotalCmQty());
            row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getTotalBmQty());
            row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getTotalCmPrice());
            row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getTotalBmPrice());;
            row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getDiscount());
            row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getOtherCharges());
            row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getTotalAmount());
            row.createCell(12, Cell.CELL_TYPE_STRING).setCellValue(blr.getDiscountReason());
            row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getPaidAmount());
            row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getBalanceAmount());
            row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getPreviousMonthsBalanceAmount());
            row.createCell(16, Cell.CELL_TYPE_BOOLEAN).setCellValue(blr.isClosed());
            row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getBmPerQuantityPrice());
            row.createCell(18, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getCmPerQuantityPrice());
            row.createCell(19, Cell.CELL_TYPE_STRING).setCellValue(blr.getComment());
            row.createCell(20,Cell.CELL_TYPE_STRING).setCellValue(blr.getMonth());
            row.createCell(21,Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getBillableAmount());
            row.createCell(22, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getPayableAmount());
            row.createCell(23, Cell.CELL_TYPE_STRING).setCellValue(blr.getCustomerName());
            row.createCell(24, Cell.CELL_TYPE_STRING).setCellValue(blr.getSector());
            row.createCell(25, Cell.CELL_TYPE_STRING).setCellValue(blr.getAddress1());
            row.createCell(26, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getGivenSerialNumber());
            row.createCell(27, Cell.CELL_TYPE_STRING).setCellValue(blr.getPhone());
            row.createCell(28, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getDailybmOrder());
            row.createCell(29, Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getDailyCmOrder());
            row.createCell(30,Cell.CELL_TYPE_NUMERIC).setCellValue(blr.getCustomerId());
            rowNumber++;
        }

    }
}
