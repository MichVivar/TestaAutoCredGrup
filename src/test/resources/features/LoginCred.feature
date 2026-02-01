Feature: Autenticación de Usuario
  # Descripción: Probamos seguridad primero, luego acceso.

  # 1. PRIMERO: Los intentos fallidos (Tu "Escenario de fallos")
  # Usamos un esquema para probar varias combinaciones malas rápido
  @seguridad @negative
  Scenario Outline: El sistema bloquea credenciales incorrectas
    Given El asesor se encuentra en la página de inicio de sesión de TeonCred
    When Ingresa el usuario "<user>" y la contraseña "<password>"
    And Clic en el botón de Acceder
    Then Debería ver el mensaje de error

    Examples:
      | user          | password |
      | 22245         | DDF00301 |
      | 22223         | Da100301 |

  # 2. SEGUNDO: El camino feliz (Tu "Escenario feliz")
  # Este va al final porque es el que "cierra" el ciclo de pruebas del Login
  @happy_path @smoke
  Scenario: Ingreso exitoso para acceder al sistema
    Given El asesor se encuentra en la página de inicio de sesión de TeonCred
    When Ingresa el usuario "20405" y la contraseña "Da100301"
    And Clic en el botón de Acceder
    Then El asesor debería ser redirigido al panel principal de TeonCred