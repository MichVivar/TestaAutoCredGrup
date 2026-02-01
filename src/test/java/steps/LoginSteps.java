package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Managed;
import pages.BasePage;
import pages.LoginPage;

import javax.swing.JOptionPane;

import org.openqa.selenium.WebDriver;

public class LoginSteps {

    @Managed
    WebDriver driver;

    BasePage basePage;
    LoginPage loginPage;

    @Given("El asesor se encuentra en la página de inicio de sesión de TeonCred")
    public void abrirPagina() {
        loginPage.open(); // Abre la URL base definida en serenity.conf
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
    }

    @When("Ingresa el usuario {string} y la contraseña {string}")
    public void ingresarCredenciales(String user, String password) {
        loginPage.ingresarUsuario(user);
        loginPage.ingresarPassword(password);
    }

    @And("Clic en el botón de Acceder")
    public void hacerClicEnBotonIniciarSesion() {
        loginPage.clickEnLogin();
    }

    @Then("Debería ver el mensaje de error")
    public void validarElMensajeDeError() {
        loginPage.validarMensajePresente();
    }

    @Then("El asesor debería ser redirigido al panel principal de TeonCred")
    public void validarPanelPrincipal() {
        JOptionPane.showMessageDialog(null, "El navegador está abierto. \nDale clicka OK aquí para cerrar todo.");
    }

}