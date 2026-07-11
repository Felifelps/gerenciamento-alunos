package br.com.gerenciamento.acceptance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:hsqldb:mem:alunoacceptancetest")
public class AlunoAcceptanceTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,800");
        this.driver = new ChromeDriver(options);
        this.baseUrl = "http://localhost:" + this.port + "/";
    }

    @After
    public void tearDown() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    @Test
    public void cadastrarAlunoComSucessoViaBrowser() {
        this.driver.get(this.baseUrl + "inserirAlunos");

        this.driver.findElement(By.id("nome")).sendKeys("Aluno Aceitacao Selenium");
        new Select(this.driver.findElement(By.id("curso"))).selectByValue("INFORMATICA");
        this.driver.findElement(By.id("matricula")).sendKeys("SEL-001");
        new Select(this.driver.findElement(By.id("turno"))).selectByValue("MATUTINO");
        new Select(this.driver.findElement(By.id("status"))).selectByValue("ATIVO");
        this.driver.findElement(By.cssSelector("form[action='/InsertAlunos'] button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("h3"), "Últimos Alunos Adicionados"));

        Assert.assertTrue(this.driver.getPageSource().contains("Aluno Aceitacao Selenium"));
    }
}
