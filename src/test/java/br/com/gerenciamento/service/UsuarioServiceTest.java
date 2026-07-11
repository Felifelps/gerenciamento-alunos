package br.com.gerenciamento.service;

import br.com.gerenciamento.exception.EmailExistsException;
import br.com.gerenciamento.model.Usuario;
import br.com.gerenciamento.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UsuarioServiceTest {

    @Autowired
    private ServiceUsuario serviceUsuario;

    private Usuario criarUsuario(String email, String user, String senha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setUser(user);
        usuario.setSenha(senha);
        return usuario;
    }

    @Test
    public void salvarUsuarioComEmailDuplicadoLancaExcecao() throws Exception {
        Usuario usuario = criarUsuario("duplicado@teste.com", "usuario1", "senha123");
        this.serviceUsuario.salvarUsuario(usuario);

        Usuario usuarioDuplicado = criarUsuario("duplicado@teste.com", "usuario2", "senha456");
        Assert.assertThrows(EmailExistsException.class, () -> {
            this.serviceUsuario.salvarUsuario(usuarioDuplicado);
        });
    }

    @Test
    public void salvarUsuarioComSucessoCriptografaSenha() throws Exception {
        Usuario usuario = criarUsuario("novo.usuario@teste.com", "novoUsuario", "minhaSenha");
        this.serviceUsuario.salvarUsuario(usuario);
        Assert.assertNotEquals("minhaSenha", usuario.getSenha());
    }

    @Test
    public void loginComCredenciaisValidasRetornaUsuario() throws Exception {
        Usuario usuario = criarUsuario("login.valido@teste.com", "loginValido", "senhaValida");
        this.serviceUsuario.salvarUsuario(usuario);

        Usuario usuarioLogado = this.serviceUsuario.loginUser("loginValido", Util.md5("senhaValida"));
        Assert.assertNotNull(usuarioLogado);
        Assert.assertEquals("login.valido@teste.com", usuarioLogado.getEmail());
    }

    @Test
    public void loginComCredenciaisInvalidasRetornaNulo() {
        Usuario usuarioLogado = this.serviceUsuario.loginUser("usuarioInexistente", "senhaErrada");
        Assert.assertNull(usuarioLogado);
    }
}
