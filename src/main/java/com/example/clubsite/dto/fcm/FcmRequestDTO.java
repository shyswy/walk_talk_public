package com.example.clubsite.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FcmRequestDTO {
    private String title;
    private String body;
    private String targetToken;
}
