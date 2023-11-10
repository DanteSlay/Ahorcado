$(document).ready(() => {
    // Este bloque se ejecuta cuando el documento HTML ha sido completamente cargado.

    // Cuando se hace clic en un botón con la clase "tecla":
    $(".tecla").click(function() {
        const boton = $(this);
        const letra = boton.text().charAt(0);

        // Realiza una solicitud AJAX al servidor para adivinar la letra.
        $.ajax({
            type: "get",
            url: "/adivinar/" + letra,
            success: function(data) {
                // Cuando se recibe una respuesta exitosa desde el servidor:

                // Actualiza el contenido del elemento con id "palabra" con la respuesta del servidor.
                $("#palabra").html(data);

                // Remueve la clase "tecla" del botón y agrega la clase "letra-oculta".
                boton.removeClass("tecla");
                boton.addClass("letra-oculta");

                // Recarga la página para actualizar la imagen del ahorcado.
                location.reload();
            }
        });
    });

    // Obtiene el valor de la cookie llamada "fallos" y verifica si existe.
    const fallosCookie = document.cookie.replace(/(?:^|.*;\s*)fallos\s*=\s*([^;]*).*$|^.*$/, "$1");

    if (fallosCookie) {
        // Si la cookie "fallos" existe, actualiza la fuente de la imagen con el número de fallos.
        document.getElementById("imagen").src = "/img/" + fallosCookie + ".jpg";
    }
    let tiempoRestante = 10;

    // Actualiza el contenido del elemento con id "tiempo-restante" con el tiempo restante actualizado
    function actualizarTemporizador() {
        $("#tiempo-restante").text(tiempoRestante);
    }

    // Configura un temporizador que disminuirá el tiempo restante cada segundo
    const temporizadorInterval = setInterval(() => {
        tiempoRestante--;

        // Verifica si el tiempo ha llegado a cero
        if (tiempoRestante <= 0) {
            clearInterval(temporizadorInterval); // Detiene el temporizador
            // Agrega aquí la lógica que desees cuando el tiempo llega a cero
            location.reload()
        }

        // Actualiza el temporizador en la vista
        actualizarTemporizador();
    }, 1000); // 1000 milisegundos = 1 segundo
});
