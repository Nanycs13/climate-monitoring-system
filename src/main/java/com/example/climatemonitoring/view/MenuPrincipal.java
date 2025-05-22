package com.example.climatemonitoring.view;

import com.example.climatemonitoring.models.Usuario;
import com.example.climatemonitoring.service.ClimaService;
import com.example.climatemonitoring.service.NotificacaoScheduler;
import com.example.climatemonitoring.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuPrincipal {
    private TerminalUtil terminal;
    private UsuarioService usuarioService;
    private MenuUsuario menuUsuario;
    private ClimaService climaService;
    private NotificacaoScheduler notificacaoScheduler;

    @Autowired
    public MenuPrincipal(TerminalUtil terminal, UsuarioService usuarioService,
            MenuUsuario menuUsuario, ClimaService climaService, NotificacaoScheduler notificacaoScheduler) {
        this.terminal = terminal;
        this.usuarioService = usuarioService;
        this.menuUsuario = menuUsuario;
        this.climaService = climaService;
        this.notificacaoScheduler = notificacaoScheduler;
    }

    public void exibir() {
        boolean sair = false;

        while (!sair) {
            terminal.limparTela();
            terminal.exibirMensagem("=== SISTEMA DE MONITORAMENTO CLIMÁTICO ===");
            terminal.exibirMensagem("São Desidério - BAHIA");
            terminal.exibirMensagem("=====================================");
            terminal.exibirMensagem("1. Fazer login");
            terminal.exibirMensagem("2. Cadastrar novo usuário");
            terminal.exibirMensagem("3. Listar todos os usuários");
            terminal.exibirMensagem("4. Testar conexão com API");
            terminal.exibirMensagem("0. Sair");
            terminal.exibirMensagem("=====================================");

            int opcao = terminal.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    fazerLogin();
                    break;
                case 2:
                    cadastrarUsuario();
                    break;
                case 3:
                    listarUsuarios();
                    break;
                case 4:
                    testarConexaoAPI();
                    break;
                case 0:
                    sair = true;
                    terminal.exibirMensagem("Obrigado por usar o Sistema de Monitoramento de São Desidério!");
                    terminal.exibirMensagem("Até logo!");
                    break;
                default:
                    terminal.exibirMensagem("Opção inválida!");
                    terminal.pausar();
            }
        }
    }

    private void fazerLogin() {
        terminal.limparTela();
        terminal.exibirMensagem("=== LOGIN NO SISTEMA ===");

        String email = terminal.lerString("Email: ");
        String senha = terminal.lerString("Senha: ");

        Usuario usuario = usuarioService.autenticar(email, senha);

        if (usuario != null) {
            terminal.exibirMensagem("Login realizado com sucesso!");
            terminal.exibirMensagem("Preparando dados climáticos de São Desidério...");
            notificacaoScheduler.reiniciarScheduler(); // Ativa o agendador no login
            terminal.pausar();
            menuUsuario.exibir(usuario);
        } else {
            terminal.exibirMensagem("Email ou senha incorretos!");
            terminal.pausar();
        }
    }

    private void cadastrarUsuario() {
        terminal.limparTela();
        terminal.exibirMensagem("=== CADASTRO DE USUÁRIO ===");
        terminal.exibirMensagem("Sistema especializado em São Desidério - BA");

        String nome = terminal.lerString("Nome completo: ");
        String email = terminal.lerString("Email: ");
        String senha = terminal.lerString("Senha: ");

        boolean sucesso = usuarioService.cadastrar(nome, email, senha);

        if (sucesso) {
            terminal.exibirMensagem("Usuário cadastrado com sucesso!");
            terminal.exibirMensagem("Bem-vindo ao sistema de monitoramento de São Desidério!");

            Usuario usuario = usuarioService.autenticar(email, senha);
            if (usuario != null) {
                terminal.exibirMensagem("Login automático realizado!");
                notificacaoScheduler.reiniciarScheduler(); // Ativa o agendador no login automático
                terminal.pausar();
                menuUsuario.exibir(usuario);
            }
        } else {
            terminal.exibirMensagem("Erro ao cadastrar usuário. Email já existe!");
            terminal.pausar();
        }
    }

    private void listarUsuarios() {
        terminal.limparTela();
        terminal.exibirMensagem("=== USUÁRIOS CADASTRADOS ===");

        java.util.List<Usuario> usuarios = usuarioService.listarTodos();

        if (usuarios.isEmpty()) {
            terminal.exibirMensagem("Nenhum usuário cadastrado.");
        } else {
            terminal.exibirMensagem("Total de usuários: " + usuarios.size());
            terminal.exibirMensagem("---------------------------");
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario usuario = usuarios.get(i);
                String status = usuario.isLogado() ? "Online" : "Offline";
                terminal.exibirMensagem((i + 1) + ". " + usuario.getNome() +
                        " (" + usuario.getEmail() + ") " + status);
            }
        }

        terminal.pausar();
    }

    private void testarConexaoAPI() {
        terminal.limparTela();
        terminal.exibirMensagem("=== TESTE DE CONEXÃO COM API ===");
        terminal.exibirMensagem("Testando conexão com OpenWeatherMap...");
        terminal.exibirMensagem("");

        try {
            boolean status = climaService.verificarStatusAPI();

            if (status) {
                terminal.exibirMensagem("API funcionando corretamente!");
                terminal.exibirMensagem("Conexão com OpenWeatherMap: OK");
                terminal.exibirMensagem("Dados de São Desidério: Disponíveis");

                // Mostra dados de exemplo
                var clima = climaService.obterDadosClimaticos();
                terminal.exibirMensagem("");
                terminal.exibirMensagem("Dados de teste obtidos:");
                terminal.exibirMensagem("   Temperatura: " + String.format("%.1f°C", clima.getTemperatura()));
                terminal.exibirMensagem("   Localização: " + clima.getLocalizacao());

            } else {
                terminal.exibirMensagem("Problema na conexão com a API!");
                terminal.exibirMensagem("Possíveis causas:");
                terminal.exibirMensagem("   - Chave da API inválida");
                terminal.exibirMensagem("   - Sem conexão com internet");
                terminal.exibirMensagem("   - Serviço temporariamente indisponível");
            }

        } catch (Exception e) {
            terminal.exibirMensagem("Erro no teste: " + e.getMessage());
        }

        terminal.pausar();
    }
}