package com.uniquindio.userservice.service.interfaces;

import com.uniquindio.userservice.dto.PaginatedUserResponse;
import com.uniquindio.userservice.dto.UserResponse;
import com.uniquindio.userservice.dto.UserRegistration;
import com.uniquindio.userservice.dto.UserUpdateRequest;


public interface UserService {

    UserResponse registerUser(UserRegistration user);

    PaginatedUserResponse getUsers(int page, int size);

    UserResponse getUser(int userId);

    UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest);

    void deleteUser(String id);

}
