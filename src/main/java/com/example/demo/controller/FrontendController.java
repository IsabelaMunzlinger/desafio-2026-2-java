package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/login")
    public String telaLogin() {
        return "index";
    }

    @GetMapping("/menu")
    public String telaMenu() {
        return "menu";
    }

    @GetMapping("/cadastros")
    public String telaCadastros() {
        return "cadastros";
    }

    @GetMapping("/cadastro-cursos")
    public String telaCursos() {
        return "cadastro-cursos";
    }

    @GetMapping("/cadastro-usuarios")
    public String telaUsuarios() {
        return "cadastro-usuarios";
    }

    @GetMapping("/cadastro-documentos")
    public String telaDocumentos() {
        return "cadastro-documentos";
    }

    @GetMapping("/cadastro-fluxos")
    public String telaFluxos() {
        return "cadastro-fluxos";
    }

    @GetMapping("/cadastro-matriculas")
    public String telaMatriculas() {
        return "cadastro-matriculas";
    }

    @GetMapping("/solicitar-documento")
    public String telaSolicitarDocumento() {
        return "solicitar-documento";
    }

    @GetMapping("/fila-trabalho")
    public String telaFilaTrabalho() {
        return "fila-trabalho";
    }

    @GetMapping("/cadastro-status")
    public String telaStatus() {
        return "cadastro-status";
    }

    @GetMapping("/meus-pedidos")
    public String telaMeusPedidos() {
        return "meus-pedidos";
    }

    @GetMapping("/historico-pedido")
    public String telaHistorico() {
        return "historico-pedido";
    }

    @GetMapping("/estatisticas")
    public String telaDashboard(){
        return "estatisticas";
    }
}