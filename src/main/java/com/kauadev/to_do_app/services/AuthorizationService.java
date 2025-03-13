package com.kauadev.to_do_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.repositories.UserRepository;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
}
