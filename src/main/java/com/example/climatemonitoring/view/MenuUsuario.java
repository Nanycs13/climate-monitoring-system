package com.example.climatemonitoring.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.climatemonitoring.models.Notificacao;
import com.example.climatemonitoring.models.Usuario;
import com.example.climatemonitoring.models.observers.UsuarioObserver;
import com.example.climatemonitoring.service.ClimaService;
import com.example.climatemonitoring.service.NotificacaoScheduler;
import com.example.climatemonitoring.service.NotificacaoService;
import com.example.climatemonitoring.service.UsuarioService;

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

        UsuarioObserver observador = new UsuarioObserver(usuario);

        climaService.adicionarObservador(observador);

        iniciarNotificacoesAutomaticas(usuario, observador);

        boolean sair = false;

        while (!sair) {
            exibirMenuPrincipal(usuario);

            int opcao = terminal.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    verBoletimClimatico(usuario);
                    break;
                case 2:
                    verAnaliseRisco(usuario);
                    break;
                case 3:
                    verHistoricoNotificacoes(usuario);  
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

                    String boletimAutomatico = climaService.gerarBoletimClimatico();

                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String timestamp = LocalDateTime.now().format(timeFormatter);

                    String notificacaoSimples = String.format(
                            "ATUALIZAÇÃO #%d [%s] - SÃO DESIDÉRIO\n" +
                                    "============================\n%s\n" +
                                    "============================\n",
                            contadorNotificacoes, timestamp, boletimAutomatico);

                    String mensagemCompleta = String.format(
                            "NOTIFICAÇÃO AUTOMÁTICA #%d\n%s\n\n%s",
                            contadorNotificacoes, timestamp, boletimAutomatico);

                    // Registrar notificação específica para o usuário
                    notificacaoService.registrarNotificacaoUsuario(mensagemCompleta, usuario.getEmail());

                    terminal.exibirNotificacaoEmTempoReal(notificacaoSimples);

                    verificarAlertasEspeciais(usuario);

                } catch (Exception e) {
                    terminal.exibirMensagem("Erro na notificação automática: " + e.getMessage());
                }
            }
        }, 20, 20, TimeUnit.SECONDS); 
    }

    private void verificarAlertasEspeciais(Usuario usuario) {
        try {
            var clima = climaService.obterDadosClimaticos();

            if (clima.getTemperatura() > 38) {
                String alerta = "ALERTA TEMPERATURA ALTA: " + String.format("%.1f°C", clima.getTemperatura()) +
                        "\nRisco para plantações em São Desidério!";
                terminal.exibirAlerta(alerta);
                notificacaoService.registrarNotificacaoUsuario("ALERTA CRÍTICO: " + alerta, usuario.getEmail());
            }

            if (clima.getUmidade() < 20) {
                String alerta = "ALERTA UMIDADE BAIXA: " + String.format("%.0f%%", clima.getUmidade()) +
                        "\nConsidere irrigação adicional!";
                terminal.exibirAlerta(alerta);
                notificacaoService.registrarNotificacaoUsuario("ALERTA UMIDADE: " + alerta, usuario.getEmail());
            }

        } catch (Exception e) {
            // Log do erro mas não interrompe o fluxo
        }
    }

    private void realizarLogout(Usuario usuario, UsuarioObserver observador) {
        menuAtivo = false;

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

        climaService.removerObservador(observador);

        notificacaoScheduler.pararScheduler();

        usuario.setLogado(false);

        String mensagemLogout = String.format(
                "LOGOUT REALIZADO - %s\n" +
                        "Total de notificações recebidas: %d\n" +
                        "Sessão encerrada às %s\n" +
                        "Obrigado por monitorar o clima de São Desidério!",
                usuario.getNome(),
                contadorNotificacoes,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        terminal.exibirMensagem(mensagemLogout);
        notificacaoService.registrarNotificacaoUsuario(mensagemLogout, usuario.getEmail());
        terminal.pausar();
    }

    private void verBoletimClimatico(Usuario usuario) {
        terminal.limparTela();
        terminal.exibirMensagem("=== BOLETIM CLIMÁTICO ATUAL ===");
        terminal.exibirMensagem("São Desidério - Bahia");
        terminal.exibirMensagem("==============================");

        try {
            String boletim = climaService.gerarBoletimClimatico();
            terminal.exibirMensagem(boletim);
            
            // Registrar que o usuário consultou o boletim
            String acesso = String.format("CONSULTA MANUAL - Boletim climático acessado por %s às %s",
                    usuario.getNome(), 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            notificacaoService.registrarNotificacaoUsuario(acesso, usuario.getEmail());
            
        } catch (Exception e) {
            terminal.exibirMensagem("Erro ao obter boletim: " + e.getMessage());
        }

        terminal.pausar();
    }

    private void verAnaliseRisco(Usuario usuario) {
        terminal.limparTela();
        terminal.exibirMensagem("=== ANÁLISE DE RISCO AGRÍCOLA ===");
        terminal.exibirMensagem("São Desidério - Próximos 3 dias");
        terminal.exibirMensagem("=====================================");

        try {
            String[] analise = climaService.obterAnaliseRisco();
            for (String dia : analise) {
                terminal.exibirMensagem(dia);
            }
            
            // Registrar que o usuário consultou a análise de risco
            String acesso = String.format("CONSULTA MANUAL - Análise de risco acessada por %s às %s",
                    usuario.getNome(), 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            notificacaoService.registrarNotificacaoUsuario(acesso, usuario.getEmail());
            
        } catch (Exception e) {
            terminal.exibirMensagem("Erro ao obter análise: " + e.getMessage());
        }

        terminal.pausar();
    }

    private void verHistoricoNotificacoes(Usuario usuario) {  
        terminal.limparTela();
        terminal.exibirMensagem("=== HISTÓRICO DE NOTIFICAÇÕES PESSOAIS ===");
        terminal.exibirMensagem("Usuário: " + usuario.getNome() + " (" + usuario.getEmail() + ")");
        terminal.exibirMensagem("==========================================");

        try {
            String emailUsuario = usuario.getEmail();
            List<Notificacao> historico = notificacaoService.listarHistoricoUsuario(emailUsuario);

            if (historico.isEmpty()) {
                terminal.exibirMensagem("Nenhuma notificação registrada para você ainda.");
                terminal.exibirMensagem("As notificações aparecerão conforme você usar o sistema.");
            } else {
                terminal.exibirMensagem("Total de notificações pessoais: " + historico.size());
                terminal.exibirMensagem("Últimas " + Math.min(15, historico.size()) + " notificações:");
                terminal.exibirMensagem("=".repeat(50));
                
                // Mostrar as notificações mais recentes primeiro
                int inicio = Math.max(0, historico.size() - 15);
                for (int i = historico.size() - 1; i >= inicio; i--) {
                    Notificacao notificacao = historico.get(i);
                    terminal.exibirMensagem((historico.size() - i) + ". " + notificacao.getMensagem());
                    terminal.exibirMensagem("-".repeat(30));
                }
            }
            
            // Registrar que o usuário acessou o histórico
            String acesso = String.format("CONSULTA HISTÓRICO - %s visualizou seu histórico de notificações às %s",
                    usuario.getNome(), 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            notificacaoService.registrarNotificacaoUsuario(acesso, usuario.getEmail());
            
        } catch (Exception e) {
            terminal.exibirMensagem("Erro ao acessar histórico: " + e.getMessage());
        }

        terminal.pausar();
    }
}