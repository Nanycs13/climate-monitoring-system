package com.example.climatemonitoring.controller;

import com.example.climatemonitoring.service.ClimaService;
import com.example.climatemonitoring.models.Clima;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para dados climáticos de Luís Eduardo Magalhães - BA.
 * Especializado no monitoramento climático para agricultura no oeste da Bahia.
 */
@RestController
@RequestMapping("/api/clima")
@CrossOrigin(origins = "*")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    /**
     * Endpoint para obter dados climáticos atuais de Luís Eduardo Magalhães.
     * 
     * @return ResponseEntity com dados climáticos atuais
     */
    @GetMapping("/atual")
    public ResponseEntity<?> obterClimaAtual() {
        try {
            Clima clima = climaService.obterDadosClimaticos();
            return ResponseEntity.ok(clima);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao obter dados climáticos de Luís Eduardo Magalhães: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obter o boletim climático completo de LEM.
     * 
     * @return ResponseEntity com boletim climático formatado
     */
    @GetMapping("/boletim")
    public ResponseEntity<String> obterBoletimClimatico() {
        try {
            String boletim = climaService.gerarBoletimClimatico();
            return ResponseEntity.ok(boletim);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao gerar boletim: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obter análise de risco agrícola de LEM.
     * 
     * @return ResponseEntity com análise de risco para os próximos dias
     */
    @GetMapping("/risco-agricola")
    public ResponseEntity<?> obterAnaliseRisco() {
        try {
            String[] analise = climaService.obterAnaliseRisco();
            return ResponseEntity.ok(analise);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao obter análise de risco: " + e.getMessage());
        }
    }

    /**
     * Endpoint para verificar status da API.
     * 
     * @return ResponseEntity indicando se a API está funcionando
     */
    @GetMapping("/status")
    public ResponseEntity<String> verificarStatus() {
        boolean status = climaService.verificarStatusAPI();
        if (status) {
            return ResponseEntity.ok("✅ API funcionando corretamente para Luís Eduardo Magalhães");
        } else {
            return ResponseEntity.status(503)
                    .body("❌ API indisponível ou erro na configuração");
        }
    }

    /**
     * Endpoint para dashboard - dados resumidos para interface.
     * 
     * @return ResponseEntity com dados essenciais para dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> obterDadosDashboard() {
        try {
            Clima clima = climaService.obterDadosClimaticos();
            String[] risco = climaService.obterAnaliseRisco();

            // Criar objeto resumido para dashboard
            var dashboard = new Object() {
                public final String cidade = "Luís Eduardo Magalhães - BA";
                public final double temperatura = clima.getTemperatura();
                public final double umidade = clima.getUmidade();
                public final String condicoes = clima.getCondicoes();
                public final String[] proximosDias = risco;
                public final String statusGeral = determinarStatusGeral(clima.getTemperatura(), clima.getUmidade());
            };

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao obter dados para dashboard: " + e.getMessage());
        }
    }

    /**
     * Determina o status geral das condições climáticas para agricultura.
     */
    private String determinarStatusGeral(double temperatura, double umidade) {
        if (temperatura >= 20 && temperatura <= 30 && umidade >= 50 && umidade <= 80) {
            return "IDEAL";
        } else if (temperatura >= 15 && temperatura <= 35 && umidade >= 40) {
            return "BOM";
        } else if (temperatura > 35 || temperatura < 10 || umidade < 30) {
            return "ALERTA";
        } else {
            return "ATENÇÃO";
        }
    }
}