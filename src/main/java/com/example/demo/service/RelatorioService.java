package com.example.demo.service;

import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

@Service
public class RelatorioService {

    public byte[] gerarPdf(String nomeRelatorio, Map<String, Object> parametros) {
        try {
            // Pega o arquivo jasper da pasta resources
            InputStream stream = getClass().getResourceAsStream("/relatorios/" + nomeRelatorio + ".jasper");
            if (stream == null) {
                stream = getClass().getResourceAsStream("/relatorios/" + nomeRelatorio + ".jrxml");
                JasperReport report = JasperCompileManager.compileReport(stream);
                JasperPrint print = JasperFillManager.fillReport(report, parametros, new JREmptyDataSource());
                return JasperExportManager.exportReportToPdf(print);
            }
            JasperPrint print = JasperFillManager.fillReport(stream, parametros, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o PDF: " + e.getMessage());
        }
    }
}