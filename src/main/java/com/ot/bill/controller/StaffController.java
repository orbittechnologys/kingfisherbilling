package com.ot.bill.controller;

import com.ot.bill.model.Admin;
import com.ot.bill.model.ResponseStructure;
import com.ot.bill.model.Staff;
import com.ot.bill.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Operation(summary = "Save Staff", description = "Input is Object and return Staff object")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "201", description = "Admin Already Exist")})
    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Staff>> saveSatff(@RequestBody @Validated Staff admin) {
        return staffService.saveStaff(admin);
    }

    @Operation(summary = "Fetch Staff by id", description = "Input Is Id Of The Staff Object and return Staff Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully found"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/id/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Staff>> getStaffById(@PathVariable String id) {
        return staffService.getStaffById(id);
    }
    @Operation(summary = "Fetch All Staffs With Pagination And Sort", description = "Return The List Of Staffs With Pagination And Sort")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Fetched All The Admins Object")})
    @GetMapping(value = "/getAllStaffs/{offset}/{pageSize}/{field}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Page<Staff>>> getStaffsWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        return staffService.getStaffsWithPaginationAndSorting(offset, pageSize, field);
    }

    @Operation(summary = "Delete Staff Object", description = "Input Is Id Of The Staff Object And Output Is String")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully Deleted"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @DeleteMapping(value = "/deleteStaff/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<String>> deleteAdminById(@PathVariable String id) {
        return staffService.deleteStaffById(id);
    }

    @Operation(summary = "Update Staff Object", description = "Input Is Staff Object And Return Updated Staff Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PutMapping(value = "/updateStaff", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Staff>> updateAdmin(@RequestBody @Validated Staff admin) {
        return staffService.updateStaff(admin);
    }

    @Operation(summary = "Fetch Staff By Name", description = "Input Is Name Of The Staff Object And Return Staff Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/getStaffByNameOrId/{query}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<List<Staff>>> findStaffByNameOrId(@PathVariable String query) {
        return staffService.searchQuery(query);
    }

    @Operation(summary = "Validate Staff By Email", description = "Inputs are Staff email id and password and return Staff object")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/staffLogin/{email}/{password}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ResponseStructure<Object>> validateStaffByEmail(@PathVariable String email,
                                                                          @PathVariable String password) {
        return staffService.staffLogin(email, password);
    }

    @GetMapping(value = "/otp", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ResponseStructure<Staff>> validateOtp(@RequestParam int otp) {
        return staffService.validateOtp(otp);
    }

    @PatchMapping(value = "/offline/{staffId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseStructure<Object>> changeAgentProfileStatusToOffline(@PathVariable String staffId) {
        return staffService.changeStaffProfileStatusToOffline(staffId);
    }

    @PatchMapping(value = "/online/{staffId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseStructure<Object>> changeAgentProfileStatusToOnline(@PathVariable String staffId) {
        return staffService.changeStaffProfileStatusToOnline(staffId);
    }

    @GetMapping(value = "/findyStaffByPhoneNumber/{phone}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseStructure<Staff>> findStaffByPhoneNumber(@PathVariable String phone) {
        return staffService.getStaffByPhone(phone);
    }

}