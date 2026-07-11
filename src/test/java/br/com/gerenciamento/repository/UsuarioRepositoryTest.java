package br.com.gerenciamento.repository;

import br.com.gerenciamento.model.Usuario;
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
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario criarUsuario(String email, String user, String senha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setUser(user);
        usuario.setSenha(senha);
        return usuario;
    }

    @Test
    public void buscarLoginComCredenciaisCorretasRetornaUsuario() {
        Usuario usuario = criarUsuario("repo.login@teste.com", "repoLoginUser", "senhaRepo");
        this.usuarioRepository.save(usuario);

        Usuario resultado = this.usuarioRepository.buscarLogin("repoLoginUser", "senhaRepo");

        Assert.assertNotNull(resultado);
        Assert.assertEquals("repo.login@teste.com", resultado.getEmail());
    }

    @Test
    public void buscarLoginComSenhaErradaRetornaNulo() {
        Usuario usuario = criarUsuario("repo.login2@teste.com", "repoLoginUser2", "senhaCorreta");
        this.usuarioRepository.save(usuario);

        Usuario resultado = this.usuarioRepository.buscarLogin("repoLoginUser2", "senhaErrada");

        Assert.assertNull(resultado);
    }

    @Test
    public void findByEmailEncontraUsuarioCadastrado() {
        Usuario usuario = criarUsuario("repo.email@teste.com", "repoEmailUser", "senhaX");
        this.usuarioRepository.save(usuario);

        Usuario resultado = this.usuarioRepository.findByEmail("repo.email@teste.com");

        Assert.assertNotNull(resultado);
        Assert.assertEquals("repoEmailUser", resultado.getUser());
    }

    @Test
    public void findByEmailInexistenteRetornaNulo() {
        Usuario resultado = this.usuarioRepository.findByEmail("email.que.nao.existe@teste.com");
        Assert.assertNull(resultado);
    }
}
