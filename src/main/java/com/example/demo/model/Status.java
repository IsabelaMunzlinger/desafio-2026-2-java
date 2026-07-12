package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean finalizaSolicitacao;

    public Status() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isFinalizaSolicitacao() { return finalizaSolicitacao; }
    public void setFinalizaSolicitacao(boolean finalizaSolicitacao) { this.finalizaSolicitacao = finalizaSolicitacao; }
}