package com.example.climatemonitoring.service;

import com.example.climatemonitoring.data.NotificacaoRepository;
import com.example.climatemonitoring.models.Notificacao;
import com.example.climatemonitoring.models.Usuario;

import java.util.List;

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

    public List<Notificacao> listarHistorico() {
        return notificacaoRepository.listarTodos();
    }

    // ✅ Novo método solicitado:
    public boolean enviarNotificacaoAutomatica(Usuario usuario, String mensagem) {
        String mensagemFormatada = String.format("Olá %s, %s", usuario.getNome(), mensagem);
        return registrarNotificacao(mensagemFormatada);
    }
}
