package fase8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ControlCliente extends Thread {

    //Socket
    private Socket socket;

    //Contructor
    public ControlCliente(Socket oSocket){
        this.socket = oSocket;
    }

    @Override
    public void run(){
        BufferedReader bf = null;

        try {
            bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String linea = bf.readLine();

            while (linea != null) {
                System.out.println("SERVIDOR RECIBIÓ: " + linea);
                if (linea.equals("=== ARCHIVOS LEIDOS ===")) {
                    System.out.println("Cliente notificó fin de envío. Cerrando hilo.");
                    break; 
                }


                //Metodo del servidor que analiza los mensajes
                Servidor.analizarMensaje(linea);
                linea = bf.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        } finally{

            //Cerrar los recursos
            
                try {

                    if(bf != null){
                        bf.close();
                    }

                    if (socket != null && !socket.isClosed()) {
                         socket.close();
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
}
