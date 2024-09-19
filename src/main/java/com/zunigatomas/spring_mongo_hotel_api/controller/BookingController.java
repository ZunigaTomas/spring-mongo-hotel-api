package com.zunigatomas.spring_mongo_hotel_api.controller;

import com.zunigatomas.spring_mongo_hotel_api.dto.Response;
import com.zunigatomas.spring_mongo_hotel_api.entity.Booking;
import com.zunigatomas.spring_mongo_hotel_api.service.interfac.IBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book-room/{roomId}/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> saveBooking (
            @PathVariable("roomId") String roomId,
            @PathVariable("userId") String userId,
            @RequestBody Booking bookingRequest
    ) {
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllBookings() {
        Response response = bookingService.getAllBookings();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-confirmation-code/{confirmationCode}")
    public ResponseEntity<Response> getBookingByConfirmationCode(@PathVariable("confirmationCode") String confirmationCode) {
        Response response = bookingService.findBookingByConfirmationCode(confirmationCode);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> cancelBooking(@PathVariable("bookingId") String bookingId) {
        Response response = bookingService.cancelBooking(bookingId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}