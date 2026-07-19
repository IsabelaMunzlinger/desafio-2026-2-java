package com.example.demo.service;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.model.enums.Perfil;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Para admin cadastrar usuarios
    public Usuario cadastrarUsuario(Usuario usuario, Perfil perfilAtribuido) {

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está cadastrado!");
        }

        // Atribui o perfil e criptografa a senha antes de salvar
        usuario.setPerfil(perfilAtribuido);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponseDTO(
                        u.getId(),
                        u.getNome(),
                        u.getEmail(),
                        u.getPerfil()
                ))
                .collect(Collectors.toList());
    }
}