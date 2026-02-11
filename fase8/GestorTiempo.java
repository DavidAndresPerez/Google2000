package fase8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GestorTiempo {

    //Ruta de la carpeta raiz de los md5
    private final String ruta_base = "fase8_md5/";

    //Intervalo de 3600 s que tiene que pasar para poder empezar a descargar en milisegundos
    private final int intervalo = 3600 * 1000;


    //Metodo que comprueba si puede empezar a descargar
    public synchronized boolean puedeDescargar(String rutaDirectorio){

        boolean puede = false;

        //Archivo en la ruta con el nombre del enunciado
        File archivo = new File(ruta_base + rutaDirectorio, "lasttime.txt");

        //Si el archivo no existe nunca se ha descargado y si puede empezar a descargar
        if(!archivo.exists()){
            puede = true;
        }

        try{

            //Leer el archivo lasstime.txt
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String contenido = br.readLine();

            if(contenido != null){

                //Convertir el texto del archivo en un numero
                long horaUltimaDescarga = Long.parseLong(contenido);
                long horaActual = System.currentTimeMillis();           //Para sacar la hora cuando se ejecuta el sistema


                //Comprobar si la diferencia de la hora actual y la ultima descarga es mayor a 3600 segundos puede descargar
                if((horaActual - horaUltimaDescarga) > intervalo){
                    puede = true;
                }
            }

            br.close();

        } catch(Exception e){

        }

        return puede;
    }

    //Metodo que actualiza la hora a la que se hizo la ultima descarga
    public synchronized void actualizarUltimaDescarga(String rutaDirectorio){
        try{

            //Sacar el directorio
            File directorio = new File(ruta_base + rutaDirectorio);

            //Comprobar que existe (el mkdirs es para que al recorrelo la primera vez y aun no estar creada no de false)
            if(directorio.exists() || directorio.mkdirs()){

                //Obtener el archivo lasstime
                File archivoTime = new File(directorio,"lasttime.txt");
                FileWriter fw = new FileWriter(archivoTime,false);  //El false se usa para sobreescribir en el archivo ya que vamos a cambiar su contenido
                PrintWriter pw = new PrintWriter(fw);

                //Guardar en el archivo la hora actual en milisegundos
                pw.print(System.currentTimeMillis());

                pw.close();
                fw.close();
            
            }

        } catch(IOException e){
            System.out.println("No se pudo actualizar el tiempo en: " + rutaDirectorio);
        }
    }
}
