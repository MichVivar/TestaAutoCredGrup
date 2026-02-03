package pages;

import net.serenitybdd.core.pages.PageObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.SecurityUtil;

public class BasePage extends PageObject {

    public BasePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Método para hacer CLICK 
     */
    public void clickear(By localizador) {
        element(localizador).waitUntilPresent().waitUntilVisible().waitUntilClickable();
        try {
            element(localizador).click();
        } catch (Exception e) {
            System.out.println("⚠️ Clic estándar falló, reintentando con JavaScript para: " + localizador);
            evaluateJavascript("arguments[0].click();", element(localizador));
        }
    }

    /**
     * Método para ESCRIBIR 
     * Espera a que sea visible, BORRA lo que haya y escribe.
     */
    public void escribe(By localizador, String texto, String placeholderEsperado) {
        element(localizador).waitUntilVisible();
        if (placeholderEsperado != null && !placeholderEsperado.isEmpty()) {
            validarPlaceholderSoft(localizador, placeholderEsperado);
        }
        element(localizador).clear();
        element(localizador).type(texto);
    }

    /**
     * Método para VALIDAR VISIBILIDAD.
     * Verifica si el elemento es visible para el usuario.
     * * @param localizador El By (XPath, ID, etc.)
     * 
     * @return true si es visible, false si no aparece tras el tiempo de espera.
     */
    public boolean estaVisible(By localizador) {
        // element(localizador) crea el WebElementFacade de Serenity.
        // isVisible() maneja la espera implícita y retorna true/false sin lanzar
        // excepción.
        return element(localizador).isVisible();
    }

    /**
     * Método genérico para OBTENER el valor de cualquier atributo HTML.
     * Útil para: placeholders, href, src, value, class, etc.
     * @param localizador El elemento
     * @param atributo    El nombre del atributo (ej: "placeholder")
     * @return El texto del atributo
     */
    public String obtenerAtributo(By localizador, String atributo) {
        return element(localizador).getAttribute(atributo);
    }

    /**
     * Valida que un elemento contenga un texto específico después de esperar a que
     * aparezca.
     * 
     * @param localizador   El By del elemento (error, éxito, alert, etc.)
     * @param textoEsperado El texto que queremos confirmar
     */
    public void validarTextoEnElemento(By localizador, String textoEsperado) {
        waitForCondition().until(
                driver -> !element(localizador).getText().trim().isEmpty());

        // 2. Captura y normalización
        String textoReal = element(localizador).getText().toLowerCase();
        String textoEsperadoNorm = textoEsperado.toLowerCase();

        // 3. Aserción robusta con mensaje personalizado por si falla
        assertThat("❌ El elemento " + localizador + " no contiene el texto esperado.",
                textoReal, containsString(textoEsperadoNorm));

        System.out.println("✅ Validación exitosa en " + localizador + ": " + textoReal);
        waitABit(500);
    }

    /**
     * Valida el placeholder de un campo (Soft Validation).
     * Si el placeholder es incorrecto, se reporta pero el test continúa.
     * 
     * @param localizador El By del input
     * @param esperado    El texto que debería decir el placeholder
     */
    public void validarPlaceholderSoft(By localizador, String esperado) {
        try {
            String real = element(localizador).getAttribute("placeholder");

            // Si es nulo, lo tratamos como vacío para evitar errores
            if (real == null)
                real = "";

            if (!real.equalsIgnoreCase(esperado)) {
                // Imprimimos un mensaje en la consola y el reporte
                System.err.println("⚠️ VALIDACIÓN MENOR FALLIDA: El campo " + localizador
                        + " debería decir '" + esperado + "' pero dice '" + real + "'");

                // Serenity registrará esto como un "paso fallido" pero el test NO se detiene
                // porque no estamos lanzando una excepción (AssertionError).
            } else {
                System.out.println("✅ Placeholder correcto: " + real);
            }
        } catch (Exception e) {
            System.out.println("ℹ️ El campo no tiene atributo placeholder o no se pudo leer, ignorando...");
        }
    }

    protected String obtenerDatoSeguro(String datoCifrado) {
        return SecurityUtil.decrypt(datoCifrado);
    }
        

}
