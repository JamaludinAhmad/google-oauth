package com.example.hundredtrygoogle.filters;

import com.example.hundredtrygoogle.entities.User;
import com.example.hundredtrygoogle.services.UserService;
import com.example.hundredtrygoogle.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if( authHeader == null){
            filterChain.doFilter(request, response);
            return;
        }

        try{

            final String token = authHeader.substring(7);
            final String email = jwtUtil.extractUsername(token);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null && email != null){
                UserDetails userDetails = userService.loadUserByUsername(email);

                if(jwtUtil.isAccessTokenValid(token, (User) userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                }

            }

            filterChain.doFilter(request, response);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
