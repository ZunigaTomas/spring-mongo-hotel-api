package com.zunigatomas.spring_mongo_hotel_api.service.impl;

import com.zunigatomas.spring_mongo_hotel_api.dto.Response;
import com.zunigatomas.spring_mongo_hotel_api.dto.RoomDto;
import com.zunigatomas.spring_mongo_hotel_api.entity.Booking;
import com.zunigatomas.spring_mongo_hotel_api.entity.Room;
import com.zunigatomas.spring_mongo_hotel_api.exception.CustomException;
import com.zunigatomas.spring_mongo_hotel_api.repository.BookingRepository;
import com.zunigatomas.spring_mongo_hotel_api.repository.RoomRepository;
import com.zunigatomas.spring_mongo_hotel_api.service.AwsS3Service;
import com.zunigatomas.spring_mongo_hotel_api.service.interfac.IRoomService;
import com.zunigatomas.spring_mongo_hotel_api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final AwsS3Service awsS3Service;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository, AwsS3Service awsS3Service) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.awsS3Service = awsS3Service;
    }
    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);

            Room savedRoom = roomRepository.save(room);

            RoomDto roomDto = Utils.mapRoomEntityToDto(savedRoom);

            response.setRoom(roomDto);
            response.setStatusCode(201);
            response.setMessage("Room created successfully");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while creating room: " + e.getMessage());
        }

        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomType();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();

        try {
            List<RoomDto> roomDtoList = roomRepository.findAll().stream().map(Utils::mapRoomEntityToDto).toList();
            response.setRoomList(roomDtoList);
            response.setStatusCode(200);
            response.setMessage("Rooms retrieved successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving all rooms: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response deleteRoom(String roomId) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found with id: " + roomId));
            roomRepository.delete(room);

            response.setStatusCode(200);
            response.setMessage("Room deleted successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while deleting room: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response updateRoom(String roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Couldn't find room with id: " + roomId));

            if(photo != null && !photo.isEmpty()) {
                String photoUrl = awsS3Service.saveImageToS3(photo);
                room.setRoomPhotoUrl(photoUrl);
            }
            if(description != null) room.setRoomDescription(description);

            if(roomType != null) room.setRoomType(roomType);

            if(roomPrice != null) room.setRoomPrice(roomPrice);

            RoomDto roomDto = Utils.mapRoomEntityToDto(roomRepository.save(room));

            response.setRoom(roomDto);
            response.setStatusCode(200);
            response.setMessage("Room updated successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while updating room: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getRoomById(String roomId) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found with id: " + roomId));

            response.setRoom(Utils.mapRoomEntityToDto(room));
            response.setStatusCode(200);
            response.setMessage("Room retrieved successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving room: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();

        try {
            List<Booking> bookings = bookingRepository.findBookingsByDateRange(checkInDate, checkOutDate);
            List<String> bookingIds = bookings.stream().map(Booking::getRoom).map(Room::getId).toList();
            //List<String> bookingIds = bookings.stream().map(booking -> booking.getRoom().getId()).toList();
            List<RoomDto> roomDtoList = roomRepository.findByRoomTypeLikeAndIdNotIn(roomType, bookingIds)
                                                        .stream().map(Utils::mapRoomEntityToDto).toList();

            response.setRoomList(roomDtoList);
            response.setStatusCode(200);
            response.setMessage("Rooms retrieved successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving rooms: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();

        try {
            List<RoomDto> roomDtoList = roomRepository.findAllAvailableRooms().stream().map(Utils::mapRoomEntityToDto).toList();

            response.setRoomList(roomDtoList);
            response.setStatusCode(200);
            response.setMessage("Rooms retrieved successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving rooms: " + e.getMessage());
        }

        return response;
    }
}
