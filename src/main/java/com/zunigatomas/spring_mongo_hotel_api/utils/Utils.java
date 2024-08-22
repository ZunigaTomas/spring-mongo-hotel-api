package com.zunigatomas.spring_mongo_hotel_api.utils;

import com.zunigatomas.spring_mongo_hotel_api.dto.BookingDto;
import com.zunigatomas.spring_mongo_hotel_api.dto.RoomDto;
import com.zunigatomas.spring_mongo_hotel_api.dto.UserDto;
import com.zunigatomas.spring_mongo_hotel_api.entity.Booking;
import com.zunigatomas.spring_mongo_hotel_api.entity.Room;
import com.zunigatomas.spring_mongo_hotel_api.entity.User;

import java.security.SecureRandom;
import java.util.List;

public class Utils {
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    public static UserDto mapUserEntityToDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRole(user.getRole());

        return userDto;
    }

    public static RoomDto mapRoomEntityToDto(Room room) {
        RoomDto roomDto = new RoomDto();

        roomDto.setId(room.getId());
        roomDto.setRoomType(room.getRoomType());
        roomDto.setRoomPrice(room.getRoomPrice());
        roomDto.setRoomPhotoUrl(room.getRoomPhotoUrl());
        roomDto.setRoomDescription(room.getRoomDescription());

        return roomDto;
    }

    public static BookingDto mapBookingEntityToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setCheckInDate(booking.getCheckInDate());
        bookingDto.setCheckOutDate(booking.getCheckOutDate());
        bookingDto.setNumOfAdults(booking.getNumOfAdults());
        bookingDto.setNumOfChildren(booking.getNumOfChildren());
        bookingDto.setTotalNumOfGuests(booking.getTotalNumOfGuests());
        bookingDto.setBookingConfirmationCode(booking.getBookingConfirmationCode());

        return bookingDto;
    }

    public static RoomDto mapRoomEntityToDtoPlusBookings(Room room) {
        RoomDto roomDto = mapRoomEntityToDto(room);

        if(room.getBookings() != null) {
            roomDto.setBookings(room.getBookings().stream().map(Utils::mapBookingEntityToDto).toList());
        }

        return roomDto;
    }

    public static BookingDto mapBookingEntityToDtoPlusBookedRooms(Booking booking, boolean mapUser) {
        BookingDto bookingDto = mapBookingEntityToDto(booking);

        if(mapUser) {
            bookingDto.setUser(Utils.mapUserEntityToDto(booking.getUser()));
        }

        if(booking.getRoom() != null) {
            RoomDto roomDto = new RoomDto();

            roomDto.setId(booking.getRoom().getId());
            roomDto.setRoomType(booking.getRoom().getRoomType());
            roomDto.setRoomPrice(booking.getRoom().getRoomPrice());
            roomDto.setRoomPhotoUrl(booking.getRoom().getRoomPhotoUrl());
            roomDto.setRoomDescription(booking.getRoom().getRoomDescription());

            bookingDto.setRoom(roomDto);
        }

        return bookingDto;
    }

    public static UserDto mapUserEntityToDtoPlusUserBookingsAndRoom(User user) {
        UserDto userDto = mapUserEntityToDto(user);

        if(!user.getBookings().isEmpty()) {
            userDto.setBookings(user.getBookings().stream().map(booking -> mapBookingEntityToDtoPlusBookedRooms(booking, true)).toList());
        }
        return userDto;
    }

    public static List<UserDto> mapUserListEntityToDto(List<User> userList) {
        return userList.stream().map(Utils::mapUserEntityToDto).toList();
    }
    public static List<RoomDto> mapRoomListEntityToDto(List<Room> roomList) {
        return roomList.stream().map(Utils::mapRoomEntityToDto).toList();
    }
    public static List<BookingDto> mapBookingListEntityToDto(List<Booking> bookingList) {
        return bookingList.stream().map(Utils::mapBookingEntityToDto).toList();
    }

}
