package com.kauadev.to_do_app.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// aqui definimos a nossa configuração em como será o comportamento do spring security

// uso de filtros, config do authenticationManager, criptografia de senhas

// tudo nesse arquivo é chamado por baixo dos panos.

@Configuration // define como classe de configuração
@EnableWebSecurity // ativa config do security web
public class SecurityConfiguration {

    @Autowired
    private SecurityFilter securityFilter;

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
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/user/delete").hasRole("ADMIN")
                        .anyRequest().authenticated()) // todas rotas deve estar autenticado.
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // qual filtro, e antes do
                                                                                             // que ele irá ocorrer
                .build(); // builda e retorna um SecurityFilterChain
    }

    // configurando o authenticationManager, que gerencia o processo de autenticação

    // É um bean, logo será um objeto gerenciado e armazenado pelo Spring, por baixo
    // dos panos.
    // sempre que precisar de um authenticationManager em algum lugar pra ser
    // injetado, o spring vem e pega daqui a instância.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // retorna uma nova instancia do decodificador, por baixo dos panos.
    // ou seja, o Spring vai automaticamente usar esse encoder sempre que precisar
    // criptografar ou comparar senhas, devido a annotation @Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }
}
