import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lector extends Thread{

     //Variables
    private GestorArchivo gArchivo;

    //Lista con los elementos a quitar
    private final List<String> elementosAQuitar = Arrays.asList(
        "el", "la", "los", "las", "un", "una", "unos", "unas",
        "a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", 
        "durante", "en", "entre", "hacia", "hasta", "mediante", "para", 
        "por", "segun", "sin", "so", "sobre", "tras", "versus", "via",
        "y", "e", "ni", "que", "o", "u", "pero", "aunque", "sino", "porque",
        "este", "esta", "estos", "estas", "ese", "esa", "esos", "esas",
        "muy", "mas", "ya", "siempre", "tambien", "como"
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
}
