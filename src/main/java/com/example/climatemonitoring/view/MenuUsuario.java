package com.example.climatemonitoring.view;

import com.example.climatemonitoring.models.Notificacao;
import com.example.climatemonitoring.models.Usuario;
import com.example.climatemonitoring.models.observers.UsuarioObserver;
import com.example.climatemonitoring.service.ClimaService;
import com.example.climatemonitoring.service.NotificacaoService;
import com.example.climatemonitoring.service.NotificacaoScheduler;
import com.example.climatemonitoring.service.UsuarioService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuUsuario {
    private TerminalUtil terminal;
    private ClimaService climaService;
    private NotificacaoService notificacaoService;
    private UsuarioService usuarioService;
    private NotificacaoScheduler notificacaoScheduler;
    private ScheduledExecutorService scheduler;
    private volatile boolean menuAtivo = false;
    private int contadorNotificacoes = 0;

    @Autowired
    public MenuUsuario(TerminalUtil terminal, ClimaService climaService,
            NotificacaoService notificacaoService, UsuarioService usuarioService,
            NotificacaoScheduler notificacaoScheduler) {
        this.terminal = terminal;
        this.climaService = climaService;
        this.notificacaoService = notificacaoService;
        this.usuarioService = usuarioService;
        this.notificacaoScheduler = notificacaoScheduler;
    }

    public void exibir(Usuario usuario) {
        menuAtivo = true;
        contadorNotificacoes = 0;
        usuario.setLogado(true);

        // Cria um observador para o usuário
        UsuarioObserver observador = new UsuarioObserver(usuario);

        // Adiciona o observador para receber notificações
        climaService.adicionarObservador(observador);

        // Inicia as notificações automáticas a cada 20 segundos
        iniciarNotificacoesAutomaticas(usuario, observador);

        boolean sair = false;

        while (!sair) {
            exibirMenuPrincipal(usuario);

            int opcao = terminal.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    verBoletimClimatico();
                    break;
                case 2:
                    verAnaliseRisco();
                    break;
                case 3:
                    verHistoricoNotificacoes();
                    break;
                case 0:
                    sair = true;
                    realizarLogout(usuario, observador);
                    break;
                default:
                    terminal.exibirMensagem("Opção inválida!");
                    terminal.pausar();
            }
        }
    }

    private void exibirMenuPrincipal(Usuario usuario) {
        terminal.limparTela();

        // Cabeçalho com informações em tempo real
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String agora = LocalDateTime.now().format(formatter);

        terminal.exibirMensagem("=== MONITORAMENTO CLIMÁTICO SÃO DESIDÉRIO ===");
        terminal.exibirMensagem("Usuário: " + usuario.getNome());
        terminal.exibirMensagem("Data/Hora: " + agora);
        terminal.exibirMensagem("Notificações recebidas: " + contadorNotificacoes);
        terminal.exibirMensagem("Status: MONITORAMENTO ATIVO");
        terminal.exibirMensagem("=================================");
        terminal.exibirMensagem("1. Ver boletim climático atual");
        terminal.exibirMensagem("2. Ver análise de risco agrícola");
        terminal.exibirMensagem("3. Ver histórico de notificações");
        terminal.exibirMensagem("0. Logout");
        terminal.exibirMensagem("=================================");
    }

    private void iniciarNotificacoesAutomaticas(Usuario usuario, UsuarioObserver observador) {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(() -> {
            if (menuAtivo) {
                try {
                    contadorNotificacoes++;

                    // Gera um novo boletim climático de São Desidério
                    String boletimAutomatico = climaService.gerarBoletimClimatico();

                    // Cria uma versão simplificada para notificação em tempo real
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String timestamp = LocalDateTime.now().format(timeFormatter);

                    String notificacaoSimples = String.format(
                            "ATUALIZAÇÃO #%d [%s] - SÃO DESIDÉRIO\n" +
                                    "============================\n%s\n" +
                                    "============================\n",
                            contadorNotificacoes, timestamp, boletimAutomatico);

                    // Registra a notificação completa no histórico
                    String mensagemCompleta = String.format(
                            "NOTIFICAÇÃO AUTOMÁTICA #%d\n%s\n\n%s",
                            contadorNotificacoes, timestamp, boletimAutomatico);

                    notificacaoService.registrarNotificacao(mensagemCompleta);

                    // Exibe a notificação em tempo real no terminal
                    terminal.exibirNotificacaoEmTempoReal(notificacaoSimples);

                    // Adiciona alertas especiais baseados nos dados
                    verificarAlertasEspeciais(usuario);

                } catch (Exception e) {
                    terminal.exibirMensagem("Erro na notificação automática: " + e.getMessage());
                }
            }
        }, 20, 20, TimeUnit.SECONDS); // Primeira execução em 20s, depois a cada 20s
    }

    private void verificarAlertasEspeciais(Usuario usuario) {
        try {
            var clima = climaService.obterDadosClimaticos();

            // Alerta de temperatura extrema
            if (clima.getTemperatura() > 38) {
                String alerta = "ALERTA TEMPERATURA ALTA: " + String.format("%.1f°C", clima.getTemperatura()) +
                        "\nRisco para plantações em São Desidério!";
                terminal.exibirAlerta(alerta);
                notificacaoService.registrarNotificacao("ALERTA CRÍTICO: " + alerta);
            }

            // Alerta de umidade baixa
            if (clima.getUmidade() < 20) {
                String alerta = "ALERTA UMIDADE BAIXA: " + String.format("%.0f%%", clima.getUmidade()) +
                        "\nConsidere irrigação adicional!";
                terminal.exibirAlerta(alerta);
                notificacaoService.registrarNotificacao("ALERTA UMIDADE: " + alerta);
            }

        } catch (Exception e) {
            // Ignora erros silenciosamente para não interromper o fluxo
        }
    }

    private void realizarLogout(Usuario usuario, UsuarioObserver observador) {
        menuAtivo = false;

        // Para o agendador de notificações
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Remove o observador ao fazer logout
        climaService.removerObservador(observador);

        // Para o agendador de notificações do sistema
        notificacaoScheduler.pararScheduler();

        // Marca usuário como deslogado
        usuario.setLogado(false);

        // Mensagem de despedida
        String mensagemLogout = String.format(
                "LOGOUT REALIZADO - %s\n" +
                        "Total de notificações recebidas: %d\n" +
                        "Sessão encerrada às %s\n" +
                        "Obrigado por monitorar o clima de São Desidério!",
                usuario.getNome(),
                contadorNotificacoes,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        terminal.exibirMensagem(mensagemLogout);
        notificacaoService.registrarNotificacao(mensagemLogout);
        terminal.pausar();
    }

    private void verBoletimClimatico() {
        terminal.limparTela();
        terminal.exibirMensagem("=== BOLETIM CLIMÁTICO ATUAL ===");
        terminal.exibirMensagem("São Desidério - Bahia");
        terminal.exibirMensagem("==============================");

        try {
            String boletim = climaService.gerarBoletimClimatico();
            terminal.exibirMensagem(boletim);
        } catch (Exception e) {
            terminal.exibirMensagem("Erro ao obter boletim: " + e.getMessage());
        }

        terminal.pausar();
    }

    private void verAnaliseRisco() {
        terminal.limparTela();
        terminal.exibirMensagem("=== ANÁLISE DE RISCO AGRÍCOLA ===");
        terminal.exibirMensagem("São Desidério - Próximos 3 dias");
        terminal.exibirMensagem("=====================================");

        try {
            String[] analise = climaService.obterAnaliseRisco();
            for (String dia : analise) {
                terminal.exibirMensagem(dia);
            }
        } catch (Exception e) {
            terminal.exibirMensagem("Erro ao obter análise: " + e.getMessage());
        }

        terminal.pausar();
    }

    private void verHistoricoNotificacoes() {
        terminal.limparTela();
        terminal.exibirMensagem("=== HISTÓRICO DE NOTIFICAÇÕES ===");

        List<Notificacao> historico = notificacaoService.listarHistorico();

        if (historico.isEmpty()) {
            terminal.exibirMensagem("Nenhuma notificação registrada.");
        } else {
            terminal.exibirMensagem("Total de notificações: " + historico.size());
            terminal.exibirMensagem("Últimas " + Math.min(10, historico.size()) + " notificações:");
            terminal.exibirMensagem("=================================");
            for (int i = 0; i < Math.min(10, historico.size()); i++) {
                terminal.exibirMensagem((i + 1) + ". " + historico.get(i).getMensagem());
                terminal.exibirMensagem("---------------------------------");
            }
        }

        terminal.pausar();
    }
}