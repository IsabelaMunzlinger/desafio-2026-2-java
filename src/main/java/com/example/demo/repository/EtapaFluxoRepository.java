package com.example.demo.repository;

import com.example.demo.model.EtapaFluxo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EtapaFluxoRepository extends JpaRepository<EtapaFluxo, Long> {

    // Traz as etapas de um documento
    List<EtapaFluxo> findByTipoDocumentoIdOrderByOrdemAsc(Long tipoDocumentoId);

    // Busca a etapa exata de um documento baseada no status
    Optional<EtapaFluxo> findByTipoDocumentoIdAndStatusId(Long tipoDocumentoId, Long statusId);

    // Apaga todas as etapas de um documento
    @Modifying
    @Transactional
    @Query("DELETE FROM EtapaFluxo e WHERE e.tipoDocumento.id = :documentoId")
    void deleteByTipoDocumentoId(Long documentoId);
}