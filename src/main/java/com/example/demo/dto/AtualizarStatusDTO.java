package com.example.demo.dto;

public record AtualizarStatusDTO(
        Long novoStatusId,
        Long statusResponsavel,
        String observacao
) {}