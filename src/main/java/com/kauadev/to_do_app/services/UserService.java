package com.kauadev.to_do_app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
import com.kauadev.to_do_app.domain.user.exceptions.UserNotFoundException;
import com.kauadev.to_do_app.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        List<User> users = this.userRepository.findAll();

        return users;
    }

    public User getUser(Integer id) {
        Optional<User> user = this.userRepository.findById(id);

        if (!user.isPresent())
            throw new UserNotFoundException();

        return user.get();
    }

    public User updateUser(UserDTO data) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = (User) auth.getPrincipal();

        Optional<User> user = this.userRepository.findById(loggedUser.getId());

        if (!user.isPresent())
            throw new UserNotFoundException();

        String encodedPassword = this.passwordEncoder.encode(data.password());
        user.get().setUsername(data.username());
        user.get().setPassword(encodedPassword);

        this.userRepository.save(user.get());

        return user.get();
    }

    public String deleteUser(Integer id) {

        if (id == null)
            throw new UserNotFoundException();

        Optional<User> user = this.userRepository.findById(id);

        this.userRepository.delete(user.get());

        return "user with id " + id + " deleted.";
    }
}
