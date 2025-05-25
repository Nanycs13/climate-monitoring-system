package com.example.climatemonitoring.service;

import com.example.climatemonitoring.models.Clima;
import com.example.climatemonitoring.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class NotificacaoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoScheduler.class);

    private final NotificacaoService notificacaoService;
    private final UsuarioService usuarioService;
    private final ClimaService climaService;

    private volatile boolean schedulerAtivo = false;

    @Autowired
    public NotificacaoScheduler(NotificacaoService notificacaoService,
            UsuarioService usuarioService,
            ClimaService climaService) {
        this.notificacaoService = notificacaoService;
        this.usuarioService = usuarioService;
        this.climaService = climaService;
    }

    @Scheduled(fixedDelay = 20000, initialDelay = 20000)
    public void enviarNotificacoesPeriodicas() {
        if (!schedulerAtivo) {
            return;
        }

        try {
            List<Usuario> usuariosLogados = usuarioService.getUsuariosLogados();

            if (usuariosLogados.isEmpty()) {
                logger.debug("Nenhum usuário logado - pulando notificações automáticas");
                return;
            }

            Clima climaAtual = obterDadosClimaticosRobustamente();

            if (climaAtual == null) {
                logger.warn("Não foi possível obter dados climáticos - pulando notificações");
                return;
            }

            for (Usuario usuario : usuariosLogados) {
                String mensagem = String.format(
                        "BOLETIM AUTOMÁTICO SISTEMA - LEM\n" +
                                "Temperatura: %.1f°C\n" +
                                "Condições: %s\n" +
                                "Umidade: %.0f%%\n" +
                                "Atualização do sistema para %s",
                        climaAtual.getTemperatura(),
                        climaAtual.getCondicoes(),
                        climaAtual.getUmidade(),
                        usuario.getNome());

                enviarNotificacaoSistema(usuario, mensagem);
            }

            logger.info("Notificações do sistema enviadas para {} usuários logados",
                    usuariosLogados.size());

        } catch (Exception e) {
            logger.error("Erro ao enviar notificações automáticas do sistema", e);
        }
    }

    private Clima obterDadosClimaticosRobustamente() {
        try {
            return climaService.obterDadosClimaticos();

        } catch (Exception e1) {
            logger.debug("Método padrão falhou, tentando com reflection...");
            try {
                return tentarObterDadosComReflection();
            } catch (Exception e2) {
                logger.error("Erro ao obter dados climáticos via reflection", e2);
                return null;
            }
        }
    }

    private Clima tentarObterDadosComReflection() {
        try {
            Class<?> clazz = climaService.getClass();

            for (var method : clazz.getMethods()) {
                if (method.getReturnType() == Clima.class &&
                        method.getParameterCount() == 0 &&
                        method.getName().contains("obterDados")) {

                    logger.debug("Invocando método alternativo via reflection: {}", method.getName());
                    return (Clima) method.invoke(climaService);
                }
            }

            logger.warn("Nenhum método compatível encontrado via reflection.");
            return null;

        } catch (Exception e) {
            logger.error("Erro ao usar reflection para obter dados climáticos", e);
            return null;
        }
    }

   private void enviarNotificacaoSistema(Usuario usuario, String mensagem) {
    try {
        notificacaoService.enviarNotificacaoAutomatica(usuario, mensagem);
    } catch (Exception e) {
        logger.error("Erro ao enviar notificação para {}: {}", usuario.getEmail(), e.getMessage());
    }
}

    public void pararScheduler() {
        schedulerAtivo = false;
        logger.info("NotificacaoScheduler pausado");
    }

    public void reiniciarScheduler() {
        schedulerAtivo = true;
        logger.info("NotificacaoScheduler reativado");
    }

    public boolean isAtivo() {
        return schedulerAtivo;
    }
}