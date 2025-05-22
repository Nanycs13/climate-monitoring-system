package com.example.climatemonitoring.api;

import com.example.climatemonitoring.models.Clima;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

/**
 * API para obtenção de dados climáticos reais usando latitude e longitude.
 */
@Service
public class ClimaAPI implements ClimaAPIInterface {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${app.cidade.latitude}")
    private double latitude;

    @Value("${app.cidade.longitude}")
    private double longitude;

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ClimaAPI() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Clima obterDadosClimaticos() {
        return obterDadosClimaticosPorCoordenadas();
    }

    private Clima obterDadosClimaticosPorCoordenadas() {
        try {
            // Monta a URL usando lat e lon
            String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=metric&lang=pt_br",
                    BASE_URL, latitude, longitude, apiKey);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            double temperatura = jsonNode.path("main").path("temp").asDouble();
            double umidade = jsonNode.path("main").path("humidity").asDouble();
            String condicoes = jsonNode.path("weather").get(0).path("description").asText();
            String localizacaoReal = jsonNode.path("name").asText();

            return new Clima(localizacaoReal, temperatura, umidade, condicoes);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException(
                        "Dados de localização não encontrados para as coordenadas: " + latitude + ", " + longitude);
            } else if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("Chave da API inválida ou não configurada");
            }
            throw new RuntimeException("Erro ao obter dados climáticos: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar dados climáticos: " + e.getMessage());
        }
    }

    @Override
    public String[] obterAnaliseRisco() {
        return obterAnaliseRiscoPorCoordenadas();
    }

    private String[] obterAnaliseRiscoPorCoordenadas() {
        try {
            String url = String.format("%s/forecast?lat=%f&lon=%f&appid=%s&units=metric&lang=pt_br",
                    BASE_URL, latitude, longitude, apiKey);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode forecasts = jsonNode.path("list");

            String[] analise = new String[3];

            for (int i = 0; i < 3 && i * 8 < forecasts.size(); i++) {
                JsonNode forecast = forecasts.get(i * 8); // 8 * 3h = 24h

                double temperatura = forecast.path("main").path("temp").asDouble();
                double precipitacao = 0.0;

                if (forecast.has("rain") && forecast.path("rain").has("3h")) {
                    precipitacao = forecast.path("rain").path("3h").asDouble();
                }
                if (forecast.has("snow") && forecast.path("snow").has("3h")) {
                    precipitacao += forecast.path("snow").path("3h").asDouble();
                }

                String risco = calcularRiscoPlantio(temperatura, precipitacao);

                analise[i] = String.format("Dia %d: %.1fºC / %.1f mm – %s",
                        i + 1, temperatura, precipitacao, risco);
            }

            return analise;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException(
                        "Dados de previsão não encontrados para as coordenadas: " + latitude + ", " + longitude);
            } else if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("Chave da API inválida ou não configurada");
            }
            throw new RuntimeException("Erro ao obter previsão climática: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar previsão climática: " + e.getMessage());
        }
    }

    private String calcularRiscoPlantio(double temperatura, double precipitacao) {
        if (temperatura > 38 || temperatura < 8 || precipitacao > 20) {
            return "Desfavorável";
        } else if (temperatura > 34 || temperatura < 12 || precipitacao > 12) {
            return "Atenção";
        } else if (temperatura >= 20 && temperatura <= 30 && precipitacao >= 1 && precipitacao <= 8) {
            return "Ideal";
        } else {
            return "Favorável";
        }
    }
}
