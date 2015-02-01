package com.sivalabs.springapp.reports.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sivalabs.springapp.web.controllers.CustomDateSerializer;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;

/**
 * User: duggirag
 * Date: 1/31/15
 * Time: 9:04 PM
 */
public class BillListReport {
    private Long id;
    private Date fromDate;
    private Date toDate;
    private Date generationDate;
    private Double totalCmQty;
    private Double totalBmQty;
    private Double totalCmPrice;
    private Double totalBmPrice;
    private Double discount;
    private Double otherCharges;
    private Double totalAmount; //Should be totalBmPrice + totalCmPrice + otherCharges
    private String discountReason;
    private Double paidAmount;
    private Double balanceAmount; //Should be totalAmount - paidAmount
    private Double previousMonthsBalanceAmount;
    private boolean isClosed; //True if the bill is closed after paying, should be done by admin;
    private Double bmPerQuantityPrice;
    private Double cmPerQuantityPrice;
    private String comment;
    private String month;
    private Double billableAmount;
    private Double payableAmount;
    private String name;
    private String sector;
    private String address1;
    private Integer givenSerialNumber;
    private String phone;
    private Double dailybmOrder;
    private Double dailyCmOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSerialize(using = CustomDateSerializer.class, as=Date.class)
    public Date getFromDate() {
        return fromDate;
    }


    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class, as=Date.class)
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class, as=Date.class)
    public Date getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }

    public Double getTotalCmQty() {
        return totalCmQty;
    }

    public void setTotalCmQty(Double totalCmQty) {
        this.totalCmQty = totalCmQty;
    }

    public Double getTotalBmQty() {
        return totalBmQty;
    }

    public void setTotalBmQty(Double totalBmQty) {
        this.totalBmQty = totalBmQty;
    }

    public Double getTotalCmPrice() {
        return totalCmPrice;
    }

    public void setTotalCmPrice(Double totalCmPrice) {
        this.totalCmPrice = totalCmPrice;
    }

    public Double getTotalBmPrice() {
        return totalBmPrice;
    }

    public void setTotalBmPrice(Double totalBmPrice) {
        this.totalBmPrice = totalBmPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(Double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public Double getPreviousMonthsBalanceAmount() {
        return previousMonthsBalanceAmount;
    }

    public void setPreviousMonthsBalanceAmount(Double previousMonthsBalanceAmount) {
        this.previousMonthsBalanceAmount = previousMonthsBalanceAmount;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Double getBmPerQuantityPrice() {
        return bmPerQuantityPrice;
    }

    public void setBmPerQuantityPrice(Double bmPerQuantityPrice) {
        this.bmPerQuantityPrice = bmPerQuantityPrice;
    }

    public Double getCmPerQuantityPrice() {
        return cmPerQuantityPrice;
    }

    public void setCmPerQuantityPrice(Double cmPerQuantityPrice) {
        this.cmPerQuantityPrice = cmPerQuantityPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setBillableAmount(Double billableAmount) {
        this.billableAmount = billableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Double getBillableAmount() {
        //Billable amount
        return (totalCmPrice==null?0:totalCmPrice) + (totalBmPrice==null?0:totalBmPrice) + (otherCharges==null?0:otherCharges);
    }

    public Double getPayableAmount() {
        return ((totalCmPrice==null?0:totalCmPrice) + (totalBmPrice==null?0:totalBmPrice) + (otherCharges==null?0:otherCharges)) -
                ((discount==null?0:discount) + (previousMonthsBalanceAmount==null?0:previousMonthsBalanceAmount) + (paidAmount==null?0:paidAmount));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public Integer getGivenSerialNumber() {
        return givenSerialNumber;
    }

    public void setGivenSerialNumber(Integer givenSerialNumber) {
        this.givenSerialNumber = givenSerialNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getDailybmOrder() {
        return dailybmOrder;
    }

    public void setDailybmOrder(Double dailybmOrder) {
        this.dailybmOrder = dailybmOrder;
    }

    public Double getDailyCmOrder() {
        return dailyCmOrder;
    }

    public void setDailyCmOrder(Double dailyCmOrder) {
        this.dailyCmOrder = dailyCmOrder;
    }
}
