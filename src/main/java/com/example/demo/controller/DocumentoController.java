package com.example.demo.controller;

import com.example.demo.dto.DocumentoDTO;
import com.example.demo.dto.DocumentoResponseDTO;
import com.example.demo.service.DocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody DocumentoDTO dados) {
        service.salvar(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tipo de documento cadastrado com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listar() {
        List<DocumentoResponseDTO> documentos = service.listarTodos()
                .stream()
                .map(doc -> new DocumentoResponseDTO(doc.getId(), doc.getNome()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(documentos);
    }
}