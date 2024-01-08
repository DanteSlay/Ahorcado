# Juego del Ahorcado

Este es un juego de ahorcado desarrollado en Java que admite múltiples modos de juego, incluyendo partidas individuales, partidas de administrador y partidas locales de dos jugadores.
Cada partida incorpora un temporizador de 10 segundos por acción, si el temporizador llega a 0 cuenta como fallo.

---

## Usuarios

Existen solo 2 usuarios con los que interactuar, admin (contraseña: admin) y user (contraseña: user).
Los modos de juego dependerán del rol del usuario:
- Admin:
  - Partida de Administrador.
- User:
  - Partida Individual.
  - Modo 2 Jugadores Locales.

## Modos de Juego

### 1. Partida Individual (User)

En este modo, los usuarios juegan individualmente contra la máquina. El juego selecciona palabras al azar y el usuario intenta adivinarlas. Cada vez que se inicia una nueva partida, se elige una palabra aleatoria.

### 2. Partida de Administrador (Admin)

En este modo, el administrador puede jugar partidas especiales con información adicional (palabra oculta, número de partidas jugadas, número de fallos, letras acertadas...). Se registran estadísticas detalladas para análisis.

### 3. Partida de Dos Jugadores Locales (User)

Este modo permite que dos jugadores locales participen, donde uno elige una palabra y el otro intenta adivinarla. Si el adivinador tiene éxito, gana un punto y cambia al siguiente turno.

## Uso de Cookies

El juego utiliza cookies para almacenar el número de fallos. Estas cookies se utilizan para cambiar la imagen del ahorcado dependiendo de los fallos.

## Tecnologias Utilizadas

- Java 17.
- Lombok.
- jQuery
- Spring Boot Security
- Thymeleaf
- Spring Boot Web
- JavaScript
- CSS
- Maven para la compilación y gestión de dependencias.
