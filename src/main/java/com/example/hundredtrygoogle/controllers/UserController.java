package com.example.hundredtrygoogle.controllers;

import com.example.hundredtrygoogle.dto.UpdateUserDTO;
import com.example.hundredtrygoogle.entities.User;
import com.example.hundredtrygoogle.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/updateMe")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO updateDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());

        User newUser = userService.update(user, updateDTO);

        return ResponseEntity.status(200).body(newUser);
    }

}
