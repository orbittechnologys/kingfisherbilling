package com.ot.bill.controller;

import com.ot.bill.model.Admin;
import com.ot.bill.model.ResponseStructure;
import com.ot.bill.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {


    @Autowired
    private AdminService adminService;

    @Operation(summary = "Save Admin", description = "Input is Object and return Admin object")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "201", description = "Admin Already Exist")})
    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveAdmin(@RequestBody @Validated Admin admin) {
        return adminService.saveAdmin(admin);
    }

    @Operation(summary = "Fetch Admin by id", description = "Input Is Id Of The Admin Object and return Admin Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully found"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/id/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findAdminById(@PathVariable String id) {
        return adminService.getAdminById(id);
    }

    @Operation(summary = "Delete Admin Object", description = "Input Is Id Of The Admin Object And Output Is String")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully Deleted"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @DeleteMapping(value = "/deleteadmin/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteAdminById(@PathVariable String id) {
        return adminService.deleteAdminById(id);
    }

    @Operation(summary = "Update Admin Object", description = "Input Is Admin Object And Return Updated Admin Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PutMapping(value = "/updateadmin", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateAdmin(@RequestBody @Validated Admin admin) {
        return adminService.updateAdmin(admin);
    }

    @Operation(summary = "Fetch Admin By Name", description = "Input Is Name Of The Admin Object And Return Admin Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/getAdminByNameOrId/{query}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Object>> findAdminByName(@PathVariable String query) {
        return adminService.getAdminByNameOrId(query);
    }

    @Operation(summary = "Validate Admin By Email", description = "Inputs are Admin email id and password and return Admin object")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/adminLogin/{email}/{password}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ResponseStructure<Object>> validateAdminByEmail(@PathVariable String email,
                                                                          @PathVariable String password) {
        return adminService.adminLogin(email, password);
    }

    @GetMapping(value = "/otp", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ResponseStructure<Admin>> validateOtp(@RequestParam int otp) {
        return adminService.validateOtp(otp);
    }

    @Operation(summary = "Fetch All Admins With Pagination And Sort", description = "Return The List Of Admins With Pagination And Sort")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Fetched All The Admins Object")})
    @GetMapping(value = "/getAllAdmins/{offset}/{pageSize}/{field}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAdminsWithPaginationAndSort(@PathVariable int offset,
                                                                 @PathVariable int pageSize, @PathVariable String field) {
        return adminService.getAdminsWithPaginationAndSorting(offset, pageSize, field);
    }
}