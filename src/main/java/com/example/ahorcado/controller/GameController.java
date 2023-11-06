package com.example.ahorcado.controller;

import com.example.ahorcado.services.Game;
import com.example.ahorcado.services.GameStats;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador que maneja las solicitudes relacionadas con el juego.
 */
@Controller
@Slf4j
public class GameController {
    @Autowired
    private Game partida;

    @Autowired
    private GameStats estadisticas = new GameStats();

    private final int MAX_FALLOS = 6;

    /**
     * Maneja la solicitud GET en la ruta "/" para iniciar o continuar el juego.
     *
     * @param response Objeto de respuesta HTTP para gestionar las cookies.
     * @param model    El modelo que se utiliza para renderizar la vista.
     * @return La vista "index" para mostrar el juego.
     */
    @GetMapping("/ahorcado")
    public String inicio(HttpServletResponse response, Model model) {
        // Determina si el juego ha terminado.
        boolean juegoTerminado = false;

        // Obtiene la cantidad actual de fallos en el juego.
        int fallos = partida.getFallos();


        // Comprueba si el juego sigue en progreso o ha terminado por exceso de fallos.
        if (fallos < MAX_FALLOS) {
            generarCookieFallos(fallos, response);

        } else {
            // Establece el máximo de fallos permitidos y muestra un mensaje.
            partida.setFallos(MAX_FALLOS);
            generarCookieFallos(MAX_FALLOS, response);
            model.addAttribute("finalizar", "Vaya, has perdido. La palabra era:");
            estadisticas.fallarPalabra(partida.getPalabra());
            juegoTerminado = true;
        }

        // Comprueba si el jugador ha adivinado todas las letras de la palabra.
        if (!partida.obtenerPalabraOculta().contains("_")) {
            model.addAttribute("finalizar", "¡Enhorabuena! Palabra correcta");
            estadisticas.acertarPalabra(partida.getPalabra());
            juegoTerminado = true;
        }

        // Actualiza el modelo con la información relevante.
        model.addAttribute("palabraOculta", partida.obtenerPalabraOculta());
        model.addAttribute("abecedario", obtenerAbecedario());
        model.addAttribute("pista", partida.obtenerPista());
        model.addAttribute("letrasProbadas", partida.getLetrasProbadas());
        model.addAttribute("juegoTerminado", juegoTerminado);

        model.addAttribute("partida", partida);
        model.addAttribute("stats", estadisticas);

        // Si el juego ha terminado por exceso de fallos, muestra la palabra oculta.
        if (fallos >= MAX_FALLOS) {
            model.addAttribute("palabraOculta", partida.getPalabra());
        }

        if (juegoTerminado) {
            estadisticas.addAciertos(partida.getLetrasAcertadas());
            estadisticas.addFallos(partida.getLetrasFalladas());
        }

        return "ahorcado-admin";
    }



    /**
     * Maneja la solicitud GET para adivinar una palabra en el juego.
     *
     * @param intentoPalabra La palabra a adivinar.
     * @return La vista parcial "fragmentos/palabraOculta :: palabraOculta" para actualizar la palabra oculta en la página.
     */
    @PostMapping("/adivinar")
    public String adivinar(@RequestParam("intentoPalabra") String intentoPalabra) {
        partida.probarPalabra(intentoPalabra);
        return "redirect:/ahorcado";
    }

    /**
     * Maneja la solicitud para adivinar una letra en el juego.
     *
     * @param letra La letra a adivinar.
     * @param model El modelo que se utiliza para actualizar la vista.
     * @return La vista parcial "fragmentos/palabraOculta :: palabraOculta" para actualizar la palabra oculta en la página.
     */
    @GetMapping("/adivinar/{letra}")
    public String adivinarLetra(@PathVariable("letra") char letra, Model model) {
        partida.probarLetra(letra);
        model.addAttribute("palabraOculta", partida.obtenerPalabraOculta());
        return "fragmentos/palabraOculta :: palabraOculta";
    }

    /**
     * Maneja la solicitud para iniciar una nueva partida del juego.
     *
     * @return Una redirección a la página de inicio del juego.
     */
    @GetMapping("/nuevaPartida")
    public String nuevaPartida() {
        partida = new Game();
        estadisticas.nuevaPartida(partida.getPalabra());
        log.info(partida.toString());

        return "redirect:/ahorcado";
    }

//    @GetMapping("/ahorca2/inicio")

    /**
     * Genera una cookie llamada "fallos" con el número de fallos especificado y la configura para que expire en 2 horas.
     *
     * @param fallos   El número de fallos que se almacenará en la cookie.
     * @param response El objeto HttpServletResponse utilizado para agregar la cookie a la respuesta.
     */
    private void generarCookieFallos(int fallos, HttpServletResponse response) {
        Cookie cookie = new Cookie("fallos", String.valueOf(fallos));
        cookie.setMaxAge(120 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Genera y retorna una lista que contiene el abecedario en mayúsculas, desde la letra 'A' hasta 'Z', incluyendo la letra 'Ñ'.
     *
     * @return Una lista de caracteres que representa el abecedario completo en mayúsculas.
     */
    private List<Character> obtenerAbecedario() {
        List<Character> abecedario = new ArrayList<>();
        for (char letra = 'A'; letra <= 'Z'; letra++) {
            abecedario.add(letra);

            if (letra == 'N') abecedario.add('Ñ');
        }
        return abecedario;
    }
}
