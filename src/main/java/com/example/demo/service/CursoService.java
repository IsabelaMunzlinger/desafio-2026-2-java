package com.example.demo.service;

import com.example.demo.dto.CursoDTO;
import com.example.demo.model.Curso;
import com.example.demo.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {

    private final CursoRepository repository;

    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void salvar(CursoDTO dados) {
        repository.save(new Curso(dados));
    }

    @Transactional
    public void atualizar(Long id, CursoDTO dados) {
        var curso = repository.getReferenceById(id);
        curso.atualizarInformacoes(dados);
    }

    @Transactional
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}