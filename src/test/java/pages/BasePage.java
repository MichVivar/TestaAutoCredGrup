package pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BasePage extends PageObject{
    
    public BasePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Método para hacer CLICK seguro.
     * Espera a que el botón sea clicable antes de intentarlo.
     */
    public void clickear(By localizador) {
        element(localizador).waitUntilClickable().click();
    }

    /**
     * Método para ESCRIBIR seguro.
     * Espera a que sea visible, BORRA lo que haya y escribe.
     */
    public void escribe(By localizador, String texto) {
        // 1. Esperamos a que esté listo
        element(localizador).waitUntilVisible();
        
        // 2. Limpieza manual profunda
        element(localizador).clear();
        
        // 3. Escribimos
        element(localizador).type(texto);
    }

    /**
     * Método para VALIDAR VISIBILIDAD.
     * Verifica si el elemento es visible para el usuario.
     * * @param localizador El By (XPath, ID, etc.)
     * @return true si es visible, false si no aparece tras el tiempo de espera.
     */
    public boolean estaVisible(By localizador) {
        // element(localizador) crea el WebElementFacade de Serenity.
        // isVisible() maneja la espera implícita y retorna true/false sin lanzar excepción.
        return element(localizador).isVisible();
    }

    /**
     * Método genérico para OBTENER el valor de cualquier atributo HTML.
     * Útil para: placeholders, href, src, value, class, etc.
     * @param localizador El elemento
     * @param atributo El nombre del atributo (ej: "placeholder")
     * @return El texto del atributo
     */
    public String obtenerAtributo(By localizador, String atributo) {
        return element(localizador).getAttribute(atributo);
    }
    
}
