package com.example.demo.repository;

import com.example.demo.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    List<Movimentacao> findBySolicitacaoId(Long solicitacaoId);
}
