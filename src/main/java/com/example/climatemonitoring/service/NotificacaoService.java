package com.example.climatemonitoring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.climatemonitoring.data.NotificacaoRepository;
import com.example.climatemonitoring.models.Notificacao;
import com.example.climatemonitoring.models.Usuario;

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
        if (emailUsuario == null || emailUsuario.trim().isEmpty()) {
            // Se não tem email, registra como notificação geral
            return registrarNotificacao(mensagem);
        }
        Notificacao notificacao = new Notificacao(mensagem, emailUsuario);
        return notificacaoRepository.adicionar(notificacao);
    }

    public List<Notificacao> listarHistorico() {
        return notificacaoRepository.listarTodos();
    }
    
    public List<Notificacao> listarHistoricoUsuario(String emailUsuario) {
        if (emailUsuario == null || emailUsuario.trim().isEmpty()) {
            return List.of(); // Retorna lista vazia se email inválido
        }
        
        List<Notificacao> todas = notificacaoRepository.listarTodos();
        return todas.stream()
                .filter(n -> emailUsuario.equals(n.getEmailUsuario()))
                .collect(Collectors.toList());
    }

    public boolean enviarNotificacaoAutomatica(Usuario usuario, String mensagem) {
        if (usuario == null || usuario.getEmail() == null) {
            return registrarNotificacao(mensagem);
        }
        String mensagemFormatada = String.format("Olá %s, %s", usuario.getNome(), mensagem);
        return registrarNotificacaoUsuario(mensagemFormatada, usuario.getEmail());
    }
}