package com.example.demo.service;

import com.example.demo.dto.MatriculaDTO;
import com.example.demo.model.Curso;
import com.example.demo.model.Matricula;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            UsuarioRepository usuarioRepository,
                            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
    }

    public void matricular(MatriculaDTO dto) {

        Optional<Matricula> matriculaExistente = matriculaRepository
                .findFirstByAlunoIdAndCursoId(dto.alunoId(), dto.cursoId());

        if (matriculaExistente.isPresent()) {
            Matricula mat = matriculaExistente.get();
            String status = mat.isAtivo() ? "ATIVA" : "INATIVA";

            throw new RuntimeException("O aluno já possui uma matrícula " + status + " neste curso! Em vez de criar um novo vínculo, altere o status na tabela.");
        }

        Usuario aluno = usuarioRepository.findById(dto.alunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));

        Curso curso = cursoRepository.findById(dto.cursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado!"));

        Matricula novaMatricula = new Matricula();
        novaMatricula.setAluno(aluno);
        novaMatricula.setCurso(curso);
        novaMatricula.setAtivo(true);

        matriculaRepository.save(novaMatricula);
    }
}