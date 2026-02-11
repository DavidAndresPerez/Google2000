
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GestorArchivo {
    
    //Variables
    private String url;
    private BufferedReader lectura;

    //Constructor
    public GestorArchivo (String ruta){
        this.url = ruta;
    }

    //Metodo que lee el contenido del archivo
    public void leerArchivo(){
        try {
            this.lectura = new BufferedReader(new FileReader(url));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Metodo que devuelve cada linea del archivo para 
    //pasarsela al thread
    public synchronized String obtenerLinea(){
        String linea = null;

            try {
                if(lectura != null){
                    linea = lectura.readLine();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return linea;
    }
}
