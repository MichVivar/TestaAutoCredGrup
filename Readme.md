# 🏗️ TeonCred Automation Core (Legacy Armor)

### **[ PROJECT STATUS: ROBUST / CI-READY ]**

Este repositorio contiene el **Framework de Automatización de Grado Industrial** basado en Java, diseñado para la validación crítica del portal TeonCred. A diferencia de scripts convencionales, este sistema implementa capas de seguridad y abstracción que garantizan la integridad de los datos de prueba en entornos de ejecución distribuida (CI/CD).

---

## 🔬 Arquitectura Técnica

El sistema se apoya en tres pilares fundamentales para garantizar la estabilidad de la "barda" de calidad:

1.  **Serenity BDD + Cucumber:** Gestión de pruebas basada en comportamiento que traduce requisitos de negocio en reportes vivos y detallados.
2.  **Screenplay Pattern (Mentalidad Middle-Senior):** Desacoplamiento total entre los Actores (quien prueba), las Tareas (qué hace) y las Interacciones (cómo lo hace).
3.  **Security Layer (Base64 Shield):** Implementación de utilerías de encriptación simétrica para el manejo de credenciales en archivos `.feature` y `.csv`, eliminando el riesgo de "texto plano" en el repositorio.

---

## 🔑 Características de Seguridad y Portabilidad

* **Universal Deserializer:** Utilería `SecurityUtil` con manejo de excepciones `try-catch` diseñada para operar en cualquier SO (Mac/Windows/Linux) sin colapsar ante datos no codificados.
* **Environment Agnostic:** Configuración mediante rutas relativas y variables de entorno, preparada para correr en **GitHub Actions** o contenedores Docker sin intervención manual.
* **Data Driven Testing:** Inyección masiva de escenarios mediante tablas de `Examples` y archivos CSV externos.

---

## 🛠️ Estructura del Proyecto

```text
src/
├── test/
│   ├── java/
│   │   ├── pages/        # Locators y Mapeo de Objetos (POM)
│   │   ├── steps/        # Step Definitions (Glue Code)
│   │   └── utils/        # Motor de Seguridad y Herramientas
│   └── resources/
│       ├── data/         # Insumos (CSVs Encriptados)
│       └── features/     # Historias de Usuario (Gherkin)

```

## 🚀 Guía de Ejecución Rápida

Para levantar la obra y ver los resultados, asegúrate de tener el entorno nivelado (Java 17+ y Gradle):

# Limpiar y ejecutar toda la suite de pruebas
gradle clean test

# Generar el reporte consolidado de Serenity
gradle aggregate

* **Nota de Auditoría:** Las credenciales de acceso deben estar encriptadas mediante la utilería SecurityUtil.encrypt() antes de ser añadidas al archivo de insumos.

* **Arquitecto:** Mich Vivar & Gemini (Collaborative Lab)

* **Fase:** V1.5 - Estabilidad y Blindaje de Datos.