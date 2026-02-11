package fase8;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lector extends Thread{

    //Variables
    private GestorArchivo gArchivo;
    private PrintWriter out; //Para hablar con el servidor
    
    // Lista con los elementos a quitar (Fase 3)
    private final List<String> elementosAQuitar = Arrays.asList(
        "el", "la", "los", "las", "un", "una", "unos", "unas",
        "a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", 
        "durante", "en", "entre", "hacia", "hasta", "mediante", "para", 
        "por", "segun", "sin", "so", "sobre", "tras", "versus", "via",
        "y", "e", "ni", "que", "o", "u", "pero", "aunque", "sino", "porque",
        "este", "esta", "estos", "estas", "ese", "esa", "esos", "esas",
        "muy", "mas", "ya", "siempre", "tambien", "como"
    );

    // Lista de palabras objetivo para el índice MD5 (Fase 5)
    private final List<String> palabrasObjetivo = Arrays.asList(
        "libro", "casa", "árbol", "perro", "gato", "sol", "luna", "nube", 
        "estrella", "montaña", "río", "mar", "flor", "pájaro", "insecto", 
        "animal", "persona", "mundo"
    );

    //Lista de URLS objetivo dada en el enunciado (Fase 6)
    private final List<String> urlsObjetivo = Arrays.asList(
        "www.elpais.es", "www.abc.es", "www.elmundo.es", "www.20minutos.es",
        "www.ivoox.com", "www.google.es", "www.meneame.net", "www.as.es",
        "www.marca.es", "www.elcorteingles.es", "www.ibm.es", "www.oracle.es",
        "www.microsoft.es", "www.valladolid.es", "www.fmdva.org"
    );

    //Constructor
    public Lector(GestorArchivo gestor, PrintWriter pwSocket) {
        this.gArchivo = gestor;
        this.out = pwSocket;
    }

     @Override
    public void run() {
        try {
            String url = gArchivo.obtenerLinea();
            System.out.println("DEBUG: Leída URL del archivo: " + url);

            while(url != null){

                //Quitar el https:// de la url
                String urlFiltro = url.replace("https://", "");
                //Buscar el archivo html en descargas
                File archivoHTML = new File("descargas/" +urlFiltro +".html");

                //Si existe ese html leer lo que hay dentro
                if(archivoHTML.exists()){
                    System.out.println("DEBUG: Archivo encontrado, procesando " + url);
                    leerXML(archivoHTML, url);
                    extraerLinks(archivoHTML, url);
                }  else {
                     System.out.println("ERROR: No se encuentra el archivo en: " + archivoHTML.getAbsolutePath());
                }

                url = gArchivo.obtenerLinea();
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
     //Metodo que saca el contenido del xml
    public void leerXML(File archivo, String urlOrigen){

        try{
            //Leer todo el contenido
            BufferedReader bf = new BufferedReader(new FileReader(archivo));
            ArrayList<String> contenido = new ArrayList<>();
            
            String linea = bf.readLine();

            while(linea != null){
                
                //Quitar primero todo lo que no nos interesa antes de añadirla
                String linea2 = linea.replaceAll("<[^>]*>", " ") // Quita las etiquetas HTML
                                     .replaceAll("[^a-záéíóúñA-ZÁÉÍÓÚÑ]", " ") // Quita signos de puntuación
                                     .toLowerCase();

                
                //Dividir la frase en palabras
                String palabras [] = linea2.split("\\s+"); //El \\s+ quita cualquier espacio

                //Ir añiadiendo las palabras si no estan vacias
                for(String p: palabras){
                    if(!p.isBlank()){
                        contenido.add(p);
                    }

                    //Fase 8: en vez de meterlas a la carpeta MD5 lo pasamos al servidor
                    if(palabrasObjetivo.contains(p)){
                        out.println("Palabra;" + p+ ";" +urlOrigen);
                    }
                }

                linea = bf.readLine();
            }

            bf.close();

            //Filtrar las palabras unicas y cuantas veces aparecen
            ArrayList<String> palabrasUnicas = new ArrayList<>();
            ArrayList<Integer> contadores = new ArrayList<>();

            for(String palabra: contenido){
                if(palabra.length() > 2 && !elementosAQuitar.contains(palabra)){

                    //Si ya esta la palabra aumentar su contador
                    if (palabrasUnicas.contains(palabra)) {
                    
                        int posicion = palabrasUnicas.indexOf(palabra);
                        int valorActual = contadores.get(posicion);
                        contadores.set(posicion, valorActual + 1);
                    
                    ///Si no añadirla y poner a uno el contador
                    } else {

                        palabrasUnicas.add(palabra);
                        contadores.add(1);
                    }
                }
            }

            //Mostrar los resultados
            System.out.println("Pagina web " +archivo.getName());
            for (int i = 0; i < palabrasUnicas.size(); i++) {
                System.out.println(palabrasUnicas.get(i) + ": " + contadores.get(i));
            }

        } catch(IOException e){
            System.out.println("Error al coger el contenido del archivo " +archivo);
        }
    }


 public void extraerLinks(File archivo, String urlOrigen) {

    try {
        BufferedReader bf = new BufferedReader(new FileReader(archivo));
        ArrayList<String> linksUnicos = new ArrayList<>();
        String linea = bf.readLine();

        while (linea != null) {
            String contenido = linea.toLowerCase();
            int posActual = 0;

            // Buscamos todas las ocurrencias de "href=\"http" en la línea
            while ((posActual = contenido.indexOf("href=\"http", posActual)) != -1) {
                
                // El link empieza después de href="
                int inicioLink = posActual + 6; 
                // Buscamos la siguiente comilla que cierra el link
                int finLink = contenido.indexOf("\"", inicioLink);

                if (finLink != -1) {
                    String link = linea.substring(inicioLink, finLink); // Usamos linea (original) para no perder mayúsculas

                    if (!linksUnicos.contains(link)) {
                        linksUnicos.add(link);

                        //Si el link apunta a una de las paginas objetivo mandarlo
                        for(String ob: urlsObjetivo){
                            if(link.contains(ob)){
                                out.println("Link;" +link+ ";"+ urlOrigen);
                            }
                        }
                    }
                    posActual = finLink; // Movemos el puntero para seguir buscando en la misma línea
                } else {
                    break; 
                }
            }
            linea = bf.readLine();
        }
        bf.close();

        System.out.println("==== LINKS UNICOS DE LA PAGINA " + archivo.getName() + " ====");
        for (String li : linksUnicos) {
            System.out.println(li);
        }

    } catch (Exception e) {
        System.out.println("Error al extraer los links de la pagina: " + e.getMessage());
    }
}

    //Metodo que genera el MD5 de la palabra
    private String obtenerMD5(String palabra) {
        String md5 = "";

        try{

            //Import necesario en Java para usar MD5
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");

            //Sacar los bytes de la palabra
            byte[] bytes = md.digest(palabra.getBytes());

            //Aplicar el MD5
            for(byte b : bytes){
                //Convertir cada byte a hex y concatenarlo a la variable md5
                md5 += String.format("%02x", b);
            }

        }  catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }

        return md5;
    
    }
}

