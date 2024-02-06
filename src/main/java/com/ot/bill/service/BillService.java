package com.ot.bill.service;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.lowagie.text.DocumentException;
import com.ot.bill.dao.BillDao;
import com.ot.bill.model.Bill;
import com.ot.bill.model.ResponseStructure;
import com.ot.bill.model.Staff;
import com.ot.bill.util.Encryption;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class BillService {

    @Autowired
    private BillDao billDao;

    public ResponseEntity<ResponseStructure<Bill>> saveBill(Bill bill, double amount, double days, int kids) {
        ResponseStructure<Bill> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Save Bill");
        double kidsAmount = kids * (amount / 2) * days;
        double firstAmount = bill.getActivity() + bill.getFoodCost() + bill.getExtraAmenity();
        double secondAmount = (bill.getTotalCustomer() - kids) * amount * days;
        bill.setPackageCost(secondAmount + kidsAmount);
        double finalAmount = firstAmount + secondAmount + kidsAmount;
        if (bill.getAdvancePayment() > 0) {
            finalAmount -= bill.getAdvancePayment();
        }
        bill.setTotalCost(finalAmount);
        bill.setGrandTotal(finalAmount);
        bill.setGstNumber("29AEPPD8610N1ZY");
        bill.setInvoiceNumber(generateInvoiceNumber());
        bill.setLocalDate(LocalDate.now());
        Bill b = billDao.saveBill(bill);
        responseStructure.setData(b);
        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    private String generateInvoiceNumber() {
        return "INVOICE-" + UUID.randomUUID().toString().split("-")[0].toUpperCase();
    }


    public static byte[] generateBillPdf(Bill bill) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(byteArrayOutputStream);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {
            generateUserPDF(document, bill);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void generateUserPDF(Document document, Bill bill) throws IOException {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd   " + "HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);
        Paragraph paragraph = new Paragraph("KINGFISHER RESORT DANDELI");
        Paragraph phone = new Paragraph("8 9 7 1 9 5 7 8 9 0").setTextAlignment(TextAlignment.RIGHT);
        Paragraph firstline = new Paragraph("=========================");
        Paragraph userDetails = new Paragraph();
        userDetails.add("DT TM	     :          ");
        userDetails.add(formattedDateTime + "\n");
        userDetails.add("Gst Number.   :            ");
        userDetails.add(bill.getGstNumber() + "\n");
        userDetails.add("Invoice No   :         ");
        userDetails.add(bill.getInvoiceNumber() + "\n");
        userDetails.add("Customer Name    :         ");
        userDetails.add(bill.getCustomerName() + "\n");
        userDetails.add("Customer PhoneNumber   :       ");
        userDetails.add(bill.getCustomerPhone() + "\n");
        userDetails.add("Activity :         ");
        userDetails.add(bill.getActivity() + "\n");
        userDetails.add("Food    :          ");
        userDetails.add(bill.getFoodCost() + "\n");
        userDetails.add("Payment Mode:          ");
        userDetails.add(bill.getModeOfPayment() + "\n");
        userDetails.add("Amenity:           ");
        userDetails.add(bill.getExtraAmenity() + "\n");
        userDetails.add("Total Customer:            ");
        userDetails.add(bill.getTotalCustomer() + "\n");
        userDetails.add("Check In Date:             ");
        userDetails.add(bill.getCheckInDate() + "\n");
        userDetails.add("Check Out Date:            ");
        userDetails.add(bill.getCheckOutDate() + "\n");
        userDetails.add("Total Cost:            ");
        userDetails.add(bill.getTotalCost() + "");
        userDetails.setBold();
        Paragraph endline = new Paragraph("=========================\n");
        Paragraph tq = new Paragraph("THANK YOU").setTextAlignment(TextAlignment.CENTER);
        document.add(paragraph);
        document.add(phone);
        document.add(firstline);
        document.add(userDetails);
        document.add(endline);
        document.add(tq);
        document.close();
    }

    public ResponseEntity<ResponseStructure<Page<Bill>>> getBillsWithPaginationAndSorting(int offset, int pageSize, String field) {
        ResponseStructure<Page<Bill>> responseStructure = new ResponseStructure<>();
        Page<Bill> page = billDao.findBillsWithPaginationAndSorting(offset, pageSize, field);
        if (page.getSize() > 0) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("All Details of Bills Fetched");
            responseStructure.setRecordCount(page.getSize());
            responseStructure.setData(page);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Bill's Data Not Present");
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<List<Bill>>> findBylocalDateBetween(LocalDate startDate, LocalDate endDate) {
        ResponseStructure<List<Bill>> responseStructure = new ResponseStructure<>();
        List<Bill> bills = billDao.findBylocalDateBetween(startDate, endDate);
        if (bills.size() > 0) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Fetched All Bill Between the Date");
            responseStructure.setData(bills);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Bill's Data Not Present");
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Bill>> findByBillId(String id) {
        ResponseStructure<Bill> responseStructure = new ResponseStructure<>();
        Bill bill = billDao.findByBillId(id);
        if (bill != null) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Fetched All Bill By Id");
            responseStructure.setData(bill);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Bill's Data Not Present");
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public void generateBillTransactionCSV(HttpServletResponse response, LocalDate startDate, LocalDate endDate) throws Exception {
        List<Bill> bill = billDao.findBylocalDateBetween(startDate, endDate);
        if (bill.size() > 0) {
            try (PrintWriter writer = response.getWriter();
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                csvPrinter.printRecord("Customer Name", "Customer PhoneNumber", "Total Customer",
                        "Activity", "Extra Amenity", "Food Cost", "CheckIn Date", "CheckOut Date", "Gst Number",
                        "Invoice Number", "Bill Date Time", "Mode Of Payment", "Total Cost");
                for (Bill transac : bill) {
                    csvPrinter.printRecord(
                            transac.getCustomerName(), transac.getCustomerPhone(), transac.getTotalCustomer(),
                            transac.getActivity(), transac.getExtraAmenity(), transac.getFoodCost(),
                            transac.getCheckInDate(), transac.getCheckOutDate(), transac.getGstNumber(),
                            transac.getInvoiceNumber(), transac.getLocalDateTime(), transac.getModeOfPayment(), transac.getTotalCost()
                    );
                }
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition",
                        "attachment; filename = bill_" + LocalDate.now() + ".csv");

                response.flushBuffer();
                csvPrinter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("No Bill Data Found");
        }

    }

    public ResponseEntity<ResponseStructure<List<Bill>>> findByCustomerPhone(String phone) {
        ResponseStructure<List<Bill>> responseStructure = new ResponseStructure<>();
        List<Bill> bills = billDao.findByCustomerPhone(phone);
        if (bills.size() > 0) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Fetch By Customer Phone Number");
            responseStructure.setData(bills);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setMessage("Customer Phone Number Not Found");
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

}