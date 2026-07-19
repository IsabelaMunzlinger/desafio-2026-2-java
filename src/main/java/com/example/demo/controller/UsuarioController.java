package com.example.demo.controller;

import com.example.demo.dto.NovoUsuarioDTO;
import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity cadastrar(@RequestBody NovoUsuarioDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(dto.senha());

        Usuario salvo = usuarioService.cadastrarUsuario(novoUsuario, dto.perfil());
        UsuarioResponseDTO response = new UsuarioResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getPerfil()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // lista apenas uuários com permissão de aluno
    @GetMapping("/alunos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarSomenteAlunos() {
        List<Usuario> todosUsuarios = usuarioRepository.findAll();

        List<UsuarioResponseDTO> apenasAlunos = todosUsuarios.stream()
                .filter(u -> u.getPerfil() != null && u.getPerfil().name().equalsIgnoreCase("ALUNO"))
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getPerfil()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(apenasAlunos);
    }
}