package com.kauadev.to_do_app.infra.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kauadev.to_do_app.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // component. um tipo de bean
// filtro que será aplicado uma vez a cada requisição.
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;

    // neste arquivo geralmente é concentrado a lógica de validação do token (a cada
    // requisiçao)
    // e recuperação do token

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = this.recoverToken(request);
        if (token != null) {

            String usernameSubject = this.tokenService.validateTokenAndReturnSubject(token);
            UserDetails userDetails = this.userRepository.findByUsername(usernameSubject);

            // autenticação. o usuário ja foi autenticado pelo token

            // password como null pois já temos o token.
            // passamos os authorities pra definir o que o usuário pode fazer.
            var authentication = new UsernamePasswordAuthenticationToken(usernameSubject, null,
                    userDetails.getAuthorities());

            // define no contexto da aplicação.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response); // vá pro próximo filtro da lista.
    }

    private String recoverToken(HttpServletRequest request) {
        // cabeçalho de autorizaçao. onde geralmente fica o token
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null)
            return null;

        // cabeçalhos com o token geralmente tem o prefixo Bearer
        // aqui só estamos retirando ele e deixando apenas o token.
        String token = authHeader.replace("Bearer", " ");

        return token;
    }
}
