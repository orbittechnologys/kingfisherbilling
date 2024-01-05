package com.ot.bill.service;

import com.ot.bill.dao.StaffDao;
import com.ot.bill.model.ResponseStructure;
import com.ot.bill.model.Staff;
import com.ot.bill.util.EmailSender;
import com.ot.bill.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private EmailSender emailSender;

    public ResponseEntity<ResponseStructure<Staff>> saveStaff(Staff agent) {

        ResponseStructure<Staff> responseStructure = new ResponseStructure<>();

        if (staffDao.getStaffByEmail(agent.getEmail()) != null || staffDao.getStaffByPhone(agent.getPhone()) != null) {
            responseStructure.setMessage("Staff Already Exist ");
            return new ResponseEntity<>(responseStructure,HttpStatus.CONFLICT);
        } else {
            agent.setPassword(Encryption.encrypt(agent.getPassword()));
            agent.setDesignation("STAFF");
            emailSender.sendSimpleEmail(agent.getEmail(),
                    "Greetings \nYour Profile in King Fisher Bill Application Account Has Been Created.\nThank You.",
                    "Hello " + agent.getStaffName());
            responseStructure.setStatus(HttpStatus.CREATED.value());
            responseStructure.setMessage("Staff Saved Successfully");
            responseStructure.setData(staffDao.saveStaff(agent));
            return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
        }
    }

    public ResponseEntity<ResponseStructure<Staff>> getStaffById(String id) {
        ResponseStructure<Staff> responseStructure = new ResponseStructure<>();
        Staff agent = staffDao.getStaffById(id);
        if (agent != null) {
            agent.setPassword(Encryption.decrypt(agent.getPassword()));
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Fetched Staff Details By Id");
            responseStructure.setData(agent);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff ID " + id + ", NOT FOUND");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Staff>>> getStaffsWithPaginationAndSorting(int offset, int pageSize,
                                                                                            String field) {
        ResponseStructure<Page<Staff>> responseStructure = new ResponseStructure<>();
        Page<Staff> page = staffDao.findStaffsWithPaginationAndSorting(offset, pageSize, field);
        if (page.getSize() > 0) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("All Details of Staff's Fetched");
            responseStructure.setRecordCount(page.getSize());
            responseStructure.setData(page);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff's Data Not Present");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<String>> deleteStaffById(String id) {
        ResponseStructure<String> responseStructure = new ResponseStructure<>();
        Staff agent = staffDao.getStaffById(id);
        if (agent != null) {
            staffDao.deleteStaff(agent);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Staff Of Id " + id + " Data Deleted");
            responseStructure.setData("Staff Data Deleted Successfully");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff Id " + id + " Not Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Staff>> updateStaff(Staff agent) {
        ResponseStructure<Staff> responseStructure = new ResponseStructure<>();
        Staff agent1 = staffDao.getStaffById(agent.getId());
        if (agent1 != null) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Staff Updated Successfully");
            agent.setPassword(Encryption.encrypt(agent.getPassword()));
            responseStructure.setData(staffDao.saveStaff(agent));
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff Id " + agent.getId() + " Not Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Staff>> getStaffByPhone(String phone) {
        Staff agent = staffDao.getStaffByPhone(phone);
        ResponseStructure<Staff> responseStructure = new ResponseStructure<>();
        if (agent != null) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Fetched Staff By PhoneNumber");
            responseStructure.setData(agent);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff-PhoneNumber : " + phone + ", NOT Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> staffLogin(String email, String password) {
        ResponseStructure<Object> responseStructure = new ResponseStructure<Object>();
        Staff agent = staffDao.getStaffByEmail(email);
        if (agent != null) {
            String checkPass = Encryption.decrypt(agent.getPassword());
            if (password.equals(checkPass)) {
                if (agent.isStatus() == true) {
                    int otp = (int) (Math.random() * (9999 - 1000) + 1000);
                    agent.setOtp(otp);
                    staffDao.saveStaff(agent);
                    emailSender.sendSimpleEmail(agent.getEmail(),
                            "Enter the Otp to Validate Your Self \n The Generated Otp " + otp, "Verify Your Otp");
                    responseStructure.setStatus(HttpStatus.OK.value());
                    responseStructure.setMessage("OTP SENT");
                    responseStructure.setData(agent);
                    return new ResponseEntity<>(responseStructure, HttpStatus.OK);
                } else {
                    responseStructure.setMessage("Ask Admin To Make You Online");
                    return new ResponseEntity<>(responseStructure,HttpStatus.FORBIDDEN);
                }
            } else {
                responseStructure.setMessage("Invalid Password");
                return new ResponseEntity<>(responseStructure,HttpStatus.FORBIDDEN);
            }
        } else {
            responseStructure.setMessage("Staff-Email : " + email + ", NOT Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> changeStaffProfileStatusToOnline(String agentId) {
        Staff agent = staffDao.getStaffById(agentId);
        ResponseStructure<Object> responseStructure = new ResponseStructure<>();
        if (agent != null) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Status Changed");
            agent.setStatus(true);
            staffDao.saveStaff(agent);
            responseStructure.setData("Status Changed To Online");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff Id " + agentId + ", Not Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> changeStaffProfileStatusToOffline(String agentId) {
        Staff agent = staffDao.getStaffById(agentId);
        ResponseStructure<Object> responseStructure = new ResponseStructure<>();
        if (agent != null) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Status Changed");
            agent.setStatus(false);
            staffDao.saveStaff(agent);
            responseStructure.setData("Status Changed To Offline");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Staff Id " + agentId + ", Not Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Staff>> validateOtp(int otp) {
        Staff agent = staffDao.getUserByOtp(otp);
        ResponseStructure<Staff> responseStructure = new ResponseStructure<>();
        if (agent != null && agent.isStatus()) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Success");
            responseStructure.setData(agent);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Invalid OTP");
            return new ResponseEntity<>(responseStructure,HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> verifyEmailBeforeUpdate(String email) {
        Staff agent = staffDao.getStaffByEmail(email);
        ResponseStructure<Object> responseStructure = new ResponseStructure<>();
        if (agent != null) {
            String uuid = UUID.randomUUID().toString();
            String partOfUuid = uuid.substring(0, 11);
            if (partOfUuid.contains("-")) {
                String replace = partOfUuid.replace("-", "");
                agent.setUuid(replace);
                staffDao.saveStaff(agent);
                emailSender.sendSimpleEmail(agent.getEmail(),
                        "Enter the Unique to Validate Your Account \n The Generated Unique ID " + replace,
                        "Verify Your Unique Id Before You Change YOur Password");
            } else {
                agent.setUuid(partOfUuid);
                staffDao.saveStaff(agent);
                emailSender.sendSimpleEmail(agent.getEmail(),
                        "Enter the Unique to Validate Your Account \n The Generated Unique ID " + partOfUuid,
                        "Verify Your Unique Id Before You Change Your Password");
            }
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Verify Admin By Email-Id");
            responseStructure.setData("Uuid Send To User Email-Id Successfully");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("User-Email : " + email + ", NOT Found");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Object>> updatePasswordByUuid(String uuid, String newPassword) {
        ResponseStructure<Object> responseStructure = new ResponseStructure<>();
        Staff agent = staffDao.getUserByUuid(uuid);
        if (agent != null) {
            agent.setPassword(Encryption.encrypt(newPassword));
            staffDao.saveStaff(agent);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Password Reset");
            responseStructure.setData("Successfully Password Updated");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("User-Uuid : " + uuid + ", Not Match");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<List<Staff>>> searchQuery(String query) {
        ResponseStructure<List<Staff>> responseStructure = new ResponseStructure<>();
        List<Staff> agentList = new ArrayList<>();
        if (query.matches(".*\\d.*")) {
            Staff agent = staffDao.getStaffById(query);
            if (agent == null) {
                responseStructure.setMessage("Staff id " + query + " not found");
                return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
            }
            agentList.add(agent);
        } else {
            agentList = staffDao.getStaffByName(query);
        }
        if (agentList.size() > 0) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Fetched Agents Details By query");
            responseStructure.setData(agentList);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("No match for " + query + ", NOT FOUND");
            return new ResponseEntity<>(responseStructure,HttpStatus.NOT_FOUND);
        }
    }

}