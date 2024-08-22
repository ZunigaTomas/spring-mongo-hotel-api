package com.zunigatomas.spring_mongo_hotel_api.repository;

import com.zunigatomas.spring_mongo_hotel_api.entity.Room;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room,String> {
    @Aggregation(" {$group: {_id: '$roomType'}} ")
    List<String> findDistinctRoomType();
    @Query(" {'bookings':  {$size: 0 }} ")
    List<Room> findAllAvailableRooms();
    List<Room> findByRoomTypeLikeAndIdNotIn(String roomType, List<String> bookedRoomIds);
}
