package com.example.climatemonitoring.api;

import com.example.climatemonitoring.models.Clima;

public interface ClimaAPIInterface {
    Clima obterDadosClimaticos();

    String[] obterAnaliseRisco();
}
