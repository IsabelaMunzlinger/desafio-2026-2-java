package com.example.demo.dto;
import com.example.demo.model.enums.Perfil;

public record UsuarioResponseDTO(Long id, String nome, String email, Perfil perfil) {}