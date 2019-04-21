# rtsqld
rtsqld (Road To SQL-D) es un programa para practicar todas las consultas sql de los ejercicios de las sesiones de practicas.

IMPORTANTE:
Para que pueda funcionar el programa debes descargarlo (el ejecutable) junto con el fichero config.json, 
en este fichero debes escribir tal y como indica, todos los datos necesarios para que se pueda conectar
a la db, el fichero debe encontrarse en la misma carpeta que el ejecutable .jar.

NOTA: Todos los datos que escribas en el fichero config.json solo los usa el programa en tiempo de ejecución, cuando cierras
 el programa los datos desaparecen del mismo por lo tanto no debes preocuparte porque te los roben o algo. En cualquier caso si 
 tienes alguna duda al respecto o no te fias, puedes comprobarlo tú mismo mirando el cofigo fuente del programa, si aun así sigue sin convencerte,
 puedes descargarte el codigo y compilarlo tu mismo con el IDE IntelliJ (es gratuito) y exportar tu propio ejecutable .jar.
 
El mecanismo del programa es el siguiente:
- Eliges una sesión, se abre la sesión, te conectas a la db (si no tienes el puerto abierto, no se conectará)
- Abres el puerto (si es necesario) y luego te conectas.
- Te mostrarán preguntas aleatorias, tienes 10 minutos para escribir la query que te piden en una pregunta.
- Si la query está bien hecha te indicará que la respuesta es correcta y si no pues que no lo es.
- A parte tamién te mostrará los resultados de tu query en formato tabla.
- Igualmente tienes disponible en la consola del programa todo el historial de lo que haces.
- Puedes saltar una pregunta en cualquier momento y se te mostrará otra aleatoria.
- Puedes practicar tantas veces que quieras.

El ejecutable .jar junto con el config.json se encuentran en la raíz del repositorio.

Codigo del programa en: src/ua/it/fbd/rtsqld

Por ultimo si detectas algún problema en el programa avisa y se arreglará.
