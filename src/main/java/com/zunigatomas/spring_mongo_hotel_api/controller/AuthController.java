package com.zunigatomas.spring_mongo_hotel_api.controller;

import com.zunigatomas.spring_mongo_hotel_api.dto.LoginRequest;
import com.zunigatomas.spring_mongo_hotel_api.dto.Response;
import com.zunigatomas.spring_mongo_hotel_api.entity.User;
import com.zunigatomas.spring_mongo_hotel_api.service.interfac.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody User user) {
        Response response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        Response response = userService.login(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}