package com.example.hundredtrygoogle.services;

import com.example.hundredtrygoogle.dto.UpdateUserDTO;
import com.example.hundredtrygoogle.dto.UserRegisterDTO;
import com.example.hundredtrygoogle.entities.User;
import com.example.hundredtrygoogle.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("Data tidak ditemukan"));
    }

    public User register(UserRegisterDTO userRegisterDTO){
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setEmail(userRegisterDTO.getEmail());

        String hash = bCryptPasswordEncoder.encode(userRegisterDTO.getPassword());
        user.setHashPassword(hash);

        return userRepository.save(user);
    }

    public User update(User user, UpdateUserDTO updateUserDTO){
        String newPassword = bCryptPasswordEncoder.encode(updateUserDTO.getPassword());

        user.setUsername(updateUserDTO.getUsername());
        user.setHashPassword(newPassword);

        return userRepository.save(user);

    }
}
