package com.example.demo.controller;

import com.example.demo.dto.AtualizarStatusDTO;
import com.example.demo.dto.NovaSolicitacaoDTO;
import com.example.demo.model.Solicitacao;
import com.example.demo.model.Usuario;
import com.example.demo.repository.SolicitacaoRepository;
import com.example.demo.service.RelatorioService;
import com.example.demo.service.SolicitacaoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final RelatorioService relatorioService;
    private final SolicitacaoRepository solicitacaoRepository;
    private final com.example.demo.repository.MovimentacaoRepository movimentacaoRepository;

    public SolicitacaoController(SolicitacaoService solicitacaoService,
                                 RelatorioService relatorioService,
                                 SolicitacaoRepository solicitacaoRepository,
                                 com.example.demo.repository.MovimentacaoRepository movimentacaoRepository) {
        this.solicitacaoService = solicitacaoService;
        this.relatorioService = relatorioService;
        this.solicitacaoRepository = solicitacaoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @PostMapping
    public ResponseEntity<String> solicitar(
            @RequestBody NovaSolicitacaoDTO dto,
            @AuthenticationPrincipal Usuario alunoLogado
    ) {
        solicitacaoService.criarSolicitacao(dto, alunoLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body("Documento solicitado com sucesso!");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        try {
            solicitacaoService.atualizarStatus(id, dto, usuarioLogado);
            return ResponseEntity.ok("Status atualizado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Mostrar documentos que precisam de conferência
    @GetMapping("/fila-trabalho")
    public ResponseEntity<List<Solicitacao>> verFilaDeTrabalho(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Solicitacao> fila = solicitacaoService.buscarFilaDeTrabalho(usuarioLogado);
        return ResponseEntity.ok(fila);
    }

    //Listar todos os pedidos do aluno
    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<Solicitacao>> buscarMeusPedidos(@AuthenticationPrincipal Usuario alunoLogado) {
        List<Solicitacao> pedidos = solicitacaoRepository.findByAlunoId(alunoLogado.getId());
        return ResponseEntity.ok(pedidos);
    }

    //Download dos docs na tela do aluno
    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> baixarDocumento(@PathVariable Long id, @AuthenticationPrincipal Usuario alunoLogado) {

        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada."));

        if (!solicitacao.getAluno().getId().equals(alunoLogado.getId())) {
            throw new RuntimeException("Acesso negado. Este documento pertence a outro aluno.");
        }
        if (!solicitacao.getStatus().getNome().equalsIgnoreCase("Emitido")) {
            throw new RuntimeException("O documento ainda não foi emitido.");
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nomeDocumento", solicitacao.getTipo().getNome());
        parametros.put("nomeAluno", alunoLogado.getNome());
        parametros.put("nomeCurso", solicitacao.getCurso().getNome());

        byte[] relatorioPdf = relatorioService.gerarPdf("base", parametros);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "documento_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(relatorioPdf);
    }

    //Ver histórico de movimentações do pedido do aluno
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<Map<String, Object>>> verHistorico(@PathVariable Long id) {

        List<com.example.demo.model.Movimentacao> historico = movimentacaoRepository.findBySolicitacaoId(id);
        historico.sort(java.util.Comparator.comparing(com.example.demo.model.Movimentacao::getId));

        List<Map<String, Object>> historicoLimpo = historico.stream().map(m -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("status", m.getStatus().getNome());
            dto.put("responsavel", m.getResponsavel().getNome());
            dto.put("perfil", m.getResponsavel().getPerfil().name());
            dto.put("observacao", m.getObservacao());
            dto.put("data", m.getDataMovimentacao() != null ? m.getDataMovimentacao().toString() : "Data Indisponível");
            return dto;
        }).toList();
        return ResponseEntity.ok(historicoLimpo);
    }
}