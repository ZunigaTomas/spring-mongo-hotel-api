package com.zunigatomas.spring_mongo_hotel_api.repository;

import com.zunigatomas.spring_mongo_hotel_api.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
