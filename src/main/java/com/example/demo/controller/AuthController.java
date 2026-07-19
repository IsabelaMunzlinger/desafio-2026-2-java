package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import com.example.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO dados) {
        // validar token
        String token = authService.autenticar(dados.login(), dados.senha());

        // Devolve o Token gerado
        return ResponseEntity.ok(token);
    }
}
