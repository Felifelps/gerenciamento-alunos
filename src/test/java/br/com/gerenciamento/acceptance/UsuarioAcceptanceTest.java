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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:hsqldb:mem:usuarioacceptancetest")
public class UsuarioAcceptanceTest {

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
    public void cadastrarUsuarioELogarComSucessoViaBrowser() {
        this.driver.get(this.baseUrl + "cadastro");

        this.driver.findElement(By.id("email")).sendKeys("aceitacao.usuario@teste.com");
        this.driver.findElement(By.id("user")).sendKeys("usuarioAceitacao");
        this.driver.findElement(By.id("senha")).sendKeys("senhaAceitacao");
        this.driver.findElement(By.cssSelector("form button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleContains("Login"));

        this.driver.findElement(By.id("user")).sendKeys("usuarioAceitacao");
        this.driver.findElement(By.id("senha")).sendKeys("senhaAceitacao");
        this.driver.findElement(By.cssSelector("form button[type='submit']")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("h1"), "Sistema de Gerenciamento de Alunos"));

        Assert.assertTrue(this.driver.findElements(By.linkText("CADASTRAR ALUNO")).size() > 0);
    }
}
