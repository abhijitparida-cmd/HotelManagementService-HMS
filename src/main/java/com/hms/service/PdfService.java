package com.hms.service;

import com.hms.entity.*;
import com.hms.payload.AppUserDto;
import com.hms.payload.BookingDto;
import com.hms.payload.PropertyDto;
import com.hms.repository.PriceRepository;
import com.hms.repository.PropertyRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class PdfService {

    private final PropertiesService propertiesService;

    public PdfService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public void generatePdf(String filePath, PropertyDto propertyDto, AppUserDto appUserDto,
                            BookingDto bookingDto, BigDecimal totalPrice, Bookings bookings) throws IOException {
        try {
            // Initialize document
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Logo
            Image logo = Image.getInstance("D:\\FILES\\HMS_Project_Files\\Stay_Easy.png");
            logo.scaleToFit(80, 80);
            logo.setAlignment(Element.ALIGN_LEFT);

            // Create JSON data for the QR Code
            String qrCodeJson = createQrCodeJson(propertyDto, appUserDto, bookingDto, totalPrice);

            // Generate the QR Code with JSON data
            BarcodeQRCode qrCode = new BarcodeQRCode(qrCodeJson, 100, 100, null);
            Image qrCodeImage = qrCode.getImage();
            qrCodeImage.scaleToFit(100, 100);
            qrCodeImage.setAlignment(Element.ALIGN_RIGHT);

            // Fonts
            Font titleFont1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.RED);

            // Title1 and Title
            Paragraph title1 = new Paragraph("STAY EASY", titleFont1);
            title1.setAlignment(Element.ALIGN_CENTER);

            Paragraph title = new Paragraph("Booking Confirmation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);

            // Nested Table for Titles
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell();
            titleCell.addElement(title1);
            titleCell.addElement(title);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleTable.addCell(titleCell);

            // Main Table for Header Layout
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            float[] columnWidths = {1, 2, 1}; // Logo, Titles, QR Code
            headerTable.setWidths(columnWidths);

            // Add Logo to header
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(logoCell);

            // Add Titles to header
            PdfPCell titleTableCell = new PdfPCell(titleTable);
            titleTableCell.setBorder(Rectangle.NO_BORDER);
            titleTableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleTableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(titleTableCell);

            // Add QR Code to header
            PdfPCell qrCodeCell = new PdfPCell(qrCodeImage);
            qrCodeCell.setBorder(Rectangle.NO_BORDER);
            qrCodeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            qrCodeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(qrCodeCell);

            document.add(headerTable);

            // Introductory paragraph
            Font codeParagraph = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Paragraph paragraph1 = new Paragraph("Booking ID: '"+bookings.getBookingCode()+"'", codeParagraph);
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            paragraph1.setSpacingAfter(10);
            document.add(paragraph1);

            // Introductory paragraph
            Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
            Paragraph paragraph = new Paragraph("Thank you for booking with us. Here are your booking details.", paragraphFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(15);
            document.add(paragraph);

            // Table for Customer Information, Hotel Details, and Booking Details
            PdfPTable table = new PdfPTable(4); // 4 columns instead of 2
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            float[] columnWidths2 = {1f, 2f, 1f, 2f}; // Adjusted column widths
            table.setWidths(columnWidths2);

            // Header style
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            PdfPCell headerCell;

            // Customer Information
            headerCell = new PdfPCell(new Phrase("CUSTOMER INFORMATION", headerFont));
            headerCell.setBackgroundColor(new BaseColor(66, 74, 119));
            headerCell.setColspan(4);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPadding(6);
            table.addCell(headerCell);

            // Customer Details
            table.addCell(createCellWithBgColor("Name:", paragraphFont));
            table.addCell(createCell(appUserDto.getName(), paragraphFont));
            String bookingDate = bookingDto.getBookingDate() != null ? bookingDto.getBookingDate() : "N/A";
            table.addCell(createCellWithBgColor("Confirm Booking Date:", paragraphFont));
            table.addCell(createCell(bookingDate, paragraphFont));
            table.addCell(createCellWithBgColor("Contact:", paragraphFont));
            table.addCell(createCell(appUserDto.getMobileNum(), paragraphFont));
            table.addCell(createCellWithBgColor("Email:", paragraphFont));
            table.addCell(createCell(appUserDto.getEmail(), paragraphFont));

            // Hotel Information
            headerCell = new PdfPCell(new Phrase("HOTEL DETAILS", headerFont));
            headerCell.setBackgroundColor(new BaseColor(66, 74, 119));
            headerCell.setColspan(4);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPadding(6);
            table.addCell(headerCell);

            // Hotel Details
            table.addCell(createCellWithBgColor("Hotel Name:", paragraphFont));
            table.addCell(createCell(propertyDto.getHotelName(), paragraphFont));
            table.addCell(createCellWithBgColor("Room Type:", paragraphFont));
            table.addCell(createCell(propertyDto.getRoomTypes(), paragraphFont));
            table.addCell(createCellWithBgColor("Room Price (Per-Night):", paragraphFont));
            table.addCell(createCell(String.valueOf(propertyDto.getPriceOfRooms()), paragraphFont));
            table.addCell(createCellWithBgColor("Location:", paragraphFont));
            table.addCell(createCell(propertyDto.getLocationName() + ", " + propertyDto.getCityName() + ", " +
                    propertyDto.getStateName() + ", " + propertyDto.getCountryName() + ", " + propertyDto.getPinCode(), paragraphFont));

            // Booking Information
            headerCell = new PdfPCell(new Phrase("BOOKING DETAILS", headerFont));
            headerCell.setBackgroundColor(new BaseColor(66, 74, 119));
            headerCell.setColspan(4);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPadding(6);
            table.addCell(headerCell);

            // Booking Details
            table.addCell(createCellWithBgColor("Guests:", paragraphFont));
            table.addCell(createCell("Adults:"+bookingDto.getNoOfAdults()+", Child:"+bookingDto.getNoOfChildren(), paragraphFont));
            table.addCell(createCellWithBgColor("Rooms:", paragraphFont));
            table.addCell(createCell(bookingDto.getNoOfRooms(), paragraphFont));

            String[] formattedDates = BookingService.formatBookingDates(bookingDto);

            table.addCell(createCellWithBgColor("CheckIn Date:", paragraphFont));
            table.addCell(createCell(formattedDates[0], paragraphFont));
            table.addCell(createCellWithBgColor("CheckOut Date:", paragraphFont));
            table.addCell(createCell(formattedDates[1], paragraphFont));

            // Header for Price Details
            headerCell = new PdfPCell(new Phrase("PRICE DETAILS", headerFont));
            headerCell.setBackgroundColor(new BaseColor(66, 74, 119));
            headerCell.setColspan(4);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPadding(6);
            table.addCell(headerCell);

            // Add Price Details Rows
            BigDecimal basePriceForRooms = BookingService.getBasePriceRoom(bookingDto,propertyDto.getPriceOfRooms());

            BigDecimal CgstTax = totalPrice.multiply(BigDecimal.valueOf(0.025)); // 2.5% CGST
            BigDecimal sgstTax = totalPrice.multiply(BigDecimal.valueOf(0.025)); // 2.5% SGST

            // Ensure all values are formatted to two decimal places
            CgstTax = CgstTax.setScale(2, RoundingMode.HALF_UP);
            sgstTax = sgstTax.setScale(2, RoundingMode.HALF_UP);
            totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

            table.addCell(createCellWithBgColor("Room Price:", paragraphFont));
            table.addCell(createCell(basePriceForRooms.toString(), paragraphFont));
            table.addCell(createCellWithBgColor("SGST(2.5%):", paragraphFont));
            table.addCell(createCell(sgstTax.toString(), paragraphFont));
            table.addCell(createCellWithBgColor("CGST(2.5%):", paragraphFont));
            table.addCell(createCell(CgstTax.toString(), paragraphFont));
            table.addCell(createCellWithBgColor("Total Price:", paragraphFont));
            table.addCell(createCell(totalPrice.toString(), paragraphFont));

            document.add(table);

            // Rules
            Font rulesFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Paragraph rules = getElements(rulesFont);
            document.add(rules);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    // Create QR Code JSON data
    private String createQrCodeJson(PropertyDto propertyDto, AppUserDto appUserDto,
                                    BookingDto bookingDto, BigDecimal totalPrice) {
        // Constructing the base URL
        String baseUrl = "https://httpbin.org/get?";

        String[] formattedDates = BookingService.formatBookingDates(bookingDto);

        // Encoding the booking information as query parameters
        String url = baseUrl + "bookingCode=" + encodeURIComponent(bookingDto.getBookingCode()) +
                "&guestName=" + encodeURIComponent(appUserDto.getName()) +
                "&contact=" + encodeURIComponent(appUserDto.getMobileNum()) +
                "&email=" + encodeURIComponent(appUserDto.getEmail()) +
                "&hotelName=" + encodeURIComponent(propertyDto.getHotelName()) +
                "&roomType=" + encodeURIComponent(propertyDto.getRoomTypes()) +
                "&checkIn=" + encodeURIComponent(formattedDates[0]) +
                "&checkOut=" + encodeURIComponent(formattedDates[1]) +
                "&noOfRooms=" + encodeURIComponent(String.valueOf(bookingDto.getNoOfRooms())) +
                "&noOfAdults=" + encodeURIComponent(String.valueOf(bookingDto.getNoOfAdults())) +
                "&noOfChild=" + encodeURIComponent(String.valueOf(bookingDto.getNoOfChildren())) +
                "&location=" + encodeURIComponent(propertyDto.getLocationName() + ", " + propertyDto.getCityName() + ", " +
                propertyDto.getStateName() + ", " + propertyDto.getCountryName() + ", " + propertyDto.getPinCode()) +
                "&roomPrice=" + encodeURIComponent(String.valueOf(propertyDto.getPriceOfRooms())) +
                "&sgst=" + encodeURIComponent(String.format("%.2f", totalPrice.multiply(BigDecimal.valueOf(0.025)))) +
                "&cgst=" + encodeURIComponent(String.format("%.2f", totalPrice.multiply(BigDecimal.valueOf(0.025)))) +
                "&totalPrice=" + encodeURIComponent(String.format("%.2f", totalPrice.add(totalPrice.multiply(BigDecimal.valueOf(0.025))).multiply(BigDecimal.valueOf(2))));

        return url;
    }

    private String encodeURIComponent(String value) {
        try {
            return value == null ? "" : java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
        return sdf.format(date);
    }


    private static Paragraph getElements(Font rulesFont) {
        Paragraph rules = new Paragraph(
                "Please note the following guidelines for a smooth stay:\n\n" +
                        "1. Check-in is from 02:00 PM, and Check-out is by 11:00 AM.\n" +
                        "2. Valid government-issued ID is required at check-in for all guests.\n" +
                        "3. Only registered guests are permitted access to the property.\n" +
                        "4. Smoking is prohibited in all indoor areas; designated smoking zones are available.\n" +
                        "5. No parties or events allowed on the premises to maintain a peaceful environment.\n" +
                        "6. Please respect quiet hours from 11:00 PM to 8:00 AM.\n" +
                        "7. Pets are not allowed unless stated in the booking terms.\n\n" +
                        "For any assistance or additional requests, please contact our customer service team. We hope you enjoy your stay!",
                rulesFont);
        rules.setSpacingBefore(15);
        rules.setAlignment(Element.ALIGN_LEFT);
        return rules;
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private PdfPCell createCellWithBgColor(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(4);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(BaseColor.BLACK);
        return cell;
    }
}
