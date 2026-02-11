# Java Multi-threaded Web Indexer (Crawler Simulation)

Este proyecto simula el comportamiento de un motor de búsqueda, procesando archivos HTML para extraer enlaces y clasificar palabras clave utilizando un sistema de indexación basado en **hashes MD5** y **programación multihilo**.



## Evolución del Proyecto (Fases 1-8)
El desarrollo se realizó de forma incremental, cubriendo los siguientes hitos técnicos:

1. **Lectura y Tokenización:** Limpieza de etiquetas HTML y signos de puntuación.
2. **Filtrado de Stop-words:** Eliminación de palabras irrelevantes (artículos, preposiciones) para optimizar el índice.
3. **Concurrencia (Threads):** Implementación de múltiples hilos trabajando simultáneamente sobre un `GestorArchivo` sincronizado.
4. **Indexación MD5:** Creación de un árbol de directorios dinámico (6 niveles) basado en el hash de las palabras para evitar colisiones y mejorar la búsqueda.
5. **Control de Tiempo (Backoff):** Lógica para evitar re-procesar la misma URL antes de un intervalo de 1 hora.
6. **Recursividad:** Extracción de nuevos enlaces de cada página para alimentar la cola de trabajo (Carpeta `IN`).

##  Conceptos Aplicados
* **Sincronización de Recursos:** Uso de bloques `synchronized` para evitar condiciones de carrera (*Race Conditions*) entre hilos.
* **Persistencia en Disco:** Manejo avanzado de `File`, `BufferedReader` y `PrintWriter`.
* **Criptografía:** Uso de `MessageDigest` para la generación de identificadores únicos.
