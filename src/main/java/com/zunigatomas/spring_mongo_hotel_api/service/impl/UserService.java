package com.zunigatomas.spring_mongo_hotel_api.service.impl;

import com.zunigatomas.spring_mongo_hotel_api.dto.LoginRequest;
import com.zunigatomas.spring_mongo_hotel_api.dto.Response;
import com.zunigatomas.spring_mongo_hotel_api.dto.UserDto;
import com.zunigatomas.spring_mongo_hotel_api.entity.User;
import com.zunigatomas.spring_mongo_hotel_api.exception.CustomException;
import com.zunigatomas.spring_mongo_hotel_api.repository.UserRepository;
import com.zunigatomas.spring_mongo_hotel_api.service.interfac.IUserService;
import com.zunigatomas.spring_mongo_hotel_api.utils.JWTUtils;
import com.zunigatomas.spring_mongo_hotel_api.utils.Utils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, JWTUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Response register(User user) {
        Response response = new Response();
        try {
            if(user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }

            if(repository.existsByEmail(user.getEmail())) {
                throw new CustomException("Email already registered");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = repository.save(user);

            UserDto userDto = Utils.mapUserEntityToDto(savedUser);

            response.setStatusCode(201);
            response.setMessage("Successfully saved");
            response.setUser(userDto);
        } catch (CustomException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while registering a user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = repository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new CustomException("User not found"));
            var token = jwtUtils.generateToken(user);

            response.setStatusCode(200);
            response.setMessage("Login successful");
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while logging in: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();

        try {
            List<User> userList = repository.findAll();
            List<UserDto> userDtoList = userList.stream().map(Utils::mapUserEntityToDto).toList();

            response.setStatusCode(200);
            response.setMessage("Users retrieved successfully");
            response.setUserList(userDtoList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving all users: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();

        try {
            User user = repository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
            UserDto userDto = Utils.mapUserEntityToDtoPlusUserBookingsAndRoom(user);

            response.setStatusCode(200);
            response.setMessage("User booking history retrieved");
            response.setUser(userDto);

        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while getting user booking history: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();

        try {
            User user = repository.findById(userId).orElseThrow(() -> new CustomException("User not found with id: " + userId));
            repository.delete(user);

            response.setStatusCode(200);
            response.setMessage("User deleted successfully");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while deleting user: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();

        try {
            User user = repository.findById(userId).orElseThrow(() -> new CustomException("User not found with id: " + userId));
            UserDto userDto = Utils.mapUserEntityToDto(user);

            response.setStatusCode(200);
            response.setMessage("User retrieved");
            response.setUser(userDto);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while retrieving user: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();

        try {
            User user = repository.findByEmail(email).orElseThrow(() -> new CustomException("User not found with email: " + email));
            UserDto userDto = Utils.mapUserEntityToDto(user);

            response.setStatusCode(200);
            response.setMessage("Info retrieved successfully");
            response.setUser(userDto);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while getting info: " + e.getMessage());
        }

        return response;
    }
}
