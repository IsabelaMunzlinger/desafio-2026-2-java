package com.example.demo.controller;

import com.example.demo.dto.CursoDTO;
import com.example.demo.model.Curso;
import com.example.demo.model.Matricula;
import com.example.demo.model.Usuario;
import com.example.demo.service.CursoService;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.MatriculaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService service;
    private final CursoRepository repository;
    private final MatriculaRepository matriculaRepository;

    public CursoController(CursoService service, CursoRepository repository, MatriculaRepository matriculaRepository) {
        this.service = service;
        this.repository = repository;
        this.matriculaRepository = matriculaRepository;
    }

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody CursoDTO dados) {
        service.salvar(dados);
        return ResponseEntity.ok("Curso salvo com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable Long id, @RequestBody CursoDTO dados) {
        service.atualizar(id, dados);
        return ResponseEntity.ok("Curso atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok("Curso excluído com sucesso!");
    }

    //Listar os cursos
    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    //Trazer os cursos do aluno logado
    @GetMapping("/meus-cursos")
    public ResponseEntity<List<Curso>> buscarMeusCursos(@AuthenticationPrincipal Usuario alunoLogado) {
        // matriculas do aluno, tanto ativa quanto inativa
        List<Matricula> matriculas = matriculaRepository.findByAlunoId(alunoLogado.getId());
        List<Curso> meusCursos = matriculas.stream()
                .filter(Matricula::isAtivo)
                .map(Matricula::getCurso)
                .collect(Collectors.toList());

        return ResponseEntity.ok(meusCursos);
    }
}