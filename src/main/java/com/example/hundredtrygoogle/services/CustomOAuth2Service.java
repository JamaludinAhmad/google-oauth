package com.example.hundredtrygoogle.services;

import com.example.hundredtrygoogle.dto.UserRegisterDTO;
import com.example.hundredtrygoogle.entities.LoginProvider;
import com.example.hundredtrygoogle.entities.User;
import com.example.hundredtrygoogle.repositories.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User userInfo = super.loadUser(userRequest);
        return processOAuth(userInfo, userRequest);
    }

    public OAuth2User processOAuth(OAuth2User userInfo, OAuth2UserRequest userRequest){
        String email = userInfo.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = new User();
        if(userOptional.isEmpty()){
            user = registerUser(userInfo, userRequest);
        }
        user = userOptional.get();
        return user;

    }

    public User registerUser(OAuth2User userInfo, OAuth2UserRequest userRequest){
        User user = new User();
        user.setUsername(userInfo.getAttribute("name"));
        user.setEmail(userInfo.getAttribute("email"));

        LoginProvider loginProvider = new LoginProvider();
        loginProvider.setProviderName(userRequest.getClientRegistration().getRegistrationId());
        loginProvider.setProviderUserId(userInfo.getAttribute("sub"));

        List<LoginProvider> prov = new ArrayList<>();
        prov.add(loginProvider);

        user.setProvider(prov);
        userRepository.save(user);

        return user;
    }

}
