package com.sivalabs.springapp.reports.pojo;

import java.util.Date;

/**
 * User: duggirag
 * Date: 1/8/15
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DailyOrderGround {
    private Double cmOrder;
    private Double bmOrder;
    private String name;
    private String sector;
    private Date orderDate;
    private String phone;
    private String givenSerialNumber;
    private String address1;

    public Double getCmOrder() {
        return cmOrder;
    }

    public void setCmOrder(Double cmOrder) {
        this.cmOrder = cmOrder;
    }

    public Double getBmOrder() {
        return bmOrder;
    }

    public void setBmOrder(Double bmOrder) {
        this.bmOrder = bmOrder;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGivenSerialNumber() {
        return givenSerialNumber;
    }

    public void setGivenSerialNumber(String givenSerialNumber) {
        this.givenSerialNumber = givenSerialNumber;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }
}
