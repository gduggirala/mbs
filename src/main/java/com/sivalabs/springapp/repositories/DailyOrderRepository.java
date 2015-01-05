package com.sivalabs.springapp.repositories;

import com.sivalabs.springapp.entities.DailyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: giridhad
 * Date: 12/18/14
 * Time: 8:38 PM
 */
public interface DailyOrderRepository extends JpaRepository<DailyOrder, Serializable> {
    public List<DailyOrder> findByUserId(Long userId);
    public List<DailyOrder> findByUserIdAndOrderDateBetween(Long userId, Date orderDateNotBefore, Date orderDateNotAfter);
    public DailyOrder findByUserIdAndOrderDate(Long userId, Date orderDate);
    public List<DailyOrder> findByOrderDate(Date orderDate);
    public DailyOrder findById(Long id);
    public List<DailyOrder> findByUserIdAndOrderDateGreaterThan(Long userId,Date orderDate);
}
