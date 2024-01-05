package com.ot.bill.dao;

import com.ot.bill.model.Staff;
import com.ot.bill.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StaffDao {

    @Autowired
    private StaffRepository staffRepository;

    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff getStaffById(String id) {
        Optional<Staff> agent = staffRepository.findById(id);
        return agent.orElse(null);
    }

    public Page<Staff> findStaffsWithPaginationAndSorting(int offset, int pageSize, String field) {
        Page<Staff> agents = staffRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
        return agents;
    }

    public void deleteStaff(Staff staff) {
        staffRepository.delete(staff);
    }

    public Staff getStaffByEmail(String email) {
        Optional<Staff> agent = staffRepository.findByEmail(email);
        return agent.orElse(null);
    }

    public Staff getStaffByPhone(String phone) {
        Optional<Staff> agent = staffRepository.findByPhone(phone);
        return agent.orElse(null);
    }

    public List<Staff> getStaffByName(String query) {
        return staffRepository.findByStaffNameContaining(query);

    }

    public Staff getUserByOtp(int otp) {
        Optional<Staff> agent = staffRepository.findByOtp(otp);
        return agent.orElse(null);
    }

    public Staff getUserByUuid(String uuid) {
        Optional<Staff> agent = staffRepository.findByUuid(uuid);
        return agent.orElse(null);
    }

}