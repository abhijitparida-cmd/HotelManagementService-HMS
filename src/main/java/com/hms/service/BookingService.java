package com.hms.service;

import com.hms.entity.*;
import com.hms.payload.AppUserDto;
import com.hms.payload.BookingDto;
import com.hms.payload.PropertyDto;
import com.hms.repository.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final BookingsRepository bookingsRepository;
    private final PropertyRepository propertyRepository;
    private final PropertiesService propertiesService;
    private final PriceRepository priceRepository;
    private final AppUserService appUserService;
    private final TwilioService twilioService;
    private final PdfService pdfService;

    // ------------------- Constructor ------------------- //

    public BookingService(
            RoomAvailabilityRepository roomAvailabilityRepository,
            BookingsRepository bookingsRepository,
            PropertyRepository propertyRepository,
            PropertiesService propertiesService,
            PriceRepository priceRepository,
            AppUserService appUserService,
            TwilioService twilioService,
            PdfService pdfService) {
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.propertyRepository = propertyRepository;
        this.bookingsRepository = bookingsRepository;
        this.propertiesService = propertiesService;
        this.priceRepository = priceRepository;
        this.appUserService = appUserService;
        this.twilioService = twilioService;
        this.pdfService = pdfService;
    }

    // -------------------- Convert --------------------- //

    public Bookings convertDtoToEntity(BookingDto bookingDto, Long propertyId, AppUser appUserId){
        Bookings bookings = new Bookings();
        bookings.setNoOfAdults(bookingDto.getNoOfAdults());
        bookings.setNoOfChildren(bookingDto.getNoOfChildren());
        bookings.setNoOfRooms(bookingDto.getNoOfRooms());

        if (bookingDto.getBookingDate() == null || bookingDto.getBookingDate().isEmpty()) {
            bookingDto.setBookingDate(getCurrentTimeInHHMM());
        }
        bookings.setBookingDate(parseDate(bookingDto.getBookingDate()));
        bookings.setCheckIn(bookingDto.getCheckIn());
        bookings.setCheckOut(bookingDto.getCheckOut());
        bookings.setTotalPrice(getTotalPrice(bookingDto, propertyId));
        bookings.setBookingCode("BOOK" + System.currentTimeMillis());
        bookings.setAppUserId(appUserId);
        bookings.setPropertyId(propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found")));
        return bookings;
    }

    public BookingDto convertEntityToDto(Bookings bookings) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setNoOfAdults(bookings.getNoOfAdults());
        bookingDto.setNoOfChildren(bookings.getNoOfChildren());
        bookingDto.setNoOfRooms(bookings.getNoOfRooms());
        bookingDto.setBookingDate(bookings.getBookingDate().toString());
        bookingDto.setCheckIn(bookings.getCheckIn());
        bookingDto.setCheckOut(bookings.getCheckOut());
        bookingDto.setTotalPrice(bookings.getTotalPrice());
        bookingDto.setBookingCode(bookings.getBookingCode());
        PropertyDto propertyDto = propertiesService.convertEntityToDto(bookings.getPropertyId());
        AppUserDto appUserDto = appUserService.mapToDto(bookings.getAppUserId());
        bookingDto.setPropertyId(propertyDto);
        bookingDto.setAppUserId(appUserDto);
        return bookingDto;
    }

    // ------------------ DateTimeMethod ------------------ //

    public String getCurrentTimeInHHMM() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
        return now.format(formatter);
    }

    public static Date parseDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date: " + date, e);
        }
    }

    public static String[] formatBookingDates(BookingDto bookingDto) {
        // Define the desired date format
        SimpleDateFormat desiredFormat = new SimpleDateFormat("EEE MMM dd,yyyy hh:mma");

        // Adjust check-in time to 02:00 PM
        Calendar checkInCalendar = Calendar.getInstance();
        checkInCalendar.setTime(bookingDto.getCheckIn());
        checkInCalendar.set(Calendar.HOUR_OF_DAY, 14); // 2 PM in 24-hour format
        checkInCalendar.set(Calendar.MINUTE, 0);
        Date adjustedCheckIn = checkInCalendar.getTime();

        // Adjust check-out time to 11:00 AM
        Calendar checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.setTime(bookingDto.getCheckOut());
        checkOutCalendar.set(Calendar.HOUR_OF_DAY, 11); // 11 AM in 24-hour format
        checkOutCalendar.set(Calendar.MINUTE, 0);
        Date adjustedCheckOut = checkOutCalendar.getTime();

        // Format the adjusted check-in and check-out times
        String formattedCheckIn = desiredFormat.format(adjustedCheckIn);
        String formattedCheckOut = desiredFormat.format(adjustedCheckOut);

        // Return the formatted dates as an array
        return new String[]{formattedCheckIn, formattedCheckOut};
    }

    // --------------------- Room Availability Check ---------------------- //

    public boolean RoomsAvailableCheck(Long propertyId, BookingDto bookingDto) {
        Date checkIn = bookingDto.getCheckIn();
        Date checkOut = bookingDto.getCheckOut();
        long noOfRooms = Long.parseLong(bookingDto.getNoOfRooms());

        Date adjustedCheckOut = Date.from(checkOut.toInstant().minusSeconds(24 * 60 * 60));

        List<RoomAvailability> roomAvailabilityList =
                roomAvailabilityRepository.findRoomAvailabilityByPropertyAndDateRange(propertyId, checkIn, adjustedCheckOut);

        for (RoomAvailability roomAvailability : roomAvailabilityList) {
            if (roomAvailability.getAvailableRooms() < noOfRooms) {
                return false;
            }
        }
        return true; // All rooms available
    }

    public boolean roomPerPersonCheck(BookingDto bookingDto) {
        int personsPerRoom = Integer.parseInt(bookingDto.getNoOfAdults());
        int childrenPerRoom = Integer.parseInt(bookingDto.getNoOfChildren());
        if (personsPerRoom > 2 && childrenPerRoom > 2) {
            return false;
        }
        return true;
    }

    // ------------------- SetTotalPrice ------------------ //

    public BigDecimal getTotalPrice(BookingDto bookingDto, Long propertyId) {
        // Tax
        BigDecimal taxes = BigDecimal.valueOf(2.5).divide(BigDecimal.valueOf(100));

        // Property and room details
        Property property = propertyRepository.findById(propertyId).orElseThrow(() ->
                new IllegalArgumentException("Property not found"));
        Room room = property.getRoomId();
        Hotels hotel = property.getHotelId();

        // Room price
        Price roomPrice = priceRepository.findByRoomIdAndHotelId(room, hotel)
                .orElseThrow(() -> new IllegalArgumentException("Price not found"));
        BigDecimal roomRate = roomPrice.getPriceOfRooms();

        // Calculate number of nights excluding the checkout
        long numberOfNights = getNumberOfNightsExcludingCheckout(bookingDto.getCheckIn(), bookingDto.getCheckOut());

        // Number of rooms
        long noOfRooms = Long.parseLong(bookingDto.getNoOfRooms());

        // price: room rate * number of rooms * number of nights
        BigDecimal basePriceForRooms = roomRate.multiply(BigDecimal.valueOf(noOfRooms))
                .multiply(BigDecimal.valueOf(numberOfNights));

        // Calculate taxes
        BigDecimal cgstAmount = basePriceForRooms.multiply(taxes);
        BigDecimal sgstAmount = basePriceForRooms.multiply(taxes);

        // Total price including taxes
        return basePriceForRooms.add(cgstAmount).add(sgstAmount);
    }

    private long getNumberOfNightsExcludingCheckout(Date checkIn, Date checkOut) {
        if (checkOut.before(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        long times = checkOut.getTime() - checkIn.getTime();
        return (times / (1000 * 60 * 60 * 24));
    }

    // --------------------- Booking ---------------------- //

    public boolean verifyProperties(Long propertyId) {
        return propertyRepository.existsById(propertyId);
    }

    public BookingDto createBooking(Long propertyId, AppUser appUserId, BookingDto bookingDto) throws IOException{

        if (bookingDto.getBookingDate() == null || bookingDto.getBookingDate().isEmpty()) {
            bookingDto.setBookingDate(getCurrentTimeInHHMM());
        }
        long noOfRooms = Long.parseLong(bookingDto.getNoOfRooms());

        // Check room availability before proceeding
        if (!RoomsAvailableCheck(propertyId, bookingDto)) {
            throw new IllegalArgumentException("Not enough rooms available for the selected dates.");
        }

        Bookings bookings = bookingsRepository.save(convertDtoToEntity(bookingDto, propertyId, appUserId));
        generateBookingConfirmationPdf(propertyId, appUserId, bookingDto);
        updateRoomAvailability(propertyId, bookingDto.getCheckIn(), bookingDto.getCheckOut(), noOfRooms);
        return convertEntityToDto(bookings);
    }

    // -------------------- PdfGenerate -------------------- //

    public void generateBookingConfirmationPdf(Long propertyId, AppUser appUserId, BookingDto bookingDto) throws IOException{
        Property property = propertyRepository.findById(propertyId).get();
        PropertyDto propertyDto = propertiesService.convertEntityToDto(property);
        AppUserDto appUserDto = appUserService.mapToDto(appUserId);
        BigDecimal totalPrice = getTotalPrice(bookingDto, propertyId);
        String filePath = "D:\\FILES\\HMS_Project_Files\\Booking_Details\\Booking_Conformation" + "_" + Instant.now().toEpochMilli() + ".pdf";
        pdfService.generatePdf(filePath, propertyDto, appUserDto, bookingDto, totalPrice);
        SmsService(propertyId, appUserId, bookingDto);
    }

    // --------------------- Send SMS ---------------------- //

    private void SmsService(Long propertyId, AppUser appUserId, BookingDto bookingDto){
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found"));
        PropertyDto propertyDto = propertiesService.convertEntityToDto(property);
        AppUserDto appUserDto = appUserService.mapToDto(appUserId);

        String[] formattedDates = formatBookingDates(bookingDto);

        String customer = appUserDto.getName();
        String email = appUserDto.getEmail();
        String phoneNumber = appUserDto.getMobileNum();
        String hotelName = propertyDto.getHotelName();
        String adults = bookingDto.getNoOfAdults();
        String child = bookingDto.getNoOfChildren();
        String room = bookingDto.getNoOfRooms();
        String roomType = propertyDto.getRoomTypes();
        String formattedCheckIn = formattedDates[0];
        String formattedCheckOut = formattedDates[1];
        BigDecimal price = getTotalPrice(bookingDto, propertyId);
        price = price.setScale (2, RoundingMode.HALF_UP);

        // SMS body
        String message = String.format(
                "Your booking is confirmed! Here are your booking details:\n" +
                        "Customer: %s\n" +
                        "Email: %s\n" +
                        "Mobile: %s\n"+
                        "Hotel: %s\n" +
                        "Adults: %s\n"+
                        "children: %s\n"+
                        "Room: %s\n" +
                        "Room-Types: %s\n"+
                        "Check-In: %s\n" +
                        "Check-Out: %s\n"+
                        "Total-Price: %s",
                customer,email,phoneNumber,hotelName,adults,child,room,
                roomType,formattedCheckIn, formattedCheckOut,price);

        twilioService.sendBookingConfirmationSms(message);
        twilioService.sendBookingConfirmationWhatsAppMessage(message);
    }

    // ------------- Update Room Availability -------------- //

    public void updateRoomAvailability(Long propertyId, Date checkIn, Date checkOut, Long noOfRooms) {
        Date adjustedCheckOut = Date.from(checkOut.toInstant().minusSeconds(24 * 60 * 60));
        List<RoomAvailability> roomAvailabilityList =
                roomAvailabilityRepository.findRoomAvailabilityByPropertyAndDateRange(propertyId, checkIn, adjustedCheckOut);
        for (RoomAvailability room : roomAvailabilityList) {
            if (!roomAvailabilityList.isEmpty()) {
                room.setAvailableRooms((int) (room.getAvailableRooms() - noOfRooms));
                roomAvailabilityRepository.save(room);
            }
        }
    }
}
