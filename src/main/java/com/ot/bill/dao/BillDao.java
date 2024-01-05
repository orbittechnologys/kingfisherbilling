package com.ot.bill.dao;

import com.ot.bill.model.Bill;
import com.ot.bill.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BillDao {

    @Autowired
    private BillRepository billRepository;

    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    public Bill findByBillId(String id) {
        Optional<Bill> optional = billRepository.findById(id);
        return optional.orElse(null);
    }

    public List<Bill> findBylocalDateBetween(LocalDate startDate, LocalDate endDate) {
        return billRepository.findBylocalDateBetween(startDate, endDate);
    }

    public Page<Bill> findBillsWithPaginationAndSorting(int offset, int pageSize, String field) {
        Page<Bill> bills = billRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
        return bills;
    }

    public List<Bill> findByCustomerPhone(String phone) {
        return billRepository.findByCustomerPhone(phone);
    }

}