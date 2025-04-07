package com.kauadev.to_do_app.services;

import java.util.List;
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
        User user = this.userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        return user;
    }

    public User updateUser(UserDTO data) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = (User) auth.getPrincipal();

        User user = this.userRepository.findById(loggedUser.getId())
                .orElseThrow(UserNotFoundException::new);

        String encodedPassword = this.passwordEncoder.encode(data.password());
        user.setUsername(data.username());
        user.setPassword(encodedPassword);

        this.userRepository.save(user);

        return user;
    }

    public String deleteUser(Integer id) {

        if (id == null)
            throw new UserNotFoundException();

        User user = this.userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        this.userRepository.delete(user);

        return "user with id " + id + " deleted.";
    }
}
