package com.zunigatomas.spring_mongo_hotel_api.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

}
