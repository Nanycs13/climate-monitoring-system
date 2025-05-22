package com.example.climatemonitoring.data;

import com.example.climatemonitoring.models.Notificacao;
import com.example.climatemonitoring.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.gson.reflect.TypeToken;


@Repository
public class NotificacaoRepository {
    private static final String ARQUIVO_NOTIFICACOES = "dados/notificacoes.json";
    private static final Type TIPO_LISTA = new TypeToken<List<Notificacao>>() {
    }.getType();


    public NotificacaoRepository() {
        File diretorio = new File("dados");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }


    public List<Notificacao> listarTodos() {
        try {
            File arquivo = new File(ARQUIVO_NOTIFICACOES);
            if (!arquivo.exists()) {
                return new ArrayList<>();
            }
            return JsonUtil.readJsonList(ARQUIVO_NOTIFICACOES, TIPO_LISTA);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de notificações: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public boolean salvarTodos(List<Notificacao> notificacoes) {
        try {
            JsonUtil.writeJsonList(ARQUIVO_NOTIFICACOES, notificacoes);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de notificações: " + e.getMessage());
            return false;
        }
    }


    public boolean adicionar(Notificacao notificacao) {
        List<Notificacao> notificacoes = listarTodos();
        notificacoes.add(notificacao);
        return salvarTodos(notificacoes);
    }
}
