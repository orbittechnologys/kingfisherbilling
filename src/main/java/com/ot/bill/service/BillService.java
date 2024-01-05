package com.ot.bill.service;

import com.amazonaws.services.s3.AmazonS3;
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

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    public ResponseEntity<ResponseStructure<Bill>> saveBill(Bill bill, double amount, double days) {
        ResponseStructure<Bill> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Save Bill");

        double firstAmount = bill.getActivity() + bill.getFoodCost() + bill.getExtraAmenity();

        double secondAmount = bill.getTotalCustomer() * amount * days;

        double finalAmount = firstAmount + secondAmount;

        bill.setTotalCost(finalAmount);

        bill.setGstNumber("29AEPPD8610N1ZY");

        bill.setInvoiceNumber("INVOICE-" + UUID.randomUUID().toString().split("-")[0].toUpperCase());
        bill.setLocalDate(LocalDate.now());
        Bill b = billDao.saveBill(bill);

        responseStructure.setData(b);

        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    public void generateUserPDF(Bill bill, String pdfFileName) throws IOException {
        PdfWriter writer = new PdfWriter(pdfFileName);
        PageSize customPageSize = new PageSize(35 * 72, 37 * 72);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(customPageSize);
        Document document = new Document(pdf);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd   " + "HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);

        Paragraph paragraph = new Paragraph();
        Paragraph phone = new Paragraph();
        Paragraph firstline = new Paragraph();

        paragraph.add("KINGFISHER RESORT DANDELI");
        phone.add("8 9 7 1 9 5 7 8 9 0").setBold();
        phone.setTextAlignment(TextAlignment.RIGHT);
        phone.setFontSize(100);
        firstline.add("======================================");
        paragraph.setTextAlignment(TextAlignment.LEFT);
        paragraph.setFontSize(160).setBold();
        firstline.setFontSize(100);
        Paragraph userDetails = new Paragraph();

        userDetails.add("DT TM	     :          ");
        userDetails.add(formattedDateTime + "\n").setFontSize(82);
        userDetails.add("Gst Number.   :            ");
        userDetails.add(bill.getGstNumber() + "\n").setFontSize(80);
        userDetails.add("Invoice No   :         ");
        userDetails.add(bill.getInvoiceNumber() + "\n").setFontSize(80);
        userDetails.add("Customer Name    :         ");
        userDetails.add(bill.getCustomerName() + "\n").setFontSize(80);
        userDetails.add("Customer PhoneNumber   :           ");
        userDetails.add(bill.getCustomerPhone() + "\n").setFontSize(80);
        userDetails.add("Activity :         ");
        userDetails.add(bill.getActivity() + "\n").setFontSize(80);
        userDetails.add("Food    :          ");
        userDetails.add(bill.getFoodCost() + "\n").setFontSize(80);
        userDetails.add("Payment Mode:          ");
        userDetails.add(bill.getModeOfPayment() + "\n").setFontSize(80);
        userDetails.add("Amenity:           ");
        userDetails.add(bill.getExtraAmenity() + "\n").setFontSize(80);
        userDetails.add("Total Customer:            ");
        userDetails.add(bill.getTotalCustomer() + "\n").setFontSize(80);
        userDetails.add("Check In Date:             ");
        userDetails.add(bill.getCheckInDate() + "\n").setFontSize(80);
        userDetails.add("Check Out Date:            ");
        userDetails.add(bill.getCheckOutDate() + "\n").setFontSize(80);
        userDetails.add("Total Cost:            ");
        userDetails.add(bill.getTotalCost() + "").setFontSize(80);


        userDetails.setFontSize(84);
        userDetails.setBold();

        Paragraph endline = new Paragraph();
        endline.add("======================================" + "\n");
        endline.setFontSize(100);

        Paragraph tq = new Paragraph();
        tq.add("THANK YOU").setBold();
        tq.setFontSize(100);
        tq.setTextAlignment(TextAlignment.CENTER);

        PdfFont defaultFont = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        document.setProperty(Property.FONT, defaultFont);
        document.add(paragraph);
        document.add(phone);
        document.add(firstline);
        document.add(userDetails);
        document.add(endline);
        document.add(tq);
        document.close();
        pdf.close();
        System.out.println("PDF created successfully.");
    }

    public String uploadToUserS3Bucket(String pdfFileName1) {
        String userBucket = "awsbucket99999";
        String folderName = "KingFisherBillpdf";
        String key = folderName + "/" + pdfFileName1;
        amazonS3.putObject(userBucket, key, new File(pdfFileName1));
        return amazonS3.getUrl(userBucket, key).toString();
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
                csvPrinter.printRecord("Customer Name", "Customer Email", "Customer PhoneNumber", "Total Customer",
                        "Activity", "Extra Amenity", "Food Cost", "CheckIn Date", "CheckOut Date", "Gst Number",
                        "Invoice Number", "Bill Date Time", "Mode Of Payment");
                for (Bill transac : bill) {
                    csvPrinter.printRecord(
                            transac.getCustomerName(), transac.getCustomerEmail(), transac.getCustomerPhone(), transac.getTotalCustomer(),
                            transac.getActivity(), transac.getExtraAmenity(), transac.getFoodCost(),
                            transac.getCheckInDate(), transac.getCheckOutDate(), transac.getGstNumber(),
                            transac.getInvoiceNumber(), transac.getLocalDateTime(), transac.getModeOfPayment()
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