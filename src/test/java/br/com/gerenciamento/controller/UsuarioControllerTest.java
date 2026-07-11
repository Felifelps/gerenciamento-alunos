package br.com.gerenciamento.controller;

import br.com.gerenciamento.model.Usuario;
import br.com.gerenciamento.service.ServiceUsuario;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceUsuario serviceUsuario;

    private void cadastrarUsuario(String email, String user, String senha) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setUser(user);
        usuario.setSenha(senha);
        this.serviceUsuario.salvarUsuario(usuario);
    }

    @Test
    public void loginComCredenciaisInvalidasNaoRedirecionaParaIndex() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/login")
                        .param("user", "usuarioQueNaoExiste")
                        .param("senha", "senhaErrada"))
                .andExpect(status().isOk())
                .andReturn();

        String viewName = result.getModelAndView().getViewName();
        Assert.assertNotEquals("home/index", viewName);
    }

    @Test
    public void loginComCredenciaisValidasRetornaViewIndex() throws Exception {
        cadastrarUsuario("controller.login@teste.com", "controllerLoginUser", "senhaValida");

        MvcResult result = this.mockMvc.perform(post("/login")
                        .param("user", "controllerLoginUser")
                        .param("senha", "senhaValida"))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertEquals("home/index", result.getModelAndView().getViewName());
    }

    @Test
    public void cadastrarUsuarioComSucessoRedirecionaParaLogin() throws Exception {
        this.mockMvc.perform(post("/salvarUsuario")
                        .param("email", "controller.cadastro@teste.com")
                        .param("user", "cadastroUser")
                        .param("senha", "senhaCadastro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void paginaLoginCarregaComSucesso() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }
}
