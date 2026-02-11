package fase1;

import java.io.FileWriter;
import java.io.PrintWriter;

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
            
            //Preparar el archivo
            PrintWriter escribir = new PrintWriter(new FileWriter(nombreArchivo));
            String url = gArchivo.obtenerLinea();


            while (url != null) {
                
                //Tiempo aleatorio para dormir el hilo
                int t = (int) (Math.random() * 2000 + 1000);

                Thread.sleep(t);

                //Escribir en el archivo
                escribir.println(url);
                escribir.flush();

                System.out.println(nombreArchivo + " : " + url);

                url = gArchivo.obtenerLinea();
            }
        
           //Cerrar el escritor cuando se acaben las lineas
           escribir.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
