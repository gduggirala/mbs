package com.sivalabs.springapp.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: giridhad
 * Date: 12/18/14
 * Time: 8:15 PM
 */
@Entity
@Table(name="BILL", uniqueConstraints = @UniqueConstraint(columnNames = {"fromDate","toDate","USER_ID"}))
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="USER_ID")
    @JsonBackReference
    private User user;

    @Temporal(TemporalType.DATE)
    @Column
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    @Column
    private Date toDate;

    @Temporal(TemporalType.DATE)
    @Column
    private Date generationDate;

    @Column
    private Double totalCmQty;

    @Column
    private Double totalBmQty;

    @Column
    private Double totalCmPrice;

    @Column
    private Double totalBmPrice;

    @Column
    private Double discount;

    @Column
    private Double otherCharges;

    @Column
    private Double totalAmount; //Should be totalBmPrice + totalCmPrice + otherCharges

    @Column
    private String discountReason;

    @Column
    private Double paidAmount;

    @Column
    private Double balanceAmount; //Should be totalAmount - paidAmount

    @Column
    private Double previousMonthsBalanceAmount;

    @Column
    private boolean isClosed; //True if the bill is closed after paying, should be done by admin;

    @Column
    private Double bmPerQuantityPrice;

    @Column
    private Double cmPerQuantityPrice;

    @Column
    private String comment;

    @Transient
    private String month;

    @Transient
    private Double billableAmount;

    @Transient
    private Double payableAmount;

    @Transient
    private String customerName;

    @Transient
    private Long customerId;

    @Transient
    private String customerPhone;

    @Transient
    private Boolean isPaid;

    @Transient
    private Double dailybmOrder;

    @Transient
    private Double dailyCmOrder;

    @Transient
    private Integer givenSerialNumber;

    @Transient
    private String sector;

    @Transient
    private String address1;

    @Transient
    private String phone; ///

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

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

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(Double otherCharges) {
        this.otherCharges = otherCharges;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM");
        this.month = simpleDateFormat.format(this.getToDate());
        return month;
    }

    public Double getPreviousMonthsBalanceAmount() {
        return previousMonthsBalanceAmount;
    }

    public void setPreviousMonthsBalanceAmount(Double previousMonthsBalanceAmount) {
        this.previousMonthsBalanceAmount = previousMonthsBalanceAmount;
    }

    public Double getBillableAmount() {
        //Billable amount
        return (totalCmPrice==null?new Double(0):totalCmPrice) + (totalBmPrice==null?new Double(0):totalBmPrice) + (otherCharges==null?new Double(0):otherCharges);
    }

    public Double getPayableAmount() {
        /*double discountedBalanceAndPayableAmount = ((discount==null?new Double(0):discount) + (previousMonthsBalanceAmount==null?new Double(0):previousMonthsBalanceAmount) + (paidAmount==null?new Double(0):paidAmount));
        double totalCmBmAndOtherChargesAmount = ((totalCmPrice==null?new Double(0):totalCmPrice) + (totalBmPrice==null?new Double(0):totalBmPrice) + (otherCharges==null?new Double(0):otherCharges));
        if(discountedBalanceAndPayableAmount < 0){
            return totalCmBmAndOtherChargesAmount + discountedBalanceAndPayableAmount;
        }else {
            return totalCmBmAndOtherChargesAmount - discountedBalanceAndPayableAmount;
        }*/
        double discountedBalanceAndPayableAmount = ((discount == null ? new Double(0) : (0-discount)) + (previousMonthsBalanceAmount == null ? new Double(0) : previousMonthsBalanceAmount) + (paidAmount == null ? new Double(0) : paidAmount));
        double totalCmBmAndOtherChargesAmount = ((totalCmPrice == null ? new Double(0) : totalCmPrice) + (totalBmPrice == null ? new Double(0) : totalBmPrice) + (otherCharges == null ? new Double(0) : otherCharges));
        if (discountedBalanceAndPayableAmount < 0) {
            return totalCmBmAndOtherChargesAmount + discountedBalanceAndPayableAmount;
        } else {
            return totalCmBmAndOtherChargesAmount - discountedBalanceAndPayableAmount;
        }
       /* return ((totalCmPrice==null?0:totalCmPrice) + (totalBmPrice==null?0:totalBmPrice) + (otherCharges==null?0:otherCharges)) -
                ((discount==null?0:discount) + (previousMonthsBalanceAmount==null?0:previousMonthsBalanceAmount) + (paidAmount==null?0:paidAmount));*/
    }

    public String getCustomerName() {
        if(this.customerName == null) {
            return user.getName();
        }else {
            return this.customerName;
        }
    }

    public void setCustomerName(String name){
        this.customerName = name;
    }

    public Long getCustomerId() {
        if (customerId == null) {
            return user.getId();
        }else {
            return this.customerId;
        }
    }

    public String getCustomerPhone() {
        if (this.phone == null) {
            return user.getPhone();
        }else {
            return this.phone;
        }
    }

    public Boolean isPaid() {
        return !(this.getPaidAmount() <=0 && this.balanceAmount > 0);
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

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
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

    public Integer getGivenSerialNumber() {
        return givenSerialNumber;
    }

    public void setGivenSerialNumber(Integer givenSerialNumber) {
        this.givenSerialNumber = givenSerialNumber;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
