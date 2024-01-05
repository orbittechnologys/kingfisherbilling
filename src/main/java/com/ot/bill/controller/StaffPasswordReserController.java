package com.ot.bill.controller;

import com.ot.bill.model.ResponseStructure;
import com.ot.bill.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/password")
@CrossOrigin(origins = "*")
public class StaffPasswordReserController {

    @Autowired
    private StaffService staffService;

    @Operation(summary = "Forget Password", description = "Inputs are User-Email and return Uuid")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Fond") })
    @PostMapping(value = "/verifyEmailBeforeUpdatePassword/{email}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseStructure<Object>> validateUserByEmail(@PathVariable String email) {
        return staffService.verifyEmailBeforeUpdate(email);
    }

    @Operation(summary = "Forget Password", description = "Inputs are User-Password and return User Object")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found") })
    @PatchMapping(value = "/forget/{uuid}/{newPassword}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseStructure<Object>> updatePassword(@PathVariable String uuid,
                                                                    @PathVariable String newPassword) {
        return staffService.updatePasswordByUuid(uuid, newPassword);
    }

}