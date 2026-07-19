package com.example.demo.service;

import com.example.demo.dto.FluxoDTO;
import com.example.demo.model.Documento;
import com.example.demo.model.EtapaFluxo;
import com.example.demo.model.Status;
import com.example.demo.model.enums.Perfil;
import com.example.demo.repository.DocumentoRepository;
import com.example.demo.repository.EtapaFluxoRepository;
import com.example.demo.repository.StatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FluxoService {

    private final EtapaFluxoRepository etapaFluxoRepository;
    private final DocumentoRepository documentoRepository;
    private final StatusRepository statusRepository;

    public FluxoService(EtapaFluxoRepository etapaFluxoRepository,
                        DocumentoRepository documentoRepository,
                        StatusRepository statusRepository) {
        this.etapaFluxoRepository = etapaFluxoRepository;
        this.documentoRepository = documentoRepository;
        this.statusRepository = statusRepository;
    }

    @Transactional
    public void salvarFluxo(FluxoDTO dto) {
        Documento documento = documentoRepository.findById(dto.documentoId())
                .orElseThrow(() -> new RuntimeException("Documento não encontrado!"));

        etapaFluxoRepository.deleteByTipoDocumentoId(documento.getId());

        Status statusPadrao = statusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Status ID 1 não encontrado! Cadastre um status inicial no banco."));

        int ordem = 1;
        for (String nomePerfil : dto.etapas()) {
            EtapaFluxo etapa = new EtapaFluxo();
            etapa.setTipoDocumento(documento);
            etapa.setOrdem(ordem);
            etapa.setPerfilResponsavel(Perfil.valueOf(nomePerfil.toUpperCase()));

            etapa.setStatus(statusPadrao);

            etapaFluxoRepository.save(etapa);
            ordem++;
        }
    }
}