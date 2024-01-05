package com.ot.bill.repository;

import com.ot.bill.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill,String> {

    public List<Bill> findBylocalDateBetween(LocalDate startDate, LocalDate endDate);

    public List<Bill> findByCustomerPhone(String phone);

}