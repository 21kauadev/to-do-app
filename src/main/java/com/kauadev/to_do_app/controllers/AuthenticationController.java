package com.kauadev.to_do_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
import com.kauadev.to_do_app.repositories.UserRepository;

@RestController("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    // processo padrão de CRUD de usuários, com algumas etapas adicionais:

    // 1 - Checa se o usuário já não existe pelo username
    // 2 - Criptografa a senha do usuário
    // 3 - Salva o usuário com a senha criptografada no banco de dados.
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO data) {
        if (this.userRepository.findByUsername(data.username()) != null) {
            return ResponseEntity.badRequest().body("Usuário já está registrado.");
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(data.password());

        User user = new User(data.username(), encodedPassword, data.role());

        // salva o usuário com senha criptografada no db.
        this.userRepository.save(user);

        return ResponseEntity.ok().body("Usuário registrado!");
    }

}
