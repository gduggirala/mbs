package com.sivalabs.springapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

/**
 * User: giridhad
 * Date: 12/18/14
 * Time: 8:01 PM
 */
@Entity
@Table(name="DAILY_ORDER", uniqueConstraints = @UniqueConstraint(columnNames = {"orderDate","USER_ID"}))
//@Table(name = "DAILY_ORDER")
public class DailyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private Double cmOrder;
    @Column
    private Double bmOrder;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date orderDate;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    @JsonBackReference
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
