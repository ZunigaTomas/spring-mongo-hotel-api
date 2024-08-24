package com.zunigatomas.spring_mongo_hotel_api.service.interfac;

import com.zunigatomas.spring_mongo_hotel_api.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IBookingService {

    Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description);
    List<String> getAllRoomTypes();
    Response getAllRooms();
    Response deleteRoom(String roomId);
    Response updateRoom(String roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo);
    Response getRoomById(String roomId);
    Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
    Response getAllAvailableRooms();
}
