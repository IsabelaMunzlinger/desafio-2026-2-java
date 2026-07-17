package com.example.demo.dto;
import com.example.demo.model.enums.Perfil;

public record NovoUsuarioDTO(String nome, String email, String senha, Perfil perfil) {}