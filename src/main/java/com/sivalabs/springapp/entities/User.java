/**
 *
 */
package com.sivalabs.springapp.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author katsi02
 */
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = true)
    private String password;
    @Column(nullable = false)
    private String sector;
    @Column(nullable = false)
    private String phone;
    @Column
    private String address1;
    @Column
    private String address2;
    @Column
    private String address3;
    @Column
    private String city;
    @Column
    private String zip;
    @Column
    private Double dailyCmOrder;
    @Column
    private Double dailyBmOrder;
    @Column
    private Double cmPrice;
    @Column
    private Double bmPrice;
    @Column
    private Boolean isActive;
    @Column
    private Integer givenSerialNumber;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date orderStartDate;

    @OneToMany(mappedBy = "user", targetEntity = Role.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<DailyOrder> dailyOrders = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Bill> bills = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Double getDailyCmOrder() {
        return dailyCmOrder;
    }

    public void setDailyCmOrder(Double dailyCmOrder) {
        this.dailyCmOrder = dailyCmOrder;
    }

    public Double getDailyBmOrder() {
        return dailyBmOrder;
    }

    public void setDailyBmOrder(Double dailyBmOrder) {
        this.dailyBmOrder = dailyBmOrder;
    }

    public Double getCmPrice() {
        return cmPrice;
    }

    public void setCmPrice(Double cmPrice) {
        this.cmPrice = cmPrice;
    }

    public Double getBmPrice() {
        return bmPrice;
    }

    public void setBmPrice(Double bmPrice) {
        this.bmPrice = bmPrice;
    }

    public Set<DailyOrder> getDailyOrders() {
        return dailyOrders;
    }

    public void setDailyOrders(Set<DailyOrder> dailyOrders) {
        this.dailyOrders = dailyOrders;
    }

    public Set<Bill> getBills() {
        return bills;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Date getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(Date orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public Integer getGivenSerialNumber() {
        return givenSerialNumber;
    }

    public void setGivenSerialNumber(Integer givenSerialNumber) {
        this.givenSerialNumber = givenSerialNumber;
    }
}
