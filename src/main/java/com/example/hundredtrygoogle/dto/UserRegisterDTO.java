package com.example.hundredtrygoogle.dto;

import com.example.hundredtrygoogle.entities.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserRegisterDTO {

    private String username;
    private String email;
    private String password;

//    this is for OAuth2.0 Register
    private UUID providedId;
    private String providerName;
    private User userId;

}
