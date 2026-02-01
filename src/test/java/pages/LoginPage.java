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
        escribe(inputUsuario, usuario);
    }

    public void ingresarPassword(String password) {
        element(passUser).typeAndTab(password);
    }

    public void clickEnLogin() {
        System.out.println("🚀 Forzando clic con JavaScript para asegurar reacción...");
        // Esto se salta cualquier bloqueo de la interfaz
        element(btnLogin).waitUntilClickable().click();
    }
    
    /**
     * MÉTODO DINÁMICO
     * Tú le dices "Usuario" y él sabe qué localizador buscar y devuelve el texto.
     */
    public String obtenerPlaceholderDe(String campo) {
        By localizadorSeleccionado = null;

        // Un simple switch para elegir el elemento
        switch (campo.toLowerCase()) {
            case "usuario":
                localizadorSeleccionado = inputUsuario;
                break;
            case "contraseña":
                localizadorSeleccionado = passUser;
                break;
            default:
                throw new IllegalArgumentException("No tengo el localizador para el campo: " + campo);
        }

        // Reutilizas tu método genérico de BasePage
        return obtenerAtributo(localizadorSeleccionado, "placeholder");
    }

    public void validarMensajePresente() {
        // 1. Espera dinámica (reemplaza al sleep)
        waitForCondition().until(
            driver -> !element(msgError).getText().trim().isEmpty()
        );

        // 2. Capturamos el texto y lo pasamos a minúsculas para comparar
        String textoReal = element(msgError).getText().toLowerCase();
        String textoEsperado = "no válidos".toLowerCase();

        // 3. Validación robusta
        if (!textoReal.contains(textoEsperado)) {
            throw new AssertionError("El mensaje de error no coincide. Se esperaba algo que contenga '" 
                + textoEsperado + "' pero se encontró: '" + textoReal + "'");
        }
        
        System.out.println("✅ Validación exitosa: " + textoReal);
    }

}
