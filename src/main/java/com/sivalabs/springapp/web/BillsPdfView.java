package com.sivalabs.springapp.web;

import ch.lambdaj.group.Group;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.reports.pojo.BillListReport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;

/**
 * User: duggirag
 * Date: 2/5/15
 * Time: 1:40 PM
 */
public class BillsPdfView extends AbstractItextPdfView {
    protected void buildPdfDocument(Map<String, Object> model, Document document,
                                    PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<BillListReport> billListReports = (List<BillListReport>) model.get("billListReports");
        Group<BillListReport> billListReportGroup = group(billListReports, by(on(BillListReport.class).getSector()));
        response.setHeader("content-disposition", "Bills List");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Pragma", "public");
        for (String sector:billListReportGroup.keySet()) {
            List<BillListReport> billListReports1 = billListReportGroup.find(sector);
            int i = 0;
            for (BillListReport billListReport : billListReports1) {
                i++;
                document.add(createBillTable(billListReport));
                document.add(new Paragraph("      " + Chunk.NEWLINE));
                if (i == 4) {
                    document.newPage();
                    document.add(new Paragraph("      " + Chunk.NEWLINE));
                    i = 0;
                }
            }
            document.newPage();
        }
    }

    private PdfPTable createBillTable(BillListReport billListReport) throws IOException, DocumentException {
        //BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = FontFactory.getFont("/fonts/arial.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, BaseColor.BLACK);
        PdfPTable billTable = new PdfPTable(5);
        Paragraph toAndPhoneParagraph = new Paragraph(new Chunk("To: "+billListReport.getName()+" ("+billListReport.getCustomerId()+") Sector:"+billListReport.getSector()+Chunk.NEWLINE+"Phone: "+billListReport.getPhone(), font));
        PdfPCell toCell = new PdfPCell(toAndPhoneParagraph);
        toCell.setColspan(4);
        toCell.setRowspan(2);
        billTable.addCell(toCell);

        PdfPCell billNumberAndDate = new PdfPCell(new Paragraph(new Chunk("Bill # : "+billListReport.getId()+Chunk.NEWLINE+"Date:"+ DateUtils.getFormattedDateForReport(billListReport.getGenerationDate())+"", font)));
        billNumberAndDate.setRowspan(2);
        billTable.addCell(billNumberAndDate);

      /*  Paragraph phoneParagraph = new Paragraph("Phone: "+billListReport.getPhone());
        PdfPCell phoneCell = new PdfPCell(phoneParagraph);
        phoneCell.setColspan(4);
        billTable.addCell(phoneCell);*/

        PdfPCell milkTypeHeaderCell = new PdfPCell(new Paragraph(new Chunk("Milk Type",font)));
        billTable.addCell(milkTypeHeaderCell);

        PdfPCell periodHeaderCell = new PdfPCell(new Paragraph(new Chunk("Period", font)));
        billTable.addCell(periodHeaderCell);

        PdfPCell quantityHeaderCell = new PdfPCell(new Paragraph(new Chunk("Quantity", font)));
        billTable.addCell(quantityHeaderCell);

        PdfPCell rateHeaderCell = new PdfPCell(new Paragraph(new Chunk("Rate", font)));
        billTable.addCell(rateHeaderCell);

        PdfPCell totalHeaderCell = new PdfPCell(new Paragraph(new Chunk("Total")));
        billTable.addCell(totalHeaderCell);

        Paragraph periodParagraph = new Paragraph("From:"+DateUtils.getFormattedDateForReport(billListReport.getFromDate())+
                Chunk.NEWLINE+"To: "+DateUtils.getFormattedDateForReport(billListReport.getToDate()));

        //Started BM Calculations
        PdfPCell bmCell = new PdfPCell(new Paragraph("Buffalo Milk"));
        billTable.addCell(bmCell);

        PdfPCell bmPeriodCell = new PdfPCell(periodParagraph);
        billTable.addCell(bmPeriodCell);

        PdfPCell bmQuantityCell = new PdfPCell(new Paragraph(billListReport.getTotalBmQty()+""));
        billTable.addCell(bmQuantityCell);

        PdfPCell bmRateCell = new PdfPCell(new Paragraph( new Chunk(" \u20B9 "+billListReport.getBmPerQuantityPrice(), font)));
        billTable.addCell(bmRateCell);

        PdfPCell bmTotalPrice = new PdfPCell(new Paragraph( new Chunk(" \u20B9 "+billListReport.getTotalBmPrice(), font)));
        billTable.addCell(bmTotalPrice);

        //Started CM Calculations
        PdfPCell cmCell = new PdfPCell(new Paragraph("Cow Milk"));
        billTable.addCell(cmCell);

        PdfPCell cmPeriodCell = new PdfPCell(periodParagraph);
        billTable.addCell(cmPeriodCell);

        PdfPCell cmQuantityCell = new PdfPCell(new Paragraph(billListReport.getTotalCmQty()+""));
        billTable.addCell(cmQuantityCell);

        Chunk cmRate = new Chunk(" \u20B9 "+billListReport.getCmPerQuantityPrice(), font);
        PdfPCell cmRateCell = new PdfPCell(new Paragraph(cmRate));
        billTable.addCell(cmRateCell);

        Chunk cmTotalPrice = new Chunk(" \u20B9 "+billListReport.getTotalCmPrice(), font);
        PdfPCell cmTotalPriceCell = new PdfPCell(new Paragraph(cmTotalPrice));
        billTable.addCell(cmTotalPriceCell);

        //Other Charges
        PdfPCell otherChargesCell = new PdfPCell(new Paragraph("Other Charges"));
        otherChargesCell.setColspan(4);
        billTable.addCell(otherChargesCell);

        PdfPCell otherChargesValueCell = new PdfPCell(new Paragraph( new Chunk(" \u20B9 "+billListReport.getOtherCharges(), font)));
        billTable.addCell(otherChargesValueCell);

        //Discount
        PdfPCell discountCell = new PdfPCell(new Paragraph("Discount"));
        discountCell.setColspan(4);
        billTable.addCell(discountCell);

        PdfPCell disCountValueCell = new PdfPCell(new Paragraph( new Chunk(" \u20B9 "+billListReport.getDiscount(), font)));
        billTable.addCell(disCountValueCell);

        //previous Months Balance
        PdfPCell previousBalanceCell = new PdfPCell(new Paragraph("Previous Balance"));
        previousBalanceCell.setColspan(4);
        billTable.addCell(previousBalanceCell);

        PdfPCell previousBalanceValueCell = new PdfPCell(new Paragraph( new Chunk(" \u20B9 "+billListReport.getPreviousMonthsBalanceAmount(), font)));
        billTable.addCell(previousBalanceValueCell);


        //Grand Total
        PdfPCell grandTotalCell = new PdfPCell(new Paragraph("Grand Total"));
        grandTotalCell.setColspan(4);
        billTable.addCell(grandTotalCell);

        PdfPCell grandTotalValueCell = new PdfPCell(new Paragraph( new Chunk(" \u20B9 "+billListReport.getPayableAmount(), font)));
        billTable.addCell(grandTotalValueCell);

        return billTable;
    }
}
