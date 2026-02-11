package fase7;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class GestorArchivo {
    
    //Variables
    private String url;
    private BufferedReader lectura;
    private File directorioIN;

    //Constructor
    public GestorArchivo (String ruta, String rutaIN){
        this.url = ruta;
        this.directorioIN = new File(rutaIN);

        //Si no existe la carpeta Crearla
        if(!directorioIN.exists()){
            directorioIN.mkdir();
        }

        
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

                //Cuando se acabe ir a la carpeta IN
                if(linea == null){
                    linea = obtenerURLCarpetaIN();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return linea;
    }

    //Metodo que obtinene las urls de la carpeta IN
    private String obtenerURLCarpetaIN(){
        String url = null;

        //Obtener los ficheros que estan en la carpeta
        File[] ficheros = directorioIN.listFiles();

        //Si tiene ficheros recorrerlos para sacar las url
        if(ficheros != null && ficheros.length > 0){
            
            for(File f : ficheros){

                //Sacar la url
                try {
                    url = Files.readString(f.toPath()).trim();

                    //Borrar el fichero para que no se use en otro hilo
                    f.delete();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return url;
    }
}

