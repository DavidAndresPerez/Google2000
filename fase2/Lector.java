package fase2;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Lector extends Thread{

    //Variables
    private GestorArchivo gArchivo;
    private String nombreArchivo;

    //Constructor
    public Lector(GestorArchivo gestor, String nombreFichero) {
        this.gArchivo = gestor;
        this.nombreArchivo = nombreFichero;
    }

    //Metodo run
    @Override
    public void run() {
        try {
           
            //Crear el cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            String url = gArchivo.obtenerLinea();

            PrintWriter escribir = new PrintWriter(new FileWriter("descargas/" + url + ".html"));

            while (url != null) {
                try {

                    //Peticion
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://" + url))
                            .GET()
                            .build();
                    
                    //Enviar la peticion y recibir la respuesta
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String html = response.body();
        
                    //Guardar el contenido en un archivo con el nombre de esa web
                    PrintWriter escritorWeb = new PrintWriter(new FileWriter("descargas/" +url + ".html"));
                    escritorWeb.print(html);
                    escritorWeb.close();

                    //Escribir el proceso
                    escribir.println("Descargado: " + url);
                    escribir.flush();
                    System.out.println(nombreArchivo + " descargó: " + url);
                    
             } catch (Exception e) {
                    System.err.println("Error descargando " + url + ": " + e.getMessage());
             }
        
             //Pausar el hilo
             int t = (int) (Math.random() * 2000 + 1000);

             Thread.sleep(t);
          
             url = gArchivo.obtenerLinea();
          
          }

          escribir.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}