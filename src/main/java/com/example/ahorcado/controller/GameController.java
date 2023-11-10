package com.example.ahorcado.controller;

import com.example.ahorcado.services.Game;
import com.example.ahorcado.services.GameStats;
import com.example.ahorcado.utilidades.Utilidades;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador que maneja las solicitudes relacionadas con el juego.
 */
@Controller
@Slf4j
public class GameController {
    private Game partida;
    @Autowired
    private GameStats estadisticas;

    /**
     * Maneja los datos y la logica dependiendo si la partida es de admin, estandar o de 2 jugadores,
     * mostrando información relevante segun que tipo de partida sea.
     *
     * @param response Objeto de respuesta HTTP para gestionar las cookies.
     * @param model    El modelo que se utiliza para renderizar la vista.
     * @return La vista "ahorcado" para mostrar el juego.
     */
    @GetMapping({"/ahorcado", "/ahorca2"})
    public String inicio(HttpServletResponse response, Model model) {
        if (partida == null) partida = new Game();

        // Comprueba si el juego sigue en progreso o ha terminado por exceso de fallos.
        if (!partida.demasiadosFallos()) {
            //Si no hay demasiados fallos añade la palabra oculta
            model.addAttribute("palabraOculta", partida.obtenerPalabraOculta());

        } else {
            //Si los hay muestra un mensaje de que ha perdido y muestra la palabra completa
            model.addAttribute("finalizar", "Vaya, has perdido. La palabra era:");
            model.addAttribute("palabraOculta", partida.getPalabra());

            if (isAdmin()) estadisticas.fallarPalabra(partida.getPalabra()); //Añade la palabra fallada a las stats del admin
        }

        // Comprueba si la partida ha finalizado al acertar la palabra y muestra un mensaje de felicitación.
        if (partida.palabraDescubierta()) {
            model.addAttribute("finalizar", "¡Enhorabuena! Palabra correcta");

            if (isAdmin()) estadisticas.acertarPalabra(partida.getPalabra()); // Añade la palabra acertada a las stats del admin
        }

        //Si la partida es de 2 jugadores y está terminada se establece la puntuación
        if (partida.isPartidaTerminada() && partida.isAhorca2()) partida.establecerPuntuacion();

        //Si la partida es de 2 jugadores muestra la puntuacion de ambos
        if (partida.isAhorca2()) {
            model.addAttribute("jugador1", Game.puntosJugador1);
            model.addAttribute("jugador2", Game.puntosJugador2);
        }

        //Si la partida es de admin añade las letras acertadas y falladas a sus stats
        if (partida.isPartidaTerminada() && isAdmin()) {
            estadisticas.addAciertos(partida.getLetrasAcertadas());
            estadisticas.addFallos(partida.getLetrasFalladas());
        }


        // Actualiza el modelo con la información relevante.
        model.addAttribute("abecedario", obtenerAbecedario());
        model.addAttribute("pista", partida.getPista());
        model.addAttribute("letrasProbadas", partida.getLetrasProbadas());//Lista que indica cuáles letras no se mostrarán en el teclado

        if (isAdmin()) {
            model.addAttribute("partida", partida);
            model.addAttribute("stats", estadisticas);
        }

        //Generamos la cookie de fallos (la imagen dependera de esta cookie)
        Utilidades.generarCookieFallos(partida.getFallos(), response);

        //Añadimos el chivato de si la partida termino para mostrar el boton de nueva partida
        model.addAttribute("juegoTerminado", partida.isPartidaTerminada());

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
     * Si el usuario es Admin se guarda la palabra en sus Stats.
     *
     * @return Si ahorca2 está activo redirige al formulario de palabra si no redirige al juego estandar.
     */
    @GetMapping("/nuevaPartida")
    public String nuevaPartida() {
        if (partida.isAhorca2()){
            return "redirect:/ahorca2/nuevaPartida";
        }

        partida = new Game();
        if (isAdmin()) estadisticas.nuevaPartida(partida.getPalabra());

        return "redirect:/ahorcado";
    }

    /**
     * Desactiva el chivato de 2 jugadores, borra los datos de la anterior partida
     * @return Página de Eleccion de partidas
     */
    @GetMapping("/salir")
    public String salir(HttpServletResponse response) {
        partida = null;
        Game.puntosJugador1 = 0;
        Game.puntosJugador2 = 0;
        Game.turno = 1;
        Utilidades.generarCookieFallos(0, response);
        return "redirect:/home";
    }

    /**
     * Muestra el formulario para introducir palabra y pista
     * @return Pagina con formulario
     */
    @GetMapping("/ahorca2/nuevaPartida")
    public String form2jugadores(Model model) {
        if (Game.turno % 2 != 0) {
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

    /**
     * Verifica si el usuario tiene el rol de admin
     * @return True si el usuario es admin o False
     */
    private boolean isAdmin() {
        return Objects.equals(obtenerRol(), "ROLE_ADMIN");
    }

    /**
     * Obtiene el rol del usuario autenticado
     * @return Un String del rol.
     */
    private String obtenerRol() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica si el usuario está autenticado
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Obtiene los roles del usuario
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                String userRole = authority.getAuthority();

                return userRole;
            }
        }
        return null;
    }
}
