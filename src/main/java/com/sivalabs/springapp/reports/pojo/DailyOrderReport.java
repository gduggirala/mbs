package com.sivalabs.springapp.reports.pojo;

import java.util.Date;

/**
 * User: gduggirala
 * Date: 4/1/15
 * Time: 12:32 PM
 */
public class DailyOrderReport {
    private Double totalCmOrder;
    private Double totalBmOrder;
    private Double bmRevenue;
    private Double cmRevenue;
    private String sector;
    private Date orderDate;
    private Double totalRevenue;
    private String orderMonth;

    public Double getTotalCmOrder() {
        return totalCmOrder;
    }

    public void setTotalCmOrder(Double totalCmOrder) {
        this.totalCmOrder = totalCmOrder;
    }

    public Double getTotalBmOrder() {
        return totalBmOrder;
    }

    public void setTotalBmOrder(Double totalBmOrder) {
        this.totalBmOrder = totalBmOrder;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Double getBmRevenue() {
        return bmRevenue;
    }

    public void setBmRevenue(Double bmRevenue) {
        this.bmRevenue = bmRevenue;
    }

    public Double getCmRevenue() {
        return cmRevenue;
    }

    public void setCmRevenue(Double cmRevenue) {
        this.cmRevenue = cmRevenue;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderMonth() {
        return orderMonth;
    }

    public void setOrderMonth(String orderMonth) {
        this.orderMonth = orderMonth;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
