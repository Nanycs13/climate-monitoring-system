package com.example.climatemonitoring.service;

import org.springframework.stereotype.Service;

import com.example.climatemonitoring.api.ClimaAPI;
import com.example.climatemonitoring.models.Clima;
import com.example.climatemonitoring.models.observers.ClimaSubject;
import com.example.climatemonitoring.models.observers.Observer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serviço para gerenciamento de dados climáticos de Luís Eduardo Magalhães -
 * BA.
 * Especializado no monitoramento climático para agricultura no oeste da Bahia.
 * Segue o princípio SRP ao ter apenas a responsabilidade de gerenciar dados
 * climáticos específicos de LEM.
 * Segue o princípio DIP ao depender de abstrações (API) injetadas via
 * construtor.
 */
@Service
public class ClimaService {
    private ClimaAPI climaAPI;
    private ClimaSubject climaSubject;

    public ClimaService(ClimaAPI climaAPI) {
        this.climaAPI = climaAPI;
        this.climaSubject = new ClimaSubject();
    }

    /**
     * Obtém os dados climáticos atuais de Luís Eduardo Magalhães.
     * 
     * @return Objeto Clima com os dados atuais de LEM
     */
    public Clima obterDadosClimaticos() {
        return climaAPI.obterDadosClimaticos();
    }

    /**
     * Obtém a análise de risco para plantio nos próximos dias em Luís Eduardo
     * Magalhães.
     * 
     * @return Array de strings com a análise para cada dia
     */
    public String[] obterAnaliseRisco() {
        return climaAPI.obterAnaliseRisco();
    }

    /**
     * Adiciona um observador para receber notificações climáticas.
     * 
     * @param observer Observador a ser adicionado
     */
    public void adicionarObservador(Observer observer) {
        climaSubject.addObserver(observer);
    }

    /**
     * Remove um observador.
     * 
     * @param observer Observador a ser removido
     */
    public void removerObservador(Observer observer) {
        climaSubject.removeObserver(observer);
    }

    /**
     * Notifica todos os observadores com uma mensagem.
     * 
     * @param mensagem Mensagem a ser enviada
     */
    public void notificarObservadores(String mensagem) {
        climaSubject.notifyObservers(mensagem);
    }

    /**
     * Gera um boletim climático formatado para Luís Eduardo Magalhães.
     * 
     * @return String formatada com o boletim climático de LEM
     */
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

    /**
     * Verifica se a API está funcionando corretamente para Luís Eduardo Magalhães.
     * 
     * @return true se a API estiver respondendo, false caso contrário
     */
    public boolean verificarStatusAPI() {
        try {
            obterDadosClimaticos();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}