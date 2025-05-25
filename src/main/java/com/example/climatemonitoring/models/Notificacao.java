package com.example.climatemonitoring.models;

public class Notificacao {
    private String mensagem;
    private String emailUsuario;

    public Notificacao(String mensagem) {
        this.mensagem = mensagem;
        this.emailUsuario = null;
    }

    public Notificacao(String mensagem, String emailUsuario) {
        this.mensagem = mensagem;
        this.emailUsuario = emailUsuario;
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

    @Override
    public String toString() {
        return "Notificacao{" +
                "mensagem='" + mensagem + '\'' +
                ", emailUsuario='" + emailUsuario + '\'' +
                '}';
    }
}