package com.example.demo.controller;

import com.example.demo.dto.MatriculaDTO;
import com.example.demo.model.Matricula;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.service.MatriculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService service;
    private final MatriculaRepository matriculaRepository;

    public MatriculaController(MatriculaService service, MatriculaRepository matriculaRepository) {
        this.service = service;
        this.matriculaRepository = matriculaRepository;
    }

    @PostMapping
    public ResponseEntity matricular(@RequestBody MatriculaDTO dto) {
        try {
            service.matricular(dto);
            return ResponseEntity.ok("Matrícula realizada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public ResponseEntity<List<Matricula>> listarTodas() {
        return ResponseEntity.ok(matriculaRepository.findAll());
    }

    //Alterar o status da matricula do aluno
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public ResponseEntity<String> alternarStatus(@PathVariable Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada."));

        matricula.setAtivo(!matricula.isAtivo());
        matriculaRepository.save(matricula);

        String mensagem = matricula.isAtivo() ? "Matrícula ativada com sucesso!" : "Matrícula inativada com sucesso!";
        return ResponseEntity.ok(mensagem);
    }
}