package com.uniquindio.userservice.service.impl;

import com.uniquindio.userservice.dto.PaginatedUserResponse;
import com.uniquindio.userservice.dto.UserRegistration;
import com.uniquindio.userservice.dto.UserResponse;
import com.uniquindio.userservice.dto.UserUpdateRequest;
import com.uniquindio.userservice.service.interfaces.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserResponse registerUser(UserRegistration user) {
        return null;
    }

    @Override
    public PaginatedUserResponse getUsers(int page, int size) {
        return null;
    }

    @Override
    public UserResponse getUser(int userId) {
        return null;
    }

    @Override
    public UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest) {
        return null;
    }

    @Override
    public void deleteUser(String id) {

    }

}
