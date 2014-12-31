package com.sivalabs.springapp.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
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
    private boolean isClosed; //True if the bill is closed after paying, should be done by admin;

    @Column
    private Double bmPerQuantityPrice;

    @Column
    private Double cmPerQuantityPrice;

    @Column
    private String comment;

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
}
