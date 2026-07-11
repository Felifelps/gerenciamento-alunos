package br.com.gerenciamento.controller;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import br.com.gerenciamento.service.ServiceAluno;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "spring.datasource.url=jdbc:hsqldb:mem:alunocontrollertest")
public class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceAluno serviceAluno;

    private Aluno criarESalvarAluno(String nome) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setMatricula("CTRL-001");
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setTurno(Turno.MATUTINO);
        aluno.setStatus(Status.ATIVO);
        this.serviceAluno.save(aluno);
        return aluno;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void pesquisarAlunoComNomeVazioRetornaListagemCompleta() throws Exception {
        criarESalvarAluno("AlunoParaListagemCompleta");

        MvcResult result = this.mockMvc.perform(post("/pesquisar-aluno").param("nome", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("Aluno/pesquisa-resultado"))
                .andReturn();

        List<Aluno> listaRetornada = (List<Aluno>) result.getModelAndView().getModel().get("ListaDeAlunos");

        Assert.assertEquals(this.serviceAluno.findAll().size(), listaRetornada.size());
        Assert.assertTrue(listaRetornada.stream().anyMatch(a -> "AlunoParaListagemCompleta".equals(a.getNome())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void pesquisarAlunoComNomeEncontraAlunoEspecifico() throws Exception {
        criarESalvarAluno("NomeUnicoParaPesquisaController");

        MvcResult result = this.mockMvc.perform(post("/pesquisar-aluno").param("nome", "NomeUnicoParaPesquisaController"))
                .andExpect(status().isOk())
                .andExpect(view().name("Aluno/pesquisa-resultado"))
                .andReturn();

        List<Aluno> listaRetornada = (List<Aluno>) result.getModelAndView().getModel().get("ListaDeAlunos");

        Assert.assertTrue(listaRetornada.stream().allMatch(a -> a.getNome().equalsIgnoreCase("NomeUnicoParaPesquisaController")));
        Assert.assertTrue(listaRetornada.stream().anyMatch(a -> a.getNome().equals("NomeUnicoParaPesquisaController")));
    }

    @Test
    public void listagemAlunosCarregaComSucesso() throws Exception {
        this.mockMvc.perform(get("/alunos-adicionados"))
                .andExpect(status().isOk())
                .andExpect(view().name("Aluno/listAlunos"));
    }

    @Test
    public void removerAlunoRedirecionaParaListagemERemoveDoBanco() throws Exception {
        Aluno aluno = criarESalvarAluno("AlunoParaRemover");

        this.mockMvc.perform(get("/remover/" + aluno.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos-adicionados"));

        Assert.assertTrue(this.serviceAluno.findAll().stream()
                .noneMatch(a -> a.getId().equals(aluno.getId())));
    }
}
