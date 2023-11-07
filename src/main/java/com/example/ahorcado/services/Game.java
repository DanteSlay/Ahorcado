package com.example.ahorcado.services;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * La clase `Game` representa un juego de ahorcado. Contiene la lógica y el estado del juego.
 */
@Data
@Service
@Slf4j
public class Game {
    private String palabra; // La palabra a adivinar.
    private String pista; // La pista de la palabra
    private Set<Character> letrasAcertadas; // Lista de letras adivinadas.
    private Set<Character> letrasFalladas; // Lista de letras incorrectas.
    private ArrayList<Character> letrasProbadas; // Lista de letras probadas.
    private static ArrayList<String> palabrasJugadas = new ArrayList<>(); // Lista de palabras jugadas previamente.
    private int fallos; // Contador de fallos.

    /**
     * Constructor de la clase `Game`. Inicializa una nueva partida.
     */
    public Game() {
        this.palabra = palabraRandom(); // Obtiene una palabra aleatoria no jugada previamente.
        this.pista = obtenerPista();
        this.letrasAcertadas = new HashSet<>(); // Inicializa la lista de letras adivinadas.
        this.letrasFalladas = new HashSet<>(); // Inicializa la lista de letras incorrectas.
        this.letrasProbadas = new ArrayList<>(); // Inicializa la lista de letras probadas.
        this.fallos = 0; // Inicializa el contador de fallos.
    }

    /**
     * Controlador para 2 jugadores inicializa la palabra y la pista con datos del usuario
     * @param palabra Palabra elegida por el usuario
     * @param pista Pista introducida por el usuario
     */
    public Game(String palabra, String pista) {
        this.palabra = palabra;
        this.pista = pista;
        this.letrasAcertadas = new HashSet<>();
        this.letrasFalladas = new HashSet<>();
        this.letrasProbadas = new ArrayList<>();
        this.fallos = 0;

    }

    /**
     * Comprueba si la letra proporcionada por el jugador está presente en la palabra actual y actualiza el estado del juego en consecuencia.
     *
     * @param letra La letra a probar.
     * @return true si la letra es correcta y está presente en la palabra, false si es incorrecta.
     */
    public boolean probarLetra(char letra) {
        String posibleLetra = Character.toString(letra).toUpperCase();
        letrasProbadas.add(letra);
        if (palabra.contains(posibleLetra)) {
            letrasAcertadas.add(posibleLetra.charAt(0));

            return true;
        } else {
            letrasFalladas.add(posibleLetra.charAt(0));
            fallos++;

            return false;
        }
    }

    /**
     * Comprueba si el intento del jugador coincide con la palabra actual y actualiza el estado del juego en consecuencia.
     *
     * @param intentoPalabra El intento de adivinar la palabra.
     * @return true si el intento es correcto, false si es incorrecto.
     */
    public boolean probarPalabra(String intentoPalabra) {
        if (palabra.equalsIgnoreCase(intentoPalabra)) {
            for (char letra : palabra.toCharArray()) {
                letrasAcertadas.add(letra);
            }
            return true;
        } else {
            fallos++;

            return false;
        }
    }


    /**
     * Obtiene la representación de la palabra oculta, donde las letras adivinadas se muestran y las letras sin adivinar se ocultan con guiones bajos.
     *
     * @return Una cadena DE letras o guiones bajos.
     */
    public String obtenerPalabraOculta() {
        StringBuilder palabraOculta = new StringBuilder();
        for (char letra : palabra.toCharArray()) {
            if (letrasAcertadas.contains(letra)) {
                palabraOculta.append(letra);
                palabraOculta.append(" ");
            } else {
                palabraOculta.append("_");
                palabraOculta.append(" ");
            }
        }
        return palabraOculta.toString();
    }

    /**
     * Obtiene la pista asociada a la palabra actual del juego.
     *
     * @return La pista correspondiente a la palabra actual.
     */
    public String obtenerPista() {
        return mapaPalabraPista().get(palabra);
    }

    /**
     * Obtiene una palabra aleatoria que no ha sido jugada previamente.
     *
     * @return Una palabra aleatoria del arreglo de palabras.
     */
    private String palabraRandom() {
        String palabraRandom;
        Random random = new Random();

        do {
            int iRandom = random.nextInt(palabras.size());
            palabraRandom = palabras.get(iRandom);
        } while (palabrasJugadas.contains(palabraRandom));

        palabrasJugadas.add(palabraRandom);
        return palabraRandom;
    }

    /**
     * Crea y retorna un mapa que asocia cada palabra con su pista correspondiente.
     *
     * @return Un HashMap que mapea palabras a sus pistas.
     */
    private HashMap<String, String> mapaPalabraPista() {
        HashMap<String, String> mapa = new HashMap<>();
        for (int i = 0; i < palabras.size(); i++) {
            mapa.put(palabras.get(i), pistas.get(i));
        }
        return mapa;
    }


    private final ArrayList<String> palabras = new ArrayList<>(Arrays.asList(
            "CASA", "PERRO", "GATO", "FLOR", "COCHE", "SOL", "LUNA", "MAR", "MONTAÑA", "RIO",
            "MESA", "SILLA", "VENTANA", "PUERTA", "JARDIN", "CIELO", "TIERRA", "AIRE", "FUEGO", "AGUA",
            "MANZANA", "PLATANO", "UVA", "NARANJA", "LIMON", "KIWI", "SANDIA", "FRESA", "PIÑA", "MELOCOTON",
            "LIBRO", "PAPEL", "LAPICERO", "TIJERAS", "PEGAMENTO", "GOMA", "PIZARRA", "MAESTRO", "ESCUELA", "ALUMNO",
            "AMIGO", "FAMILIA", "PADRE", "MADRE", "HERMANO", "HERMANA", "ABUELO", "ABUELA", "TIO", "TIA",
            "COMIDA", "CENA", "DESAYUNO", "TELEVISOR", "PANTALLA", "SOMBRILLA", "VENTILADOR", "CUBIERTOS", "RELOJ",
            "CALENDARIO", "MONITOR", "TELEFONO", "COMPUTADORA", "ESPEJO", "CUCHARA", "TENEDOR", "CUCHILLO", "ESCALERA"
    ));


    private final ArrayList<String> pistas = new ArrayList<>(Arrays.asList(
            "Lugar donde vives.", // CASA
            "Un amigo de cuatro patas.", // PERRO
            "Un felino doméstico.", // GATO
            "Una planta que suele ser colorida.", // FLOR
            "Medio de transporte con cuatro ruedas.", // COCHE
            "La estrella que brilla en el cielo durante el día.", // SOL
            "El satélite natural de la Tierra.", // LUNA
            "Gran extensión de agua salada.", // MAR
            "Elevación natural del terreno.", // MONTAÑA
            "Agua que fluye continuamente.", // RIO
            "Mueble utilizado para poner objetos.", // MESA
            "Asiento con respaldo para sentarse.", // SILLA
            "Abertura en la pared para ver afuera.", // VENTANA
            "Permite entrar o salir de un espacio.", // PUERTA
            "Área con plantas y flores.", // JARDIN
            "La atmósfera sobre la Tierra durante el día.", // CIELO
            "Nuestro planeta.", // TIERRA
            "Lo que respiramos.", // AIRE
            "Elemento que puede arder.", // FUEGO
            "Esencial para la vida.", // AGUA
            "Una fruta roja y jugosa.", // MANZANA
            "Una fruta amarilla y alargada.", // PLATANO
            "Pequeñas frutas verdes o moradas.", // UVA
            "Una fruta cítrica de color naranja.", // NARANJA
            "Una fruta cítrica amarilla.", // LIMON
            "Una fruta pequeña y verde con semillas negras.", // KIWI
            "Una fruta grande, verde por fuera y roja por dentro.", // SANDIA
            "Pequeña fruta roja y dulce.", // FRESA
            "Una fruta tropical con una cáscara dura y escamosa.", // PIÑA
            "Una fruta jugosa con una piel aterciopelada.", // MELOCOTON
            "Un objeto con páginas que se lee para aprender o entretenerse.", // LIBRO
            "Hoja fina utilizada para escribir o imprimir.", // PAPEL
            "Un instrumento para escribir o dibujar.", // LAPICERO
            "Se utilizan para cortar papel o tela.", // TIJERAS
            "Se utiliza para unir objetos.", // PEGAMENTO
            "Un objeto elástico para borrar lápiz.", // GOMA
            "Superficie en la que se puede escribir o dibujar con tiza.", // PIZARRA
            "Persona que enseña a otros.", // MAESTRO
            "Lugar donde se aprende y se educa.", // ESCUELA
            "Persona que estudia en la escuela.", // ALUMNO
            "Una persona con la que tienes una relación cercana.", // AMIGO
            "El grupo de personas con la que tienes lazos familiares.", // FAMILIA
            "Un progenitor masculino.", // PADRE
            "Una progenitora femenina.", // MADRE
            "Hijo de tus padres.", // HERMANO
            "Hija de tus padres.", // HERMANA
            "El padre de uno de tus padres.", // ABUELO
            "La madre de uno de tus padres.", // ABUELA
            "El hermano de uno de tus padres.", // TIO
            "La hermana de uno de tus padres.", // TIA
            "Alimento que se consume durante el día.", // COMIDA
            "La última comida del día.", // CENA
            "La primera comida del día.", // DESAYUNO
            "Este dispositivo muestra programas y películas.", // TELEVISOR
            "Se utiliza para ver imágenes en dispositivos electrónicos.", // PANTALLA
            "Te protege del sol en la playa o en un día lluvioso.", // SOMBRILLA
            "Proporciona aire fresco en días calurosos.", // VENTILADOR
            "Utensilios de mesa que incluyen cuchillo, tenedor y cuchara.", // CUBIERTOS
            "Te dice la hora en cualquier momento del día.", // RELOJ
            "Te ayuda a realizar un seguimiento de fechas importantes y eventos.", // CALENDARIO
            "La pantalla de una computadora o dispositivo similar.", // MONITOR
            "Puedes hacer llamadas y enviar mensajes con este dispositivo.", // TELEFONO
            "Una máquina que te permite realizar tareas digitales.", // COMPUTADORA
            "Te muestra tu propio reflejo.", // ESPEJO
            "Se usa para servir alimentos líquidos o para comer.", // CUCHARA
            "Ayuda a comer alimentos sólidos y a pincharlos.", // TENEDOR
            "Un utensilio afilado para cortar alimentos.", // CUCHILLO
            "La usas para subir o bajar de un lugar elevado.")); // ESCALERA


}
