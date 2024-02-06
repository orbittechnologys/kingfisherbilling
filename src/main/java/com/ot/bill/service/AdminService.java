package com.ot.bill.service;

import com.ot.bill.dao.AdminDao;
import com.ot.bill.model.Admin;
import com.ot.bill.model.ResponseStructure;
import com.ot.bill.util.EmailSender;
import com.ot.bill.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private EmailSender emailSender;

    public ResponseEntity<Object> saveAdmin(Admin admin) {
        Admin existingByEmail = adminDao.getAdminByEmail(admin.getEmail());
        Admin existingByPhone = adminDao.getAdminByPhone(admin.getPhone());
        if (existingByEmail != null || existingByPhone != null) {
            Object obj = "Admin Already Existing With this Email Id And Password "+ existingByEmail != null ? existingByEmail : existingByPhone;
            return ResponseEntity.status(HttpStatus.CONFLICT).body(obj);
        } else {
            admin.setPassword(Encryption.encrypt(admin.getPassword()));
            emailSender.sendSimpleEmail(admin.getEmail(),
                    "Greetings \nYour Profile in King Fisher Bill Application Account Has Been Created.\nThank You.",
                    "Hello " + admin.getAdminName());
            Admin admin1 = adminDao.saveAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(admin1);
        }
    }


    public ResponseEntity<Object> getAdminById(String id) {
        Admin admin = adminDao.getAdminById(id);
        if (admin != null) {
            admin.setPassword(Encryption.decrypt(admin.getPassword()));
            return ResponseEntity.ok(admin);
        } else {
            Object obj = "Admin Id Not Found "+ id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
        }
    }

    public ResponseEntity<Object> getAdminsWithPaginationAndSorting(int offset, int pageSize, String field) {
        ResponseStructure<Page<Admin>> responseStructure = new ResponseStructure<>();
        Page<Admin> page = adminDao.findAdminWithPaginationAndSorting(offset, pageSize, field);
        if (page.getSize() > 0) {
            return ResponseEntity.ok(page);
        } else {
            Object obj = "Admin Data Not Present";
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(obj);
        }
    }

    public ResponseEntity<String> deleteAdminById(String id) {
        Admin admin = adminDao.getAdminById(id);
        if (admin != null) {
            adminDao.deleteAdmin(admin);
            return ResponseEntity.ok("Admin Is Deleted Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin Id Not Found");
        }
    }

    public ResponseEntity<Object> updateAdmin(Admin admin) {
        Admin admin1 = adminDao.getAdminById(admin.getId());
        if (admin1 != null) {
            admin.setPassword(Encryption.encrypt(admin.getPassword()));
            Object object = adminDao.saveAdmin(admin);
            return ResponseEntity.ok(object);
        } else {
            Object object = "Admin Id Not Found "+ admin.getId();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(object);
        }
    }

    public ResponseEntity<List<Object>> getAdminByNameOrId(String query) {
        List<Admin> adminList = new ArrayList<>();
        if (query.matches(".*\\d.*")) {
            Admin admin = adminDao.getAdminById(query);
            if (admin == null) {
                Object object = "Admin Id Not Found "+admin.getId();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(object));
            }
            adminList.add(admin);
        } else {
            adminList = adminDao.getAdminByName(query);
        }
        if (adminList.size() > 0) {
            List<Object> list = Collections.singletonList(adminList);
            return  ResponseEntity.ok(list);
        } else {
            List<Object> list = Collections.singletonList("No Such Query Found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(list);
        }

    }

    public ResponseEntity<ResponseStructure<Object>> adminLogin(String email, String password) {
        ResponseStructure<Object> responseStructure = new ResponseStructure<Object>();
        Admin admin = adminDao.getAdminByEmail(email);
        if (admin != null) {
            String checkPass = Encryption.decrypt(admin.getPassword());
            if (password.equals(checkPass)) {
//                int otp = (int) (Math.random() * (9999 - 1000) + 1000);
//                admin.setOtp(otp);
//                adminDao.saveAdmin(admin);
                emailSender.sendSimpleEmail(admin.getEmail(),
                        "User has logged in to the Billing System - Kingfisher\n" + LocalDate.now()+" "+ LocalTime.now(), "Software Login Activity");
                responseStructure.setStatus(HttpStatus.OK.value());
                responseStructure.setMessage("Mail SENT");
                responseStructure.setData(admin);
                return new ResponseEntity<>(responseStructure, HttpStatus.OK);
            } else {
                responseStructure.setMessage("Invalid Password");
                return new ResponseEntity<>(responseStructure,HttpStatus.FORBIDDEN);
            }
        } else {
            responseStructure.setMessage("Email Not Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Admin>> validateOtp(int otp) {
        Admin admin = adminDao.getAdminByOtp(otp);
        ResponseStructure<Admin> responseStructure = new ResponseStructure<>();
        if (admin != null) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Success");
            responseStructure.setData(admin);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Invalid Otp");
            return new ResponseEntity<>(responseStructure,HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> verifyEmailBeforeUpdate(String email) {
        ResponseStructure<Object> responseStructure = new ResponseStructure<>();
        Admin admin = adminDao.getAdminByEmail(email);
        if (admin != null) {
            String uuid = UUID.randomUUID().toString();
            String partOfUuid = uuid.substring(0, 11);
            if (partOfUuid.contains("-")) {
                String replace = partOfUuid.replace("-", "");
                admin.setUuid(replace);
                adminDao.saveAdmin(admin);
                emailSender.sendSimpleEmail(admin.getEmail(),
                        "Enter the Unique to Validate Your Account \n The Generated Unique ID " + replace,
                        "Verify Your Unique Id Before You Change YOur Password");
            } else {
                admin.setUuid(partOfUuid);
                adminDao.saveAdmin(admin);
                emailSender.sendSimpleEmail(admin.getEmail(),
                        "Enter the Unique to Validate Your Account \n The Generated Unique ID " + partOfUuid,
                        "Verify Your Unique Id Before You Change Your Password");
            }
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Verify Admin By Email-Id");
            responseStructure.setData("Uuid Send To User Email-Id Successfully");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Email not Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> updatePasswordByUuid(String uuid, String newPassword) {
        ResponseStructure<Object> responseStructure = new ResponseStructure<>();
        Admin admin = adminDao.getAdminByUuid(uuid);
        if (admin != null) {
            admin.setPassword(Encryption.encrypt(newPassword));
            adminDao.saveAdmin(admin);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Password Reset");
            responseStructure.setData("Successfully Password Updated");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("UUID Not Valid");
            return new ResponseEntity<>(responseStructure,HttpStatus.FORBIDDEN);
        }
    }

}