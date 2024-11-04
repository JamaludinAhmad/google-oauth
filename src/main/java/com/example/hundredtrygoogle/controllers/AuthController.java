package com.example.hundredtrygoogle.controllers;

import com.example.hundredtrygoogle.dto.RefreshTokenDTO;
import com.example.hundredtrygoogle.dto.UserLoginDTO;
import com.example.hundredtrygoogle.dto.UserRegisterDTO;
import com.example.hundredtrygoogle.entities.User;
import com.example.hundredtrygoogle.repositories.UserRepository;
import com.example.hundredtrygoogle.services.CustomOAuth2Service;
import com.example.hundredtrygoogle.services.UserService;
import com.example.hundredtrygoogle.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, CustomOAuth2Service customOAuth2Service, CustomOAuth2Service customOAuth2Service1) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO){
        User user = userService.register(userRegisterDTO);

        return ResponseEntity.status(200).body(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody  UserLoginDTO userLoginDTO){
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return this.getToken();
    }

    @GetMapping
    public ResponseEntity<?> index(){
        return ResponseEntity.status(200).body("EA");
    }

    @GetMapping("/auth/token")
    public ResponseEntity<?> getToken(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated()){
            return ResponseEntity.status(403).body("not authenticatd");
        }

        UserDetails userDetails = userService.loadUserByUsername(auth.getName());

        Map<String, Object> resp = new HashMap<>();

        resp.put("access_token", jwtUtil.generateAccessToken((User) userDetails));
        resp.put("refresh_token", jwtUtil.generateRefreshToken((User) userDetails));

        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("/auth/refresh_token")
    public ResponseEntity<?> getAccessToken(@RequestBody RefreshTokenDTO refreshTokenDTO){
        if(refreshTokenDTO == null){
            return ResponseEntity.status(403).body("refresh token provided");
        }

        if(jwtUtil.isRefreshTokenExpired(refreshTokenDTO.getRefreshToken())){
            return ResponseEntity.status(403).body("refresh token expired");
        }


        Map<String, Object> resp = new HashMap<>();
        String username = jwtUtil.extractRefreshUsername(refreshTokenDTO.getRefreshToken());
        User user = (User) userService.loadUserByUsername(username);

        if(!jwtUtil.isRefreshTokenValid(refreshTokenDTO.getRefreshToken(), user)){
            return ResponseEntity.status(403).body("token not valid");
        }

        resp.put("access_token", jwtUtil.generateAccessToken(user));

        return ResponseEntity.status(200).body(resp);

    }


}
