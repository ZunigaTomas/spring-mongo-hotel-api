package com.zunigatomas.spring_mongo_hotel_api.service.interfac;

import com.zunigatomas.spring_mongo_hotel_api.dto.Response;
import com.zunigatomas.spring_mongo_hotel_api.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IBookingService {
    Response saveBooking(String roomId, String userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(String bookingId);
}
