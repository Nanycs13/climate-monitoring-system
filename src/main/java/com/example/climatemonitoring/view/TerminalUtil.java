package com.example.climatemonitoring.view;

import org.springframework.stereotype.Component;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário para interação com terminal, especializado para
 * monitoramento climático de Luís Eduardo Magalhães.
 * Inclui funcionalidades para notificações em tempo real.
 */
@Component
public class TerminalUtil {
    private Scanner scanner;

    public TerminalUtil() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Limpa a tela do terminal.
     */
    public void limparTela() {
        try {
            // Para Windows
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Para Linux/Mac
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Se não conseguir limpar, pelo menos pula algumas linhas
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Exibe uma mensagem no terminal.
     */
    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    /**
     * Exibe uma notificação em tempo real com destaque.
     */
    public void exibirNotificacaoEmTempoReal(String notificacao) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔔 NOTIFICAÇÃO EM TEMPO REAL");
        System.out.println("=".repeat(50));
        System.out.println(notificacao);
        System.out.println("=".repeat(50));
        System.out.println("Pressione ENTER para continuar ou aguarde a próxima...");
    }

    /**
     * Exibe um alerta crítico com destaque especial.
     */
    public void exibirAlerta(String alerta) {
        System.out.println("\n" + "🚨".repeat(20));
        System.out.println("⚠️  ALERTA CRÍTICO  ⚠️");
        System.out.println("🚨".repeat(20));
        System.out.println(alerta);
        System.out.println("🚨".repeat(20));
        System.out.println();
    }

    /**
     * Lê uma string do usuário.
     */
    public String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Lê um inteiro do usuário com tratamento de erro.
     */
    public int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido!");
            }
        }
    }

    /**
     * Pausa a execução até o usuário pressionar ENTER.
     */
    public void pausar() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    /**
     * Fecha o scanner quando necessário.
     */
    public void fechar() {
        if (scanner != null) {
            scanner.close();
        }
    }
}