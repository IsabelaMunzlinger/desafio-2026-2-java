package com.example.demo.dto;

import java.util.List;

public record FluxoDTO(
        Long documentoId,
        List<String> etapas
) {}