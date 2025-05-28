package com.example.climatemonitoring.service;

import com.example.climatemonitoring.data.UsuarioRepository;
import com.example.climatemonitoring.models.Usuario;
import org.springframework.stereotype.Service;
import com.example.climatemonitoring.utils.EmailValidatorUtil; // Importa o validador

import java.util.List;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario != null && senha.equals(usuario.getSenha())) {
            usuario.setLogado(true);
            usuarioRepository.atualizar(usuario);
            return usuario;
        }
        return null;
    }

    public boolean cadastrar(String nome, String email, String senha) {
        if (!EmailValidatorUtil.isEmailValido(email)) {
            return false;
        }

        if (usuarioRepository.buscarPorEmail(email) != null) {
            return false;
        }

        Usuario novoUsuario = new Usuario(nome, email, senha);
        return usuarioRepository.adicionar(novoUsuario);
    }

    public List<Usuario> getUsuariosLogados() {
        List<Usuario> todos = usuarioRepository.listarTodos();
        return todos.stream()
                .filter(Usuario::isLogado)
                .toList();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }
}