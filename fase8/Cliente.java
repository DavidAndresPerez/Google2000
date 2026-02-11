package fase8;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Cliente {

    public static void main(String[] args) {
        
        //Variables para conectarse al servidor
        String host = "localhost";
        int puerto = 5000;

         //Configurar SSL
        System.setProperty("javax.net.ssl.keyStore", "cifrados/miAlmacen.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        System.setProperty("javax.net.ssl.trustStore", "cifrados/miAlmacen.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        try {
            
            //Sockets para la comunicacion cifrada
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) ssf.createSocket(host, puerto);


            //Intanciar el gestor de archivos
            GestorArchivo gestor = new GestorArchivo("fase8/seeds2.txt");
            gestor.leerArchivo();

            //Variable para la salida del socket
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            //Lista para controlar los hilos
            ArrayList<Lector> hilosLanzados = new ArrayList<>();

            //Lanzar los hilos lectores
            for(int i = 0; i < 3; i++){
                Lector lector = new Lector(gestor, out);
                lector.start();
                hilosLanzados.add(lector);
            }

            //Esperar a que los hilos hayan leido el contenido
            for(Lector l: hilosLanzados){
                try {
                    l.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //Mensaje para decirle al servidor que ya ha terminado el proceso
            out.println("=== ARCHIVOS LEIDOS ===");
    
        } catch (IOException e) {
            System.err.println("Error al establecer la conexion");
        }
    }
}
