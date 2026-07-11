package br.com.gerenciamento.service;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AlunoServiceTest {

    @Autowired
    private ServiceAluno serviceAluno;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void resetIdentity() {
        this.entityManager.createNativeQuery("ALTER TABLE ALUNO ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }

    @Test
    public void getById() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Vinicius");
        aluno.setTurno(Turno.NOTURNO);
        aluno.setCurso(Curso.ADMINISTRACAO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("123456");
        this.serviceAluno.save(aluno);

        Aluno alunoRetorno = this.serviceAluno.getById(1L);
        Assert.assertTrue(alunoRetorno.getNome().equals("Vinicius"));
    }

    @Test
    public void salvarSemNome() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setTurno(Turno.NOTURNO);
        aluno.setCurso(Curso.ADMINISTRACAO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("123456");
        Assert.assertThrows(ConstraintViolationException.class, () -> {
                this.serviceAluno.save(aluno);});
    }

    @Test
    public void findByNomeContainingIgnoreCaseNaoEncontraQuandoNaoExiste() {
        List<Aluno> resultado = this.serviceAluno.findByNomeContainingIgnoreCase("nome");
        Assert.assertEquals(0, resultado.size());
    }

    @Test
    public void findByNomeContainingIgnoreCaseEncontraAlunoSalvo() {
        Aluno aluno = new Aluno();
        aluno.setNome("Vinicius");
        aluno.setTurno(Turno.NOTURNO);
        aluno.setCurso(Curso.ADMINISTRACAO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("123456");
        this.serviceAluno.save(aluno);

        List<Aluno> resultado = this.serviceAluno.findByNomeContainingIgnoreCase("vinicius");
        Assert.assertTrue(resultado.stream().anyMatch(a -> a.getNome().equals("Vinicius")));
    }

    @Test
    public void findAllRetornaAlunoSalvo() {
        Aluno aluno = new Aluno();
        aluno.setNome("AlunoParaFindAll");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("111222");
        this.serviceAluno.save(aluno);

        List<Aluno> todos = this.serviceAluno.findAll();
        Assert.assertTrue(todos.stream().anyMatch(a -> "AlunoParaFindAll".equals(a.getNome())));
    }

    @Test
    public void deleteByIdRemoveAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome("AlunoParaDeletar");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("222333");
        this.serviceAluno.save(aluno);

        this.serviceAluno.deleteById(aluno.getId());

        List<Aluno> todos = this.serviceAluno.findAll();
        Assert.assertTrue(todos.stream().noneMatch(a -> a.getId().equals(aluno.getId())));
    }

    @Test
    public void findByStatusAtivoRetornaAlunoAtivo() {
        Aluno aluno = new Aluno();
        aluno.setNome("AlunoAtivoService");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("333444");
        this.serviceAluno.save(aluno);

        List<Aluno> ativos = this.serviceAluno.findByStatusAtivo();
        Assert.assertTrue(ativos.stream().anyMatch(a -> "AlunoAtivoService".equals(a.getNome())));
    }

    @Test
    public void findByStatusInativoRetornaAlunoInativo() {
        Aluno aluno = new Aluno();
        aluno.setNome("AlunoInativoService");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setStatus(Status.INATIVO);
        aluno.setMatricula("444555");
        this.serviceAluno.save(aluno);

        List<Aluno> inativos = this.serviceAluno.findByStatusInativo();
        Assert.assertTrue(inativos.stream().anyMatch(a -> "AlunoInativoService".equals(a.getNome())));
    }
}
