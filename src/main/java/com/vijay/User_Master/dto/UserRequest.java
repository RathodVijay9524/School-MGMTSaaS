package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String phoNo;
    private String about;
    private String imageName;
    private java.util.Set<String> roles;
    private boolean isDeleted;
    private java.time.LocalDateTime deletedOn;
    private String accountStatus; // Add accountStatus field
}