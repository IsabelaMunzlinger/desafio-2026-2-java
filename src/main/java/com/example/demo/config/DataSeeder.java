package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.model.enums.Perfil;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final StatusRepository statusRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DocumentoRepository documentoRepository;
    private final EtapaFluxoRepository etapaFluxoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(StatusRepository statusRepository,
                      CursoRepository cursoRepository,
                      MatriculaRepository matriculaRepository,
                      UsuarioRepository usuarioRepository,
                      DocumentoRepository documentoRepository,
                      EtapaFluxoRepository etapaFluxoRepository,
                      PasswordEncoder passwordEncoder) {
        this.statusRepository = statusRepository;
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.documentoRepository = documentoRepository;
        this.etapaFluxoRepository = etapaFluxoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        //Status
        if (statusRepository.count() == 0) {
            Status aberto = new Status(); aberto.setNome("Aberta"); aberto.setFinalizaSolicitacao(false);
            Status emAnalise = new Status(); emAnalise.setNome("Em Análise"); emAnalise.setFinalizaSolicitacao(false);
            Status emitido = new Status(); emitido.setNome("Emitido"); emitido.setFinalizaSolicitacao(true);
            Status reprovado = new Status(); reprovado.setNome("Reprovado"); reprovado.setFinalizaSolicitacao(true);

            statusRepository.saveAll(Arrays.asList(aberto, emAnalise, emitido, reprovado));
        }

        // Usuários
        if (usuarioRepository.count() == 0) {
            String senhaPadrao = passwordEncoder.encode("1234");

            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@gmail.com");
            admin.setSenha(senhaPadrao);
            admin.setPerfil(Perfil.ADMIN);

            Usuario secretaria = new Usuario();
            secretaria.setNome("Secretaria");
            secretaria.setEmail("secretaria@gmail.com");
            secretaria.setSenha(senhaPadrao);
            secretaria.setPerfil(Perfil.SECRETARIA);

            Usuario coordenador = new Usuario();
            coordenador.setNome("Coordenador");
            coordenador.setEmail("coordenador@gmail.com");
            coordenador.setSenha(senhaPadrao);
            coordenador.setPerfil(Perfil.COORDENADOR);

            Usuario aluno = new Usuario();
            aluno.setNome("Aluno");
            aluno.setEmail("aluno@gmail.com");
            aluno.setSenha(senhaPadrao);
            aluno.setPerfil(Perfil.ALUNO);

            usuarioRepository.saveAll(Arrays.asList(admin, secretaria, coordenador, aluno));
        }


        // Criar doc e fluxo de conferencia
        // Criar doc e fluxo de conferencia
        if (documentoRepository.count() == 0) {
            List<Status> listaStatus = statusRepository.findAll();

            Status statusAberta = listaStatus.stream()
                    .filter(s -> s.getNome().equals("Aberta"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Status Aberta não encontrado"));

            Status statusAnalise = listaStatus.stream()
                    .filter(s -> s.getNome().equals("Em Análise"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Status Em Análise não encontrado"));

            // Documento base
            Documento documento = new Documento();
            documento.setNome("Atestado de frequência");
            Documento docSalvo = documentoRepository.save(documento);

            // Fluxo de conferência (Secretaria)
            EtapaFluxo etapa1 = new EtapaFluxo();
            etapa1.setTipoDocumento(docSalvo);
            etapa1.setOrdem(1);
            etapa1.setPerfilResponsavel(Perfil.SECRETARIA);
            etapa1.setStatus(statusAberta);

            // Etapa 2 (Coordenador)
            EtapaFluxo etapa2 = new EtapaFluxo();
            etapa2.setTipoDocumento(docSalvo);
            etapa2.setOrdem(2);
            etapa2.setPerfilResponsavel(Perfil.COORDENADOR);
            etapa2.setStatus(statusAnalise);

            etapaFluxoRepository.saveAll(Arrays.asList(etapa1, etapa2));
        }

        // Curso e matrícula
        if (cursoRepository.count() == 0) {
            Curso curso = new Curso();
            curso.setNome("Direito");

            Curso cursoSalvo = cursoRepository.save(curso);

            // Matricula do aluno teste ao curso
            if (matriculaRepository.count() == 0) {

                // Busca o aluno que criamos no passo 2
                Usuario alunoTeste = usuarioRepository.findByEmail("aluno@gmail.com")
                        .orElseThrow(() -> new RuntimeException("Aluno não encontrado no seeder"));

                Matricula matricula = new Matricula();
                matricula.setAluno(alunoTeste);
                matricula.setCurso(cursoSalvo);
                matricula.setAtivo(true);

                matriculaRepository.save(matricula);
            }
        }
    }
}