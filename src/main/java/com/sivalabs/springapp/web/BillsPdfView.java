package com.sivalabs.springapp.web;

import ch.lambdaj.group.Group;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.Bill;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        List<Bill> billListReports = (List<Bill>) model.get("billListReports");
        Group<Bill> billListReportGroup = group(billListReports, by(on(Bill.class).getSector()));
        response.setHeader("content-disposition", "Bills List");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Pragma", "public");
        for (String sector:billListReportGroup.keySet()) {
            List<Bill> billListReports1 = billListReportGroup.find(sector);
            int i = 0;
            for (Bill billListReport : billListReports1) {
                i++;
                document.add(createBillTable(billListReport));
                document.add(new Paragraph("      " + Chunk.NEWLINE));
                if (i == 2) {
                    document.newPage();
                    document.add(new Paragraph("      " + Chunk.NEWLINE));
                    i = 0;
                }
            }
            document.newPage();
        }
    }

    private PdfPTable createBillTable(Bill billListReport) throws IOException, DocumentException {
        Font font = FontFactory.getFont("/fonts/arial.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, BaseColor.BLACK);
        Font logoFont = FontFactory.getFont("/fonts/arial.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, BaseColor.BLACK);
        Image logoImage = Image.getInstance("http://localhost:8080/cmb/resources/img/finallogo.jpg");
        logoImage.scaleToFit(120f,300f);
        logoImage.setAlignment(Element.ALIGN_CENTER);
        PdfPTable billTable = new PdfPTable(5);
        Paragraph companyLogoAndNameParagraph = new Paragraph(
                new Chunk("Ph: +917893669493, www.djandcdairy.com, www.facebook.com/djandcdairy", logoFont)
        );
        PdfPCell logoImageCell = new PdfPCell();
        logoImageCell.setColspan(5);
        logoImageCell.setRowspan(4);
        logoImageCell.addElement(logoImage);
        logoImageCell.addElement(companyLogoAndNameParagraph);
        logoImageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //logoImageCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        billTable.addCell(logoImageCell);

        Paragraph toAndPhoneParagraph = new Paragraph(
                new Chunk("To: "+billListReport.getCustomerName()+" ("+billListReport.getCustomerId()+") Sector:"+billListReport.getSector()+
                Chunk.NEWLINE+"Phone: "+((billListReport.getPhone()!= null && !billListReport.getPhone().equalsIgnoreCase("Change me"))?billListReport.getPhone():"__________"), font));
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
        milkTypeHeaderCell.setRowspan(2);
        billTable.addCell(milkTypeHeaderCell);

        PdfPCell periodHeaderCell = new PdfPCell(new Paragraph(new Chunk("Period", font)));
        periodHeaderCell.setRowspan(2);
        billTable.addCell(periodHeaderCell);

        PdfPCell quantityHeaderCell = new PdfPCell(new Paragraph(new Chunk("Quantity", font)));
        quantityHeaderCell.setRowspan(2);
        billTable.addCell(quantityHeaderCell);

        PdfPCell rateHeaderCell = new PdfPCell(new Paragraph(new Chunk("Rate", font)));
        rateHeaderCell.setRowspan(2);
        billTable.addCell(rateHeaderCell);

        PdfPCell totalHeaderCell = new PdfPCell(new Paragraph(new Chunk("Total")));
        totalHeaderCell.setRowspan(2);
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
        PdfPCell otherChargesCell = new PdfPCell();
        otherChargesCell.addElement(new Paragraph("Other Charges"));
        otherChargesCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        otherChargesCell.setColspan(4);
   //     otherChargesCell.setRowspan(3);
        billTable.addCell(otherChargesCell);

        PdfPCell otherChargesValueCell = new PdfPCell();
        otherChargesValueCell.addElement(new Paragraph( new Chunk(" \u20B9 "+billListReport.getOtherCharges(), font)));
        otherChargesValueCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
   //     otherChargesValueCell.setRowspan(3);
        billTable.addCell(otherChargesValueCell);

        //Discount
        PdfPCell discountCell = new PdfPCell(); //Discount
        discountCell.addElement(new Paragraph("Adjustments"));
        discountCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        discountCell.setColspan(4);
     //   discountCell.setRowspan(2);
        billTable.addCell(discountCell);

        PdfPCell disCountValueCell = new PdfPCell();
        disCountValueCell.addElement(new Paragraph( new Chunk(" \u20B9 "+billListReport.getDiscount(), font)));
        disCountValueCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
      //  disCountValueCell.setRowspan(2);
        billTable.addCell(disCountValueCell);

        //previous Months Balance
        PdfPCell previousBalanceCell = new PdfPCell();
        previousBalanceCell.addElement(new Paragraph("Previous Balance"));
        previousBalanceCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        previousBalanceCell.setColspan(4);
      //  previousBalanceCell.setRowspan(2);
        billTable.addCell(previousBalanceCell);

        PdfPCell previousBalanceValueCell = new PdfPCell();
        previousBalanceValueCell.addElement(new Paragraph( new Chunk(" \u20B9 "+billListReport.getPreviousMonthsBalanceAmount(), font)));
        previousBalanceValueCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        previousBalanceValueCell.setColspan(4);
     //   previousBalanceValueCell.setRowspan(2);
        billTable.addCell(previousBalanceValueCell);


        //Grand Total
        PdfPCell grandTotalCell = new PdfPCell();
        grandTotalCell.addElement(new Paragraph("Grand Total"));
        grandTotalCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        grandTotalCell.setColspan(4);
      //  grandTotalCell.setRowspan(2);
        billTable.addCell(grandTotalCell);

        PdfPCell grandTotalValueCell = new PdfPCell();
        grandTotalValueCell.addElement(new Paragraph( new Chunk(" \u20B9 "+billListReport.getBalanceAmount(), font)));
        grandTotalValueCell.addElement(new Paragraph("      " + Chunk.NEWLINE));
        grandTotalValueCell.setRowspan(2);
     //   grandTotalValueCell.setRowspan(2);
        billTable.addCell(grandTotalValueCell);

        return billTable;
    }
}
