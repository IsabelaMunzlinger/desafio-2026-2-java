package com.example.demo.service;

import com.example.demo.dto.DocumentoDTO;
import com.example.demo.model.Documento;
import com.example.demo.repository.DocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoService {

    private final DocumentoRepository repository;

    public DocumentoService(DocumentoRepository repository) {
        this.repository = repository;
    }

    public Documento salvar(DocumentoDTO dto) {
        Documento doc = new Documento();
        doc.setNome(dto.nome());
        return repository.save(doc);
    }

    public List<Documento> listarTodos() {
        return repository.findAll();
    }
}