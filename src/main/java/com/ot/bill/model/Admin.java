package com.ot.bill.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GenericGenerator(name = "sequence_admin_id", strategy = "com.ot.bill.model.AdminIdGenerator")
    @GeneratedValue(generator = "sequence_admin_id")
    private String id;

    @NotBlank(message = "Please Enter The Admin-Name")
    private String adminName;

    @Column(unique = true)
    @NotBlank(message = "Please Enter The Admin-Email")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Enter a Valid User-Email")
    private String email;

    @Column(unique = true)
    @NotBlank(message = "Please Enter The Admin-PhoneNumber")
    @Pattern(regexp = "^[6-9]{1}[0-9]{9}+$", message = "Enter Proper User-PhoneNumber")
    private String phone;

    @NotBlank(message = "Please Enter the Admin-Password")
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!]).{6,15})", message = "Enter Proper Admin-Password "
            + "\n" + " The User-Password Should Have Atleast " + "\n" + " 1 Upper Case " + "\n" + " 1 Lower Case "
            + "\n" + " 1 Special Character " + "\n" + " And 1 Numric Character " + "\n"
            + " The Length OF The Password Must Be Minimum OF 6 Character And Maximum OF 15 Character ")
    private String password;

    @NotBlank(message = "Please Enter the Admin-Address")
    private String address;

    private String adminProfilePic;

    @JsonIgnore
    private int otp;

    @JsonIgnore
    private String uuid;

}