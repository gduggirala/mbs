package com.sivalabs.springapp.web;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sivalabs.springapp.reports.pojo.DailyOrderGround;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;


public class PdfView extends AbstractItextPdfView {
    protected void buildPdfDocument(Map<String, Object> model, Document document,
                                    PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<DailyOrderGround> dailyOrderGroundList = (List<DailyOrderGround>) model.get("dailyOrderGroundList");
        Group<DailyOrderGround> dailyOrderGroundGroup = Lambda.group(dailyOrderGroundList, by(on(DailyOrderGround.class).getSector()));
        Double totalBmOrderFromList = sum(dailyOrderGroundList, on(DailyOrderGround.class).getBmOrder());
        Double totalCmOrderFromList = sum(dailyOrderGroundList, on(DailyOrderGround.class).getCmOrder());
        response.setHeader("content-disposition", "Daily Order - "+model.get("formattedDateForReport"));
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Pragma", "public");

        Paragraph sectorHeader = new Paragraph("Milk Order for the date :" + model.get("formattedDateForReport"));
        sectorHeader.setSpacingAfter(25);
        sectorHeader.setSpacingBefore(25);
        sectorHeader.setAlignment(Element.ALIGN_CENTER);
        sectorHeader.setIndentationLeft(50);
        sectorHeader.setIndentationRight(50);
        document.add(sectorHeader);
        //Last page Sector wise total Begin
        PdfPTable sectorTotalTable = new PdfPTable(3);
        PdfPCell sectorNameHeaderCell = new PdfPCell(new Paragraph("Sector name"));
        PdfPCell bmHeaderCell = new PdfPCell(new Paragraph("BM Order"));
        PdfPCell cmHeaderCell = new PdfPCell(new Paragraph("CM Order"));
        sectorNameHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        bmHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cmHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        sectorTotalTable.addCell(sectorNameHeaderCell);
        sectorTotalTable.addCell(bmHeaderCell);
        sectorTotalTable.addCell(cmHeaderCell);
        //Last page Sector wise total end
        for (String key : dailyOrderGroundGroup.keySet()) {
            Paragraph header = new Paragraph("Milk Order for the date :" + model.get("formattedDateForReport"));
            header.setSpacingAfter(25);
            header.setSpacingBefore(25);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setIndentationLeft(50);
            header.setIndentationRight(50);
            document.add(header);
            PdfPTable pdfPTable = getPdfPTable(key);
            List<DailyOrderGround> dailyOrderGrounds = dailyOrderGroundGroup.find(key);
            List<DailyOrderGround> dailyOrderGroundsSorted = sort(dailyOrderGrounds, on(DailyOrderGround.class).getGivenSerialNumber());
            for (DailyOrderGround dailyOrderGround : dailyOrderGroundsSorted) {
                PdfPCell serialNumberCell = new PdfPCell(new Paragraph(dailyOrderGround.getGivenSerialNumber()));
                PdfPCell nameCell = new PdfPCell(new Paragraph(dailyOrderGround.getName()));
                PdfPCell addressCell = new PdfPCell(new Paragraph(dailyOrderGround.getAddress1()));
                PdfPCell phoneCell = new PdfPCell(new Paragraph(dailyOrderGround.getPhone()));
                PdfPCell bmOrderCellCell = new PdfPCell(new Paragraph(dailyOrderGround.getBmOrder() + ""));
                PdfPCell cmOrderCellCell = new PdfPCell(new Paragraph(dailyOrderGround.getCmOrder() + ""));
                pdfPTable.addCell(serialNumberCell);
                pdfPTable.addCell(nameCell);
                pdfPTable.addCell(addressCell);
                pdfPTable.addCell(phoneCell);
                pdfPTable.addCell(bmOrderCellCell);
                pdfPTable.addCell(cmOrderCellCell);
            }
            PdfPCell totalCell = new PdfPCell(new Paragraph("Total"));
            totalCell.setColspan(4);
            totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell totalBmQty = new PdfPCell(new Paragraph(sumFrom(dailyOrderGroundsSorted).getBmOrder() + ""));
            PdfPCell totalCmQty = new PdfPCell(new Paragraph(sumFrom(dailyOrderGroundsSorted).getCmOrder() + ""));
            totalBmQty.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalCmQty.setBackgroundColor(BaseColor.LIGHT_GRAY);
            //Last page Sector wise total begin
            PdfPCell sectorNameCell = new PdfPCell(new Paragraph(key));
            sectorTotalTable.addCell(sectorNameCell);
            sectorTotalTable.addCell(totalBmQty);
            sectorTotalTable.addCell(totalCmQty);
            //Last page Sector wise total end.
            pdfPTable.addCell(totalCell);
            pdfPTable.addCell(totalBmQty);
            pdfPTable.addCell(totalCmQty);
            document.add(pdfPTable);
            document.newPage();
        }
        //Last page Sector wise total begin
        PdfPCell totalTailCell = new PdfPCell(new Paragraph("Total"));
        PdfPCell totalBmOrderSectorCell = new PdfPCell(new Paragraph(totalBmOrderFromList+""));
        PdfPCell totalCmOrderSectorCell = new PdfPCell(new Paragraph(totalCmOrderFromList+""));
        totalTailCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalBmOrderSectorCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalCmOrderSectorCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        sectorTotalTable.addCell(totalTailCell);
        sectorTotalTable.addCell(totalBmOrderSectorCell);
        sectorTotalTable.addCell(totalCmOrderSectorCell);
        document.add(sectorTotalTable);
        //Last page Sector wise total end.
    }

    private PdfPTable getPdfPTable(String sector) throws DocumentException {
        PdfPTable pdfPTable = new PdfPTable(6);
        pdfPTable.setWidthPercentage(100);
        float[] columnWidths = {11, 25, 25, 19, 10, 10};
        pdfPTable.setWidths(columnWidths);
        PdfPCell sectorCell = new PdfPCell(new Paragraph(sector));
        sectorCell.setColspan(6);
        PdfPCell headerGivenSerialNumberCell = new PdfPCell(new Paragraph("Serial #"));
        PdfPCell headerNameCell = new PdfPCell(new Paragraph("Name"));
        PdfPCell headerAddress1Cell = new PdfPCell(new Paragraph("Address"));
        PdfPCell headerPhoneCell = new PdfPCell(new Paragraph("Phone"));
        PdfPCell headerBmOrder = new PdfPCell(new Paragraph("BM"));
        PdfPCell headerCmOrder = new PdfPCell(new Paragraph("CM"));
        sectorCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerGivenSerialNumberCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerNameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerAddress1Cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerPhoneCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerBmOrder.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCmOrder.setBackgroundColor(BaseColor.LIGHT_GRAY);
        pdfPTable.addCell(sectorCell);
        pdfPTable.addCell(headerGivenSerialNumberCell);
        pdfPTable.addCell(headerNameCell);
        pdfPTable.addCell(headerAddress1Cell);
        pdfPTable.addCell(headerPhoneCell);
        pdfPTable.addCell(headerBmOrder);
        pdfPTable.addCell(headerCmOrder);
        return pdfPTable;
    }
}
