package com.kauadev.to_do_app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
import com.kauadev.to_do_app.domain.user.exceptions.UserNotFoundException;
import com.kauadev.to_do_app.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    public User updateUser(Integer id, UserDTO data) {
        Optional<User> user = this.userRepository.findById(id);

        if (!user.isPresent())
            throw new UserNotFoundException();

        user.get().setUsername(data.username());

        // temporario. ainda n tá sendo feito o hash.
        user.get().setPassword(data.password());

        user.get().setRole(data.role());

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
