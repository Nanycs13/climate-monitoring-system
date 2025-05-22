package com.example.climatemonitoring.view;

import org.springframework.stereotype.Component;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class TerminalUtil {
    private Scanner scanner;

    public TerminalUtil() {
        this.scanner = new Scanner(System.in);
    }


    public void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }


    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }


    public void exibirNotificacaoEmTempoReal(String notificacao) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔔 NOTIFICAÇÃO EM TEMPO REAL");
        System.out.println("=".repeat(50));
        System.out.println(notificacao);
        System.out.println("=".repeat(50));
        System.out.println("Pressione ENTER para continuar ou aguarde a próxima...");
    }


    public void exibirAlerta(String alerta) {
        System.out.println("\n" + "🚨".repeat(20));
        System.out.println("⚠️  ALERTA CRÍTICO  ⚠️");
        System.out.println("🚨".repeat(20));
        System.out.println(alerta);
        System.out.println("🚨".repeat(20));
        System.out.println();
    }


    public String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

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


    public void pausar() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }


    public void fechar() {
        if (scanner != null) {
            scanner.close();
        }
    }
}