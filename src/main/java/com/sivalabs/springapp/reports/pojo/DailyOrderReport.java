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
}
