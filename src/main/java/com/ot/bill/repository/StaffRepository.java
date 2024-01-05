package com.ot.bill.repository;

import com.ot.bill.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {
    public Optional<Staff>  findById(String id);

    public Optional<Staff> findByEmail(String email);

    public Optional<Staff> findByPhone(String phone);

    public Optional<Staff> findByOtp(int otp);

    public Optional<Staff> findByUuid(String uuid);

    public List<Staff> findByStaffNameContaining(String agentName);
}
