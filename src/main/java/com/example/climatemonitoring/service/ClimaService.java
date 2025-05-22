package com.example.climatemonitoring.service;

import org.springframework.stereotype.Service;

import com.example.climatemonitoring.api.ClimaAPI;
import com.example.climatemonitoring.models.Clima;
import com.example.climatemonitoring.models.observers.ClimaSubject;
import com.example.climatemonitoring.models.observers.Observer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class ClimaService {
    private ClimaAPI climaAPI;
    private ClimaSubject climaSubject;

    public ClimaService(ClimaAPI climaAPI) {
        this.climaAPI = climaAPI;
        this.climaSubject = new ClimaSubject();
    }


    public Clima obterDadosClimaticos() {
        return climaAPI.obterDadosClimaticos();
    }


    public String[] obterAnaliseRisco() {
        return climaAPI.obterAnaliseRisco();
    }


    public void adicionarObservador(Observer observer) {
        climaSubject.addObserver(observer);
    }


    public void removerObservador(Observer observer) {
        climaSubject.removeObserver(observer);
    }


    public void notificarObservadores(String mensagem) {
        climaSubject.notifyObservers(mensagem);
    }


    public String gerarBoletimClimatico() {
        try {
            Clima clima = obterDadosClimaticos();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataHora = LocalDateTime.now().format(formatter);

            StringBuilder boletim = new StringBuilder();
            boletim.append("=== BOLETIM CLIMÁTICO - LUÍS EDUARDO MAGALHÃES/BA ===\n");
            boletim.append("Data/Hora: ").append(dataHora).append("\n");
            boletim.append("Localização: ").append(clima.getLocalizacao()).append("\n\n");

            boletim.append("CONDIÇÕES ATUAIS:\n");
            boletim.append(String.format("🌡️  Temperatura: %.1fºC\n", clima.getTemperatura()));
            boletim.append(String.format("💧 Umidade: %.0f%%\n", clima.getUmidade()));
            boletim.append(String.format("☁️  Condições: %s\n\n", clima.getCondicoes()));

            // Adicionar análise de risco para agricultura
            boletim.append("ANÁLISE AGRÍCOLA - PRÓXIMOS 3 DIAS:\n");
            String[] analiseRisco = obterAnaliseRisco();
            for (String risco : analiseRisco) {
                boletim.append("📊 ").append(risco).append("\n");
            }

            boletim.append("\n=== REGIÃO OESTE DA BAHIA - AGRICULTURA SUSTENTÁVEL ===");

            return boletim.toString();

        } catch (Exception e) {
            return "❌ Erro ao gerar boletim climático de Luís Eduardo Magalhães: " + e.getMessage();
        }
    }


    public boolean verificarStatusAPI() {
        try {
            obterDadosClimaticos();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}