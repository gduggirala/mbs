package com.sivalabs.springapp.repositories;

import com.sivalabs.springapp.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: giridhad
 * Date: 12/18/14
 * Time: 8:37 PM
 */
public interface BillRepository extends JpaRepository<Bill, Serializable> {
    public List<Bill> findByUserId(Long userId);

    //@Query("select b from Bill b where MONTH(?1) ")
    public Bill findByUserIdAndFromDateAndToDate(Long userId, Date fromDate, Date toDate);
    public List<Bill> findByFromDateAndToDate(Date fromDate, Date toDate);
}
