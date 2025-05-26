package com.example.climatemonitoring.controller;

import com.example.climatemonitoring.service.ClimaService;
import com.example.climatemonitoring.models.Clima;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/clima")
@CrossOrigin(origins = "*")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }


    @GetMapping("/atual")
    public ResponseEntity<?> obterClimaAtual() {
        try {
            Clima clima = climaService.obterDadosClimaticos();
            return ResponseEntity.ok(clima);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao obter dados climáticos de São Desidério: " + e.getMessage());
        }
    }


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


    @GetMapping("/status")
    public ResponseEntity<String> verificarStatus() {
        boolean status = climaService.verificarStatusAPI();
        if (status) {
            return ResponseEntity.ok("✅ API funcionando corretamente para São Desidério");
        } else {
            return ResponseEntity.status(503)
                    .body("❌ API indisponível ou erro na configuração");
        }
    }


    @GetMapping("/dashboard")
    public ResponseEntity<?> obterDadosDashboard() {
        try {
            Clima clima = climaService.obterDadosClimaticos();
            String[] risco = climaService.obterAnaliseRisco();

            var dashboard = new Object() {
                public final String cidade = "São Desidério - BA";
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