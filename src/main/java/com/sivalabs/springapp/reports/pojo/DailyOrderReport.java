package com.sivalabs.springapp.reports.pojo;

/**
 * Created with IntelliJ IDEA.
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
}
