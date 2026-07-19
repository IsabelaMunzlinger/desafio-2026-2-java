package com.example.demo.controller;

import com.example.demo.model.Status;
import com.example.demo.model.Usuario;
import com.example.demo.model.enums.Perfil;
import com.example.demo.repository.StatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final StatusRepository statusRepository;

    public StatusController(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    // Traz os status permitidos para cada tipo de perfil
    @GetMapping("/permitidos")
    public ResponseEntity<List<Status>> listarStatusPermitidos(@AuthenticationPrincipal Usuario usuarioLogado) {

        Perfil perfilDoUsuario = usuarioLogado.getPerfil();

        if (perfilDoUsuario == Perfil.ADMIN) {
            return ResponseEntity.ok(statusRepository.findAll());
        }
        List<Status> statusFiltrados = statusRepository.findByPerfilPermitidoIsNullOrPerfilPermitido(perfilDoUsuario);
        return ResponseEntity.ok(statusFiltrados);
    }


    // Lista todos os status no dashboard
    @GetMapping
    public ResponseEntity<List<Status>> listarTodos() {
        return ResponseEntity.ok(statusRepository.findAll());
    }

    // Cadastra um novo status
    @PostMapping
    public ResponseEntity<Status> criar(@RequestBody Status status) {
        Status salvo = statusRepository.save(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Deleta um status
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        statusRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}