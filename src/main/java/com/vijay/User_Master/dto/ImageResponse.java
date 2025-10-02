package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse {
    private String imageName;
    private String imageUrl;
    private String message;
    private boolean success;
    private HttpStatus status;
}