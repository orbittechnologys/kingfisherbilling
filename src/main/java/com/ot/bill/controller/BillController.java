package com.ot.bill.controller;

import com.lowagie.text.DocumentException;
import com.ot.bill.dao.BillDao;
import com.ot.bill.model.Bill;
import com.ot.bill.model.ResponseStructure;
import com.ot.bill.model.Staff;
import com.ot.bill.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bill")
@CrossOrigin(origins = "*")
public class BillController {

    @Autowired
    private BillService billService;
    @Autowired
    private BillDao billDao;


    @Operation(summary = "Save Bill", description = "Input is Object and return Bill object")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "201", description = "Bill Already Exist")})
    @PostMapping(value = "/save/{amount}/{days}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Bill>> saveBill(@RequestBody Bill bill, @PathVariable double amount, @PathVariable double days) {
        return billService.saveBill(bill, amount, days);
    }

    @GetMapping("/generate-pdf/{id}")
    public ResponseEntity<byte[]> generateBillPdf(@PathVariable String id) {
        try {
            Bill bill = billDao.findByBillId(id);  // Create an instance of your Bill class with the necessary data

            byte[] pdfBytes = billService.generateBillPdf(bill);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", bill.getId()+"_"+LocalDate.now()+".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Fetch All Bills With Pagination And Sort", description = "Return The List Of Bills With Pagination And Sort")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Fetched All The Bills Object")})
    @GetMapping(value = "/getAllBills/{offset}/{pageSize}/{field}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Page<Bill>>> getBillsWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        return billService.getBillsWithPaginationAndSorting(offset, pageSize, field);
    }

    @Operation(summary = "Fetch Bill by id", description = "Input Is Id Of The Bill Object and return Bill Object With Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully found"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/id/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<Bill>> getBillById(@PathVariable String id) {
        return billService.findByBillId(id);
    }

    @GetMapping(value = "/findBylocalDateBetween/{startDate}/{endDate}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<List<Bill>>> findBylocalDateBetween(LocalDate startDate, LocalDate endDate) {
        return billService.findBylocalDateBetween(startDate, endDate);
    }

    @GetMapping("/csv/{startDate}/{endDate}")
    public void generateCSVReport(HttpServletResponse response, @PathVariable LocalDate startDate,
                                  @PathVariable LocalDate endDate) throws Exception {
        billService.generateBillTransactionCSV(response, startDate, endDate);
    }
    @GetMapping(value = "/phone/{phone}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseStructure<List<Bill>>> findByCustomerPhone(String phone) {
        return billService.findByCustomerPhone(phone);
    }

}