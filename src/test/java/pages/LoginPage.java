package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utils.SecurityUtil;


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
        String usuarioReal = SecurityUtil.decrypt(usuario);
        escribe(inputUsuario, usuarioReal,"Usuario");
    }

    public void ingresarPassword(String password) {
        String passReal = SecurityUtil.decrypt(password);
        escribe(passUser, passReal,"Contraseña");
    }

    public void clickEnLogin() {
        clickear(btnLogin);
    }
    
    public void validarMensajePresente() {
        validarTextoEnElemento(msgError, "no válidos");
    }

}
