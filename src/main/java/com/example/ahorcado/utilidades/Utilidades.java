package com.example.ahorcado.utilidades;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase con metodos estaticos que se podran usar en todo el proyecto sin instanciar la clase
 */
public class Utilidades {
    /**
     * Genera una cookie llamada "fallos" con el número de fallos especificado y la configura para que expire en 2 horas.
     *
     * @param fallos   El número de fallos que se almacenará en la cookie.
     * @param response El objeto HttpServletResponse utilizado para agregar la cookie a la respuesta.
     */
    public static void generarCookieFallos(int fallos, HttpServletResponse response) {
        Cookie cookie = new Cookie("fallos", String.valueOf(fallos));
        cookie.setMaxAge(120 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
