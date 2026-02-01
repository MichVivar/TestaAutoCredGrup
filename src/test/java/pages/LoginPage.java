package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // =======================================================
    // Locators
    // =======================================================
    private By inputUsuario = By.id("usuario");
    private By passUser = By.id("password");
    private By btnLogin = By.xpath("//button[@type='submit']");
    private By msgError = By.id("error-message");


    // =======================================================
    // FUNCIONES 
    // =======================================================
    public void ingresarUsuario(String usuario) {
        escribe(inputUsuario, usuario,"Usuario");
    }

    public void ingresarPassword(String password) {
        escribe(passUser, password,"Contraseña");
    }

    public void clickEnLogin() {
        clickear(btnLogin);
    }
    
    public void validarMensajePresente() {
        validarTextoEnElemento(msgError, "no válidos");
    }

}
