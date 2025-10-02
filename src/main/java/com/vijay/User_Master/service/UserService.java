package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.PageableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<UserResponse> createUser(UserRequest userRequest);
    UserResponse create(UserRequest userRequest);
    Page<UserResponse> getUsersWithFilter(Boolean isDeleted, Boolean isActive, String keyword, Pageable pageable);
    PageableResponse<UserResponse> getUsersWithFilters(int pageNumber, int pageSize, String sortBy, String sortDir, Boolean isDeleted, Boolean isActive);
    PageableResponse<UserResponse> getAllActiveUsers(Pageable pageable);
    PageableResponse<UserResponse> getAllDeletedUsers(Pageable pageable);
    UserResponse updateUser(Long userId, UserRequest request);
    UserResponse update(Long id, UserRequest request);
    UserResponse getByIdForUser(Long id);
    void updateAccountStatus(Long id, Boolean isActive);
    void softDeleteUser(Long id);
    void restoreUser(Long id);
    void permanentlyDelete(Long id);
    String uploadUserImage(MultipartFile image);
}