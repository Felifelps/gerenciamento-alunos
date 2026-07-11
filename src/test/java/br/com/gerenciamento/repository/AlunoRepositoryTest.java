package br.com.gerenciamento.repository;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.datasource.url=jdbc:hsqldb:mem:alunorepositorytest")
public class AlunoRepositoryTest {

    @Autowired
    private AlunoRepository alunoRepository;

    private Aluno criarAluno(String nome, String matricula, Status status) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setMatricula(matricula);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setTurno(Turno.MATUTINO);
        aluno.setStatus(status);
        return aluno;
    }

    @Test
    public void findByStatusInativoRetornaApenasAlunoInativo() {
        Aluno alunoAtivo = criarAluno("AtivoRepoTeste", "REPO-001", Status.ATIVO);
        Aluno alunoInativo = criarAluno("InativoRepoTeste", "REPO-002", Status.INATIVO);
        this.alunoRepository.save(alunoAtivo);
        this.alunoRepository.save(alunoInativo);

        List<Aluno> inativos = this.alunoRepository.findByStatusInativo();

        Assert.assertTrue(inativos.stream().anyMatch(a -> a.getNome().equals("InativoRepoTeste")));
        Assert.assertTrue(inativos.stream().noneMatch(a -> a.getNome().equals("AtivoRepoTeste")));
    }

    @Test
    public void findByStatusAtivoRetornaApenasAlunoAtivo() {
        Aluno alunoAtivo = criarAluno("AtivoRepoTeste2", "REPO-003", Status.ATIVO);
        Aluno alunoInativo = criarAluno("InativoRepoTeste2", "REPO-004", Status.INATIVO);
        this.alunoRepository.save(alunoAtivo);
        this.alunoRepository.save(alunoInativo);

        List<Aluno> ativos = this.alunoRepository.findByStatusAtivo();

        Assert.assertTrue(ativos.stream().anyMatch(a -> a.getNome().equals("AtivoRepoTeste2")));
        Assert.assertTrue(ativos.stream().noneMatch(a -> a.getNome().equals("InativoRepoTeste2")));
    }

    @Test
    public void findByNomeContainingIgnoreCaseEncontraAlunoSalvo() {
        Aluno aluno = criarAluno("NomeBuscavelRepo", "REPO-005", Status.ATIVO);
        this.alunoRepository.save(aluno);

        List<Aluno> resultado = this.alunoRepository.findByNomeContainingIgnoreCase("nomebuscavelrepo");

        Assert.assertTrue(resultado.stream().anyMatch(a -> a.getNome().equals("NomeBuscavelRepo")));
    }

    @Test
    public void saveERecuperarPorIdFuncionaCorretamente() {
        Aluno aluno = criarAluno("AlunoParaBuscarPorId", "REPO-006", Status.ATIVO);
        Aluno alunoSalvo = this.alunoRepository.save(aluno);

        Aluno alunoRecuperado = this.alunoRepository.findById(alunoSalvo.getId()).orElse(null);

        Assert.assertNotNull(alunoRecuperado);
        Assert.assertEquals("AlunoParaBuscarPorId", alunoRecuperado.getNome());
    }
}
