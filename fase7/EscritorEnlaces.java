    package fase7;

    import java.io.BufferedWriter;
    import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
    import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
    import java.security.NoSuchAlgorithmException;

    public class EscritorEnlaces {

        //Variables
        private String rutaIN;

        //Constructor
        public EscritorEnlaces(String ruta){
            this.rutaIN = ruta;
            File carpeta = new File(rutaIN);

            //Si no existe la carpeta crearla
            if(!carpeta.exists()){
                carpeta.mkdirs();
            }
        }

        //Metodo que guarda los enlaces en la ruta de la carpeta in
       public synchronized void guardarEnlace(String url) {
            //Generar nombre en md5 para guardarlo
            String archivo = generarNombre(url);

            //Si no existe crear la carpeta IN
            File carpeta = new File("IN");

            if (!carpeta.exists()) {
               carpeta.mkdirs();
            }

            //Crear el archivo en la carpeta
            File f = new File(carpeta, archivo);
        
            //Escribir la url dentro de el 
            try (FileWriter fw = new FileWriter(f);
               PrintWriter pw = new PrintWriter(fw)) {
        
                pw.print(url);
                pw.flush();
            
            } catch (IOException e) {
                System.err.println("Error físico al escribir en IN: " + e.getMessage());
            }
        }          

        //Metodo que genera el nombre del archivo usando md5
        private String generarNombre(String url){
            String md5 = "";

            //Import necesario en Java para usar MD5
            java.security.MessageDigest md;
            try {
                md = java.security.MessageDigest.getInstance("MD5");

                //Sacar los bytes de la palabra
                byte[] bytes = md.digest(url.getBytes());

                //Aplicar el MD5
                for(byte b : bytes){
            
                    //Convertir cada byte a hex y concatenarlo a la variable md5
                    md5 += String.format("%02x", b);
                }

                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            
            return md5;
        }

    }
