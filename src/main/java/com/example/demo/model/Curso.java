package com.example.demo.model;

import com.example.demo.dto.CursoDTO;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

// Cursos cadastrados

@Entity
@Audited
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    public Curso() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Curso(CursoDTO dados) {
        this.nome = dados.nome();
    }

    public void atualizarInformacoes(CursoDTO dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
    }
}