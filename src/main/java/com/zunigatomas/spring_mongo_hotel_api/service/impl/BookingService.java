package com.zunigatomas.spring_mongo_hotel_api.service.impl;

import com.zunigatomas.spring_mongo_hotel_api.dto.*;
import com.zunigatomas.spring_mongo_hotel_api.entity.*;
import com.zunigatomas.spring_mongo_hotel_api.exception.CustomException;
import com.zunigatomas.spring_mongo_hotel_api.repository.*;
import com.zunigatomas.spring_mongo_hotel_api.service.interfac.*;
import com.zunigatomas.spring_mongo_hotel_api.utils.Utils;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;

import java.util.List;

public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final IRoomService roomService;
    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository, IRoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomService = roomService;
    }
    @Override
    public Response saveBooking(String roomId, String userId, Booking bookingRequest) {
        Response response = new Response();

        try {
            if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check-in date must come before check-out date");
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found with id: " + roomId));
            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found with id: " + userId));

            List<Booking> existingBookings = room.getBookings();

            if(!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new CustomException("Room not available for selected date range");
            }

            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);

            Booking savedBooking = bookingRepository.save(bookingRequest);

            //Agregamos a la lista de reservas del usuario
            List<Booking> userBookings = user.getBookings();
            userBookings.add(savedBooking);
            user.setBookings(userBookings);
            userRepository.save(user);

            //A la lista del cuarto en si
            List<Booking> roomBookings = room.getBookings();
            roomBookings.add(savedBooking);
            room.setBookings(roomBookings);
            roomRepository.save(room);

            response.setStatusCode(201);
            response.setMessage("Booking created successfully");
            response.setBookingConfirmationCode(bookingConfirmationCode);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while booking room: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new CustomException("Couldn't find booking with code: " + confirmationCode));
            response.setBooking(Utils.mapBookingEntityToDto(booking));
            response.setStatusCode(200);
            response.setMessage("Booking retrieved successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving booking: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();

        try {
            List<BookingDto> bookingDtoList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC)).stream().map(Utils::mapBookingEntityToDto).toList();
            response.setBookingList(bookingDtoList);
            response.setStatusCode(200);
            response.setMessage("All bookings retrieved successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all bookings: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response cancelBooking(String bookingId) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new CustomException("Booking not found with id: " + bookingId));

            User user = booking.getUser();
            if(user != null) {
                user.getBookings().removeIf(b -> b.getId().equals(bookingId));
                userRepository.save(user);
            }

            Room room = booking.getRoom();
            if(room != null) {
                room.getBookings().removeIf(b -> b.getId().equals(bookingId));
                roomRepository.save(room);
            }

            bookingRepository.deleteById(bookingId);

            response.setStatusCode(200);
            response.setMessage("Booking deleted successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while cancelling booking: " + e.getMessage());
        }

        return response;
    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}