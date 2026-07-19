package com.example.demo.repository;

import com.example.demo.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByAlunoIdAndCursoIdAndAtivoTrue(Long alunoId, Long cursoId);

    boolean existsByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    List<Matricula> findByAlunoId(Long alunoId);

    Optional<Matricula> findFirstByAlunoIdAndCursoId(Long alunoId, Long cursoId);

}