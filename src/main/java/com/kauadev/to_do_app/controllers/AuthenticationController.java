package com.kauadev.to_do_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
import com.kauadev.to_do_app.infra.security.TokenService;
import com.kauadev.to_do_app.repositories.UserRepository;

@RequestMapping("/auth")
@RestController()
public class AuthenticationController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    // processo padrão de CRUD de usuários, com algumas etapas adicionais:

    // 1 - Checa se o usuário já não existe pelo username
    // 2 - Criptografa a senha do usuário
    // 3 - Salva o usuário com a senha criptografada no banco de dados.
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO data) {
        if (this.userRepository.findByUsername(data.username()) != null) {
            return ResponseEntity.badRequest().body("Usuário já está registrado.");
        }

        String encodedPassword = passwordEncoder.encode(data.password());

        User user = new User(data.username(), encodedPassword, data.role());

        // salva o usuário com senha criptografada no db.
        this.userRepository.save(user);

        return ResponseEntity.ok().body("Usuário registrado!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO data) {

        // usernamePassowrd e authenticationManager responsáveis pela autenticação:

        // 1- usernamePassword cria um objeto com ambos dados
        // 2- authenticationManager verifica se as credenciais batem, estão corretas
        // 3- se sim, o usuário é autenticado
        // 4- a partir daí podemos gerar o token com o objeto do usuário retornado

        // o spring security faz isso por baixo dos panos, desde que já haja Beans
        // retornando uma instancia
        // do authenticaitonManager e do decodificador de senha (nesse caso,
        // BCryptPasswordEncoder)
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(data.username(),
                data.password());

        // passa o objeto usernamePassword de argumento pro método authenticate, que só
        // confirma
        var authenticationManager = this.authenticationManager.authenticate(usernamePassword);

        // authManager.getPrincipal() retornaria o objeto User, permitindo, assim, que o
        // token possa ser gerado com base nesse usuario autenticado.

        String token = this.tokenService.generateToken((User) authenticationManager.getPrincipal());

        return ResponseEntity.ok().body(token);
    }

}
