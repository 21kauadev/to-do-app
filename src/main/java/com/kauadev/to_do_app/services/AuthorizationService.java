package com.kauadev.to_do_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.repositories.UserRepository;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // para permtir que o Spring Security busque internamente toda vez que uma
    // AUTENTICAÇÃO for necessária.
    public UserDetails loadUserByUsername(String username) {

        if (username == null)
            throw new UsernameNotFoundException("Usuário não encontrado.");

        return this.userRepository.findByUsername(username);
    }
}
