package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phoNo;
    private String about;
    private String imageName;
    private boolean isDeleted;
    private Date deletedOn;
    private Set<String> roles;
    private AccountStatus accountStatus;
    private Date createdOn;
    private Date updatedOn;
}