package com.example.climatemonitoring.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notificacao {
    private String mensagem;
    private String emailUsuario;
    private String timestamp;

    public Notificacao(String mensagem) {
        this.mensagem = mensagem;
        this.emailUsuario = null;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public Notificacao(String mensagem, String emailUsuario) {
        this.mensagem = mensagem;
        this.emailUsuario = emailUsuario;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Notificacao{" +
                "mensagem='" + mensagem + '\'' +
                ", emailUsuario='" + emailUsuario + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}