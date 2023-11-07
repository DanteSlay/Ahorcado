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

    private boolean ahorca2 = false;
    private int jugador1 = 0;
    private int jugador2 = 0;
    private int turno = 1;

    /**
     * Maneja la solicitud GET en la ruta "/" para iniciar o continuar el juego.
     *
     * @param response Objeto de respuesta HTTP para gestionar las cookies.
     * @param model    El modelo que se utiliza para renderizar la vista.
     * @return La vista "index" para mostrar el juego.
     */
    @GetMapping({"/ahorcado", "/ahorca2"})
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
            establecerPuntuacion();
            juegoTerminado = true;
        }

        // Comprueba si el jugador ha adivinado todas las letras de la palabra.
        if (!partida.obtenerPalabraOculta().contains("_")) {
            model.addAttribute("finalizar", "¡Enhorabuena! Palabra correcta");
            estadisticas.acertarPalabra(partida.getPalabra());
            establecerPuntuacion();
            juegoTerminado = true;
        }

        // Actualiza el modelo con la información relevante.
        model.addAttribute("palabraOculta", partida.obtenerPalabraOculta());
        model.addAttribute("abecedario", obtenerAbecedario());
        model.addAttribute("pista", partida.getPista());
        model.addAttribute("letrasProbadas", partida.getLetrasProbadas());
        model.addAttribute("juegoTerminado", juegoTerminado);

        model.addAttribute("partida", partida);
        model.addAttribute("stats", estadisticas);

        // Si el juego ha terminado por exceso de fallos, muestra la palabra oculta.
        if (fallos >= MAX_FALLOS) {
            model.addAttribute("palabraOculta", partida.getPalabra());
        }

        if (ahorca2) {
            model.addAttribute("jugador1", jugador1);
            model.addAttribute("jugador2", jugador2);
        }

        if (juegoTerminado) {
            estadisticas.addAciertos(partida.getLetrasAcertadas());
            estadisticas.addFallos(partida.getLetrasFalladas());
        }

        return "ahorcado";
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
     * @return Si ahorca2 está activo redirige al formulario de palabra si el valor es false redirige al juego individual.
     */
    @GetMapping("/nuevaPartida")
    public String nuevaPartida() {
        if (ahorca2){
            return "redirect:/ahorca2/nuevaPartida";
        }

        partida = new Game();
        estadisticas.nuevaPartida(partida.getPalabra());

        return "redirect:/ahorcado";
    }

    /**
     * Desactiva el chivato de 2 jugadores, borra los datos de la anterior partida
     * @return Página de Eleccion de partidas
     */
    @GetMapping("/salir")
    public String salir() {
        ahorca2 = false;
        partida = new Game();
        jugador1 = 0;
        jugador2 = 0;
        turno = 1;
        return "redirect:/home";
    }

    /**
     * Muestra el formulario para introducir palabra y pista
     * @return Pagina con formulario
     */
    @GetMapping("/ahorca2/nuevaPartida")
    public String form2jugadores(Model model) {
        ahorca2 = true;
        if (turno % 2 != 0) {
            model.addAttribute("jugador2", "Jugador 2");
        }

        return "ahorca2";
    }

    /**
     * Inicia una nueva partida con la palabra y pista del usuario
     * @param nuevaPalabra Palabra del usuario
     * @param nuevaPista Pista del usuario
     * @return Pagina del juego
     */
    @PostMapping("/ahorca2/submit")
    public String ahorca2submit(@RequestParam("nuevaPalabra") String nuevaPalabra,
                                @RequestParam("nuevaPista") String nuevaPista) {
        partida = new Game(nuevaPalabra.toUpperCase(), nuevaPista);

        return "redirect:/ahorca2";
    }

    public void establecerPuntuacion() {
        if (!partida.obtenerPalabraOculta().contains("_")) {
            if (turno % 2 == 0) {
                jugador2++;
            } else {
                jugador1++;
            }
        }
        turno++;
    }

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
