package com.example.demo.service;

import com.example.demo.dto.AtualizarStatusDTO;
import com.example.demo.dto.NovaSolicitacaoDTO;
import com.example.demo.model.*;
import com.example.demo.model.enums.Prioridade;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final CursoRepository cursoRepository;
    private final DocumentoRepository documentoRepository;
    private final StatusRepository statusRepository;
    private final MatriculaRepository matriculaRepository;
    private final EtapaFluxoRepository etapaFluxoRepository;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              MovimentacaoRepository movimentacaoRepository,
                              CursoRepository cursoRepository,
                              DocumentoRepository documentoRepository,
                              StatusRepository statusRepository,
                              MatriculaRepository matriculaRepository,
                              EtapaFluxoRepository etapaFluxoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.cursoRepository = cursoRepository;
        this.documentoRepository = documentoRepository;
        this.statusRepository = statusRepository;
        this.matriculaRepository = matriculaRepository;
        this.etapaFluxoRepository = etapaFluxoRepository;
    }

    @Transactional
    public void criarSolicitacao(NovaSolicitacaoDTO dto, Usuario alunoLogado) {
        Matricula matricula = matriculaRepository.findFirstByAlunoIdAndCursoId(alunoLogado.getId(), dto.cursoId())
                .orElseThrow(() -> new RuntimeException("Você não possui nenhum vínculo com este curso."));

        if (!matricula.isAtivo()) {
            throw new RuntimeException("Seu vínculo com este curso está inativo. Você não pode solicitar documentos. Procure a secretaria.");
        }

        Curso curso = cursoRepository.findById(dto.cursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado!"));

        Documento documento = documentoRepository.findById(dto.documentoId())
                .orElseThrow(() -> new RuntimeException("Documento não encontrado!"));

        Status statusInicial;
        String observacaoInicial;

        List<EtapaFluxo> etapas = etapaFluxoRepository.findByTipoDocumentoIdOrderByOrdemAsc(documento.getId());

        // Excessão para o tipo de status cadastrado
        if (!etapas.isEmpty()) {
            statusInicial = statusRepository.findAll().stream()
                    .filter(s -> limparNomeStatus(s.getNome()).equals("ABERTA"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Status inicial 'Aberto' não encontrado no banco. O Administrador precisa cadastrá-lo."));

            observacaoInicial = "Solicitação iniciada. Aguardando primeira etapa do fluxo.";
        } else {
            statusInicial = statusRepository.findAll().stream()
                    .filter(s -> s.getNome().equalsIgnoreCase("Emitido"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Status 'Emitido' não configurado no banco. O Administrador precisa cadastrá-lo."));

            //Se não tem nenhum fluxo cadastrado, o sistema emite direto para o aluno
            observacaoInicial = "Documento de emissão imediata (sem fluxo de aprovação). Emitido automaticamente pelo sistema.";
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setAluno(alunoLogado);
        solicitacao.setCurso(curso);
        solicitacao.setTipo(documento);
        solicitacao.setStatus(statusInicial);
        solicitacao.setPrioridade(Prioridade.NORMAL);

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setSolicitacao(solicitacaoSalva);
        movimentacao.setStatus(statusInicial);
        movimentacao.setResponsavel(alunoLogado);
        movimentacao.setObservacao(observacaoInicial);

        //Salva na tabela de movimentação a alteração de status
        movimentacaoRepository.save(movimentacao);
    }

    public List<Solicitacao> buscarFilaDeTrabalho(Usuario usuarioLogado) {
        String perfilLogado = usuarioLogado.getPerfil().toString();
        List<Solicitacao> filaParaRetornar = new ArrayList<>();

        List<Solicitacao> todas = solicitacaoRepository.findAll();

        //Admin consegue verificar os pedidos
        if (perfilLogado.equalsIgnoreCase("ADMIN")) {
            filaParaRetornar.addAll(todas);
        } else {
            //Tira da fila de conferência se o pedido já foi finalizado, tem algum status vrdadeiro no isFinalizaSolicitcao
            for (Solicitacao sol : todas) {
                if (sol.getStatus() == null || sol.getStatus().isFinalizaSolicitacao()) {
                    continue;
                }

                List<EtapaFluxo> etapas = etapaFluxoRepository.findByTipoDocumentoIdOrderByOrdemAsc(sol.getTipo().getId());

                //Verifica onde que parou na sequência
                int etapaAtual = 0;
                List<Movimentacao> historico = movimentacaoRepository.findBySolicitacaoId(sol.getId());
                historico.sort(Comparator.comparing(Movimentacao::getId));

                for (Movimentacao mov : historico) {
                    if (etapaAtual < etapas.size()) {
                        String donoDaEtapa = etapas.get(etapaAtual).getPerfilResponsavel().name();
                        if (mov.getStatus().getNome().toLowerCase().contains("aprovad") &&
                                mov.getResponsavel().getPerfil().name().equalsIgnoreCase(donoDaEtapa)) {
                            etapaAtual++;
                        }
                    }
                }

                //Verifica qual perfil tem que assinar
                if (etapaAtual < etapas.size()) {
                    String donoDaVez = etapas.get(etapaAtual).getPerfilResponsavel().name();

                    if (donoDaVez.equalsIgnoreCase(perfilLogado)) {
                        filaParaRetornar.add(sol);
                    }
                }
            }
        }

        // Para mudar a prioridade do pedido conforme a quantidade de dias
        for (Solicitacao sol : filaParaRetornar) {
            try {
                if (sol.getDataSolicitacao() != null) {
                    long diasParado = ChronoUnit.DAYS.between(sol.getDataSolicitacao(), LocalDateTime.now());
                    if (diasParado > 5) sol.setPrioridade(Prioridade.URGENTE);
                    else if (diasParado > 2) sol.setPrioridade(Prioridade.ALTA);
                    else sol.setPrioridade(Prioridade.NORMAL);
                } else {
                    sol.setPrioridade(Prioridade.NORMAL);
                }
            } catch (Exception e) {
                sol.setPrioridade(Prioridade.NORMAL);
            }
        }
        return filaParaRetornar;
    }

    @Transactional
    public void atualizarStatus(Long solicitacaoId, AtualizarStatusDTO dto, Usuario usuarioLogado) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada."));

        Status novoStatus = statusRepository.findById(dto.novoStatusId())
                .orElseThrow(() -> new RuntimeException("Status de destino não encontrado."));

        //Impede que pule etapas obrigatórias
        validarTransicao(solicitacao.getStatus(), novoStatus);

        //Verifica se é o usuário logado
        Long idResponsavelInformado = dto.statusResponsavel() != null ? dto.statusResponsavel() : usuarioLogado.getId();

        if (!idResponsavelInformado.equals(usuarioLogado.getId())) {
            throw new RuntimeException("Acesso negado: O código do responsável informado é inválido ou não corresponde ao seu usuário.");
        }

        //Verifica em qual etapa está o processo de conferência
        String perfilLogado = usuarioLogado.getPerfil().toString();
        List<EtapaFluxo> etapas = etapaFluxoRepository.findByTipoDocumentoIdOrderByOrdemAsc(solicitacao.getTipo().getId());
        int etapaAtual = 0;
        List<Movimentacao> historico = movimentacaoRepository.findBySolicitacaoId(solicitacao.getId());
        historico.sort(Comparator.comparing(Movimentacao::getId));

        for (Movimentacao mov : historico) {
            if (etapaAtual < etapas.size()) {
                String donoDaEtapa = etapas.get(etapaAtual).getPerfilResponsavel().name();
                if (mov.getStatus().getNome().toLowerCase().contains("aprovad") &&
                        mov.getResponsavel().getPerfil().name().equalsIgnoreCase(donoDaEtapa)) {
                    etapaAtual++;
                }
            }
        }

        // Verifica o fluxo cadastrado, e se o perfil acessando é o que precisa, menos para o admin
        if (!etapas.isEmpty() && !perfilLogado.equalsIgnoreCase("ADMIN")) {
            if (etapaAtual < etapas.size()) {
                String donoDaVez = etapas.get(etapaAtual).getPerfilResponsavel().name();
                if (!perfilLogado.equalsIgnoreCase(donoDaVez)) {
                    throw new RuntimeException("Permissão negada. O documento está com o(a) " + donoDaVez);
                }
            }
        }

        Status statusFinalParaSalvar = novoStatus;
        String obsFinal = dto.observacao() != null ? dto.observacao() : "Status atualizado via fluxo.";

        //Muda para emitido se está na última etapa e o status foi aprovado
        if (!etapas.isEmpty() && novoStatus.getNome().toLowerCase().contains("aprovad")) {
            if ((etapaAtual + 1) >= etapas.size()) {
                statusFinalParaSalvar = statusRepository.findAll().stream()
                        .filter(s -> s.getNome().equalsIgnoreCase("Emitido"))
                        .findFirst()
                        .orElse(novoStatus);
                obsFinal = "Todas as etapas aprovadas. Documento emitido automaticamente!";
            }
        }

        solicitacao.setStatus(statusFinalParaSalvar);
        solicitacao.setDataAlteracao(LocalDateTime.now());
        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setSolicitacao(solicitacaoSalva);
        movimentacao.setStatus(statusFinalParaSalvar);
        movimentacao.setResponsavel(usuarioLogado);
        movimentacao.setObservacao(obsFinal);

        movimentacaoRepository.save(movimentacao);
    }


    private void validarTransicao(Status statusAtual, Status novoStatus) {
        boolean transicaoInvalida = false;

        String atual = limparNomeStatus(statusAtual.getNome());
        String destino = limparNomeStatus(novoStatus.getNome());

        // Obriga a colocar em analise quando chega e não aprovar direto
        if (atual.equals("ABERTA") || atual.contains("APROVAD")) {
            if (!destino.equals("EM_ANALISE")) {
                transicaoInvalida = true;
            }
        }
        // Se o documento já está em analise, ele pode aprovar ou usar qualquer status que finalize
        else if (atual.equals("EM_ANALISE")) {
            //Verifica o parametro isFinalizaSolicitacao do cadastro de status
            if (!destino.contains("APROVAD") && !novoStatus.isFinalizaSolicitacao()) {
                transicaoInvalida = true;
            }
        }
        // Status finais travam o documento
        else if (statusAtual.isFinalizaSolicitacao()) {
            transicaoInvalida = true;
        }

        if (transicaoInvalida) {
            throw new RuntimeException("Transição inválida! Você precisa colocar o documento 'Em Análise' antes de aprovar ou reprovar.");
        }
    }

    // Para padronizar o nome
    private String limparNomeStatus(String nome) {
        if (nome == null) return "";
        return Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replace(" ", "_");
    }
}