package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {

    private final SecurityFilter securityFilter;

    public SecurityConfigurations(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {

                    // Libera somente a visualização das telas
                    req.requestMatchers(HttpMethod.GET,
                            "/",
                            "/login",
                            "/menu",
                            "/cadastro-cursos",
                            "/cadastro-usuarios",
                            "/cadastro-documentos",
                            "/cadastro-fluxos",
                            "/cadastro-matriculas",
                            "/solicitar-documento",
                            "/fila-trabalho",
                            "/cadastro-status",
                            "/meus-pedidos",
                            "/historico-pedido",
                            "/error",
                            "/*.html",
                            "/*.js",
                            "/css/**",
                            "/img/**",
                            "/favicon.ico"
                    ).permitAll();

                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();

                    // Cursos
                    req.requestMatchers(HttpMethod.POST, "/api/cursos").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/cursos/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/cursos/**").hasRole("ADMIN");

                    // Usuários
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN");

                    // Documentos
                    req.requestMatchers(HttpMethod.POST, "/api/documentos").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/documentos/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/documentos/**").hasRole("ADMIN");

                    // Cadastro de fluxo
                    req.requestMatchers(HttpMethod.POST, "/api/fluxos").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/fluxos/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/fluxos/**").hasRole("ADMIN");

                    // Matrículas
                    req.requestMatchers(HttpMethod.POST, "/api/matriculas").hasAnyRole("ADMIN", "SECRETARIA");
                    req.requestMatchers(HttpMethod.PUT, "/api/matriculas/**").hasAnyRole("ADMIN", "SECRETARIA");
                    req.requestMatchers(HttpMethod.DELETE, "/api/matriculas/**").hasAnyRole("ADMIN", "SECRETARIA");

                    // Pedido de documentos
                    req.requestMatchers(HttpMethod.POST, "/api/solicitacoes").hasRole("ALUNO");
                    req.requestMatchers(HttpMethod.GET, "/api/solicitacoes").hasRole("ALUNO");

                    // Cadastro de status
                    req.requestMatchers(HttpMethod.GET, "/api/status").authenticated();
                    req.requestMatchers(HttpMethod.POST, "/api/status").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/status/**").hasRole("ADMIN");

                    //  Bloqueia o resto
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}