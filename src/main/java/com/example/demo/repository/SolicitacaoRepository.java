package com.example.demo.repository;

import com.example.demo.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    // Traz todos os pedidos que o aluno fez
    List<Solicitacao> findByAlunoId(Long alunoId);

    // Traz os pedidos de um status específico
    List<Solicitacao> findByStatusId(Long statusId);

    // Traz pedidos de vários status ao mesmo tempo
    List<Solicitacao> findByStatusIdIn(List<Long> statusIds);

    // Traz as solicitações ignorando os status finalizados
    List<Solicitacao> findByStatusIdNotIn(List<Long> statusIds);

    //Agrupa pelo tipo de status para o dashboard
    @Query("SELECT s.status.nome, COUNT(s) FROM Solicitacao s GROUP BY s.status.nome")
    List<Object[]> contarSolicitacoesAgrupadasPorStatus();

}