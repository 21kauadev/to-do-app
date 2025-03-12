package com.kauadev.to_do_app.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// aqui definimos a nossa configuração em como será o comportamento do spring security

// uso de filtros, config do authenticationManager, criptografia de senhas

// tudo nesse arquivo é chamado por baixo dos panos.

@Configuration // define como classe de configuração
@EnableWebSecurity // ativa config do security web
public class SecurityConfiguration {

    @Bean // -> spring consegue identificar e instanciar a classe
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // com o httpSecurity fazemos as configurações da aplicação
        // que rotas autorizar
        // tipo de estado da aplicação e etc.

        // desabilitamos o csrf pois no contexto de applicaçoes stateless (jwt, sem
        // estado e cookies) o csrf não é uma ameçaa.

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()) // todas rotas deve estar autenticado.
                .build(); // builda e retorna um SecurityFilterChain
    }

}
