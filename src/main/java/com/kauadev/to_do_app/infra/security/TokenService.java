package com.kauadev.to_do_app.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kauadev.to_do_app.domain.user.User;

// aqui, no token service, fica a lógica de geração de token, validação e de geração do seu tempo de expiração.

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        // cria um algoritmo de assinatura usando minha cahve secret
        // ou seja, vai usar esse algoritmo para assinar e validar o token
        // uma camada a mais (essencial) de segurança
        Algorithm algorithm = Algorithm.HMAC256(this.secret);

        // criação dele é simples, passamos algumas configzinhas a mais
        // pra deixar tudo certinho.

        return JWT.create()
                .withIssuer("to-do-api") // qualquer nome aqui, so pra identificar
                .withSubject(user.getUsername()) // identifica o usuário
                .withExpiresAt(this.genExpirationInstant()) // quando vai
                                                            // expirar
                .sign(algorithm); // assina o token e gera.
    }

    public String validateTokenAndReturnSubject(String token) {
        Algorithm algorithm = Algorithm.HMAC256(this.secret);

        return JWT.require(algorithm)
                .withIssuer("to-do-api") // mesmo issuer
                .build() // builda
                .verify(token) // verifica o token
                .getSubject(); // retorna o subject com base no token

    }

    private Instant genExpirationInstant() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
