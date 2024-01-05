package com.ot.bill.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GenericGenerator(name = "sequence_bill_id", strategy = "com.ot.bill.model.BillIdGenerator")
    @GeneratedValue(generator = "sequence_bill_id")
    private String id;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private double totalCustomer;

    private double activity;

    private double extraAmenity;

    private double foodCost;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private String gstNumber;

    @JsonGetter
    public String getGstNumber() {
        return gstNumber;
    }

    @JsonIgnore
    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    private String invoiceNumber;

    private double totalCost;

    private LocalDate localDate;

    @UpdateTimestamp
    private LocalDateTime localDateTime;

    private String modeOfPayment;

}