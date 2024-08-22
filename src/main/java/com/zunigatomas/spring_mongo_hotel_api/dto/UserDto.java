package com.zunigatomas.spring_mongo_hotel_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String id;
    private String email;
    private String name;
    private String phoneNumber;
    private String role;
    private List<BookingDto> bookings = new ArrayList<>();
}
