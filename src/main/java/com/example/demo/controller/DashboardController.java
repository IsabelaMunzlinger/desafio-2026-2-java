package com.example.demo.controller;

import com.example.demo.model.Status;
import com.example.demo.repository.SolicitacaoRepository;
import com.example.demo.repository.StatusRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final SolicitacaoRepository solicitacaoRepository;
    private final StatusRepository statusRepository;

    public DashboardController(SolicitacaoRepository solicitacaoRepository, StatusRepository statusRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.statusRepository = statusRepository;
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> obterEstatisticas() {
        List<Object[]> resultados = solicitacaoRepository.contarSolicitacoesAgrupadasPorStatus();
        Map<String, Long> estatisticas = new HashMap<>();

        //traz todos os status cadastrados
        List<Status> todosStatus = statusRepository.findAll();
        for (Status status : todosStatus) {
            estatisticas.put(status.getNome(), 0L);
        }

        //Preenche os pedidos com a quantidade de cada status
        for (Object[] linha : resultados) {
            String nomeStatus = (String) linha[0];
            Long quantidade = (Long) linha[1];
            estatisticas.put(nomeStatus, quantidade);
        }

        return ResponseEntity.ok(estatisticas);
    }
}