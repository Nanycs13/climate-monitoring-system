package com.example.climatemonitoring.data;

import com.example.climatemonitoring.models.Usuario;
import com.example.climatemonitoring.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.gson.reflect.TypeToken;


@Repository
public class UsuarioRepository {
    private static final String ARQUIVO_USUARIOS = "dados/usuarios.json";
    private static final Type TIPO_LISTA = new TypeToken<List<Usuario>>() {
    }.getType();


    public UsuarioRepository() {
        File diretorio = new File("dados");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }


    public List<Usuario> listarTodos() {
        try {
            File arquivo = new File(ARQUIVO_USUARIOS);
            if (!arquivo.exists()) {
                return new ArrayList<>();
            }
            return JsonUtil.readJsonList(ARQUIVO_USUARIOS, TIPO_LISTA);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de usuários: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public boolean salvarTodos(List<Usuario> usuarios) {
        try {
            JsonUtil.writeJsonList(ARQUIVO_USUARIOS, usuarios);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de usuários: " + e.getMessage());
            return false;
        }
    }


    public Usuario buscarPorEmail(String email) {
        List<Usuario> usuarios = listarTodos();
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }
        return null;
    }


    public boolean adicionar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();

        // Verifica se já existe um usuário com o mesmo email
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(usuario.getEmail())) {
                return false;
            }
        }

        usuarios.add(usuario);
        return salvarTodos(usuarios);

    }

    public boolean atualizar(Usuario usuarioAtualizado) {
        List<Usuario> usuarios = listarTodos();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equals(usuarioAtualizado.getEmail())) {
                usuarios.set(i, usuarioAtualizado);
                return salvarTodos(usuarios);
            }
        }
        return false;
    }
}
