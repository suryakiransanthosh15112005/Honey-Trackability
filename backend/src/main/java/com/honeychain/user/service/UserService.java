package com.honeychain.user.service;

import com.honeychain.user.dto.UserResponse;
import com.honeychain.user.entity.User;

import java.util.List;

public interface UserService {

    UserResponse getUserByPhoneNumber(String phoneNumber);

    UserResponse getUserById(Long id);

    User findEntityByPhoneNumber(String phoneNumber);

    List<UserResponse> getAllUsers();
}
