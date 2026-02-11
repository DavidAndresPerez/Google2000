package fase6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fase7.EscritorEnlaces;
import fase7.GestorTiempo;

public class Lector extends Thread{

    //Variables
    private GestorArchivo gArchivo;

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
    public Lector(GestorArchivo gestor, String nombreFichero) {
        this.gArchivo = gestor;
    }

     @Override
    public void run() {
        try {
            String url = gArchivo.obtenerLinea();
    
            while(url != null){
                
              //Coger el HTML de la fase 2
              File archivoHtml = new File("descargas/" + url + ".html");

              //Comprobar que exista
              if(archivoHtml.exists()){
                leerXML(archivoHtml);
                extraerLinks(archivoHtml);
              } else {
                System.out.println("El archivo " +archivoHtml+ " no se ha encontrado");
              }
              
              url = gArchivo.obtenerLinea();
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
     //Metodo que saca el contenido del xml
    public void leerXML(File archivo){

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


                        //Si la palabra esta en las palabras que buscamos la ponemso en el arbol de directorios
                        if(palabrasObjetivo.contains(p)){
                            clasificarMD5(p,archivo.getName());
                        }
                    
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


    //Metoodo que extrae los links del html
    public void extraerLinks(File archivo) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(archivo));
            ArrayList<String> linksUnicos = new ArrayList<>();

            String linea = bf.readLine();

            while (linea != null) {
                
                //Sacar los caracteres de la linea y comprobar si contienen un link
                char[] letras = linea.toCharArray();

                for (int i = 0; i < letras.length - 10; i++) {
                
                // Buscar el inicio de un link
                if (letras[i] == 'h' && letras[i+1] == 'r' && letras[i+2] == 'e' && 
                    letras[i+3] == 'f' && letras[i+4] == '=' && letras[i+5] == '\"' &&
                    letras[i+6] == 'h' && letras[i+7] == 't' && letras[i+8] == 't' && letras[i+9] == 'p') {
                    
                    String link = "";

                    //Copiar el link desde la posicion de la h de http
                    int indi = i+6;

                    //Copiar hasta que encuente el cierre el link
                    while (indi < letras.length && letras[indi] != '\"') {
                        link = link + letras[indi];
                        indi++;
                    }
                    
                    //Si ha encontrado el link guardarlo
                    if(!link.equals("") && !linksUnicos.contains(link)){
                        linksUnicos.add(link);


                        //Clasificar el link en MD5 (Fase 6)
                        for(String web: urlsObjetivo){
                            if(link.contains(web)){
                                clasificarLinkMD5(link, archivo.getName());
                            }
                        }
                    }
                }

            }

            linea = bf.readLine();
        }

        bf.close();
        
        //Imprimir los links
        System.out.println("==== LINKS UNICOS DE LA PAGINA " + archivo.getName() + "====");
        for(String li : linksUnicos){
            System.out.println(li);
        }
    
        } catch (Exception e) {
           System.out.println("Error al extraer los links de la pagina");
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


    //Metodo que crea un arbol un arbol de directorios y guarda la referencia
    private synchronized void clasificarMD5(String palabra, String nombreWeb) {
    
        //Obtener el hash
        String hash = obtenerMD5(palabra);
    
        if(hash != null){

            //Ruta base
            String ruta = "fase5_md5/";

            //Bucle para generar las divisiones del arbol de directorios 
            for (int i = 0; i < 6; i++) {
                ruta = ruta + hash.charAt(i) + "/";
            }
        
            //Crear objeto File con la ruta completa
            File directorio = new File(ruta);
        
            //Crear las carpetas si no existen
            if(directorio.exists() || directorio.mkdirs()){

                //Archivo text de destino
                File archivo = new File(directorio,palabra + ".txt");

                //Abrir el archivo para escribir la url de la pagina en el
                try{
                    FileWriter fw = new FileWriter(archivo,true);
                    PrintWriter pw = new PrintWriter(fw);

                    pw.println("Encontrada en:  " +nombreWeb);

                    //Cerrar los flujos de escrituras
                    pw.close();
                    fw.close();
                } catch (IOException e) {
                    System.out.println("Error al escribir el archivo de indice MD5");
                }
            }
        } 
    }

    //Metodo de la fase 6 para clasificar un link en MD5
    private synchronized void clasificarLinkMD5(String urlEncontrada, String nombreWebOrigen) {
    
        String hash = obtenerMD5(urlEncontrada);

        if(hash != null){
            String ruta = "fase6_links/";

            for (int i = 0; i < 6; i++) {
                ruta = ruta + hash.charAt(i) + "/";
            }

            File directorio = new File(ruta);

            if(directorio.exists() || directorio.mkdirs()){
            
                //Lamamos al archivo enlaces.txt para no usar la ruta completa del archivo
                File archivo = new File(directorio, "enlaces.txt");
            

                try{
                    FileWriter fw = new FileWriter(archivo,true);
                    PrintWriter pw = new PrintWriter(fw);

                    pw.println("URL encontrada: " + urlEncontrada);
                    pw.println("Origen: " + nombreWebOrigen);
            
                    //Cerrar los flujos de escrituras
                    pw.close();
                    fw.close();

                } catch (IOException e) {
                    System.out.println("Error al escribir el archivo");
                }
            }
        }  
    }
}
