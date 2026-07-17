package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean finalizaSolicitacao;

    public Status() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isFinalizaSolicitacao() { return finalizaSolicitacao; }
    public void setFinalizaSolicitacao(boolean finalizaSolicitacao) { this.finalizaSolicitacao = finalizaSolicitacao; }
}