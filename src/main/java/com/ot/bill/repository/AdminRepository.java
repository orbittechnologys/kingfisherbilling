package com.ot.bill.repository;

import java.util.List;
import java.util.Optional;

import com.ot.bill.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {

    public Optional<Admin> findById(String id);

    public Optional<Admin> findByEmail(String email);

    public Optional<Admin> findByPhone(String phone);

    public Optional<List<Admin>> findByAdminNameContaining(String adminName);

    public Optional<Admin> findByOtp(int otp);

    public Optional<Admin> findByUuid(String uuid);

}