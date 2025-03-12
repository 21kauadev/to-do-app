package com.kauadev.to_do_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.kauadev.to_do_app.domain.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    UserDetails findByUsername(String username); // será usado pelo spring security nas consultas
}
