package com.example.climatemonitoring.service;

import com.example.climatemonitoring.data.NotificacaoRepository;
import com.example.climatemonitoring.models.Notificacao;
import com.example.climatemonitoring.models.Usuario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    private NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public boolean registrarNotificacao(String mensagem) {
        Notificacao notificacao = new Notificacao(mensagem);
        return notificacaoRepository.adicionar(notificacao);
    }
    
    public boolean registrarNotificacaoUsuario(String mensagem, String emailUsuario) {
        Notificacao notificacao = new Notificacao(mensagem, emailUsuario);
        return notificacaoRepository.adicionar(notificacao);
    }

    public List<Notificacao> listarHistorico() {
        return notificacaoRepository.listarTodos();
    }
    
    public List<Notificacao> listarHistoricoUsuario(String emailUsuario) {
        List<Notificacao> todas = notificacaoRepository.listarTodos();
        return todas.stream()
                .filter(n -> emailUsuario.equals(n.getEmailUsuario()))
                .collect(Collectors.toList());
    }

    public boolean enviarNotificacaoAutomatica(Usuario usuario, String mensagem) {
        String mensagemFormatada = String.format("Olá %s, %s", usuario.getNome(), mensagem);
        return registrarNotificacaoUsuario(mensagemFormatada, usuario.getEmail());
    }
}