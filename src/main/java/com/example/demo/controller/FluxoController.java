package com.example.demo.controller;

import com.example.demo.dto.FluxoDTO;
import com.example.demo.service.FluxoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fluxos")
public class FluxoController {

    private final FluxoService service;

    public FluxoController(FluxoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody FluxoDTO dados) {
        service.salvarFluxo(dados);
        return ResponseEntity.ok("Fluxo configurado com sucesso!");
    }
}