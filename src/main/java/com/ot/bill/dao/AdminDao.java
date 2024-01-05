package com.ot.bill.dao;

import com.ot.bill.model.Admin;
import com.ot.bill.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminDao {

    @Autowired
    private AdminRepository adminRepository;

    public Admin saveAdmin(Admin agent) {
        return adminRepository.save(agent);
    }

    public Admin getAdminById(String id) {
        Optional<Admin> agent = adminRepository.findById(id);
        return agent.orElse(null);
    }

    public Page<Admin> findAdminWithPaginationAndSorting(int offset, int pageSize, String field) {
        Page<Admin> admins = adminRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
        return admins;
    }

    public void deleteAdmin(Admin admin) {
        adminRepository.delete(admin);
    }

    public Admin getAdminByEmail(String email) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        return admin.orElse(null);
    }

    public Admin getAdminByPhone(String phone) {
        Optional<Admin> admin = adminRepository.findByPhone(phone);
        return admin.orElse(null);
    }

    public List<Admin> getAdminByName(String adminName) {
        Optional<List<Admin>> agents = adminRepository.findByAdminNameContaining(adminName);
        return agents.orElse(null);
    }

    public Admin getAdminByOtp(int otp) {
        Optional<Admin> agent = adminRepository.findByOtp(otp);
        return agent.orElse(null);
    }

    public Admin getAdminByUuid(String uuid) {
        Optional<Admin> agent = adminRepository.findByUuid(uuid);
        return agent.orElse(null);
    }
}