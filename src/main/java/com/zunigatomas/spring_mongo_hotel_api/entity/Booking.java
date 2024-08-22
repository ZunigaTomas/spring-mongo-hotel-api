package com.zunigatomas.spring_mongo_hotel_api.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    @NotBlank(message = "Check-in date is required")
    private LocalDate checkInDate;

    @NotBlank(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Number of adults should not be less than one")
    private int numOfAdults;

    @Min(value = 0, message = "Number of children should not be less than zero")
    private int numOfChildren;

    @Transient
    private int totalNumOfGuests;

    private String bookingConfirmationCode;

    @DBRef
    private User user;

    @DBRef
    private Room room;

    public void calculateTotalNumberOfGuests() {
        this.totalNumOfGuests = this.numOfAdults + this.numOfChildren;
    }
    public void setNumOfAdults(int numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalNumberOfGuests();
    }
    public void setNumOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
        calculateTotalNumberOfGuests();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numOfAdults=" + numOfAdults +
                ", numOfChildren=" + numOfChildren +
                ", totalNumOfGuests=" + totalNumOfGuests +
                ", bookingConfirmationCode='" + bookingConfirmationCode + '\'' +
                '}';
    }
}