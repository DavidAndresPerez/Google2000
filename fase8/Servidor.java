package fase8;


import java.io.File;
import java.io.FileWriter;

import java.io.PrintWriter;

import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;


public class Servidor {

    //Clases necesarias
    private static GestorTiempo gestor = new GestorTiempo();
    public static void main(String[] args) {
    
        //Puerto que conecta con el cliente
        int puerto = 5000;

        //Configurar SSL
        System.setProperty("javax.net.ssl.keyStore", "cifrados/miAlmacen.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        //Encender el servidor para esperar al cliente
        try{

            //Sockets para la comunicacion cifrada
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(puerto);

            System.out.println("Servidor arrancado esperando conexión");

            //Aceptar la conexión del cliente
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado ");

                //Hilo que controla la lectura de 
                ControlCliente manejador = new ControlCliente(socketCliente);
                manejador.start(); 
            }

        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
    //Metodo que recoje cada linea del mensaje y la separa en partes
    public static void analizarMensaje(String mensaje){

        //Separar la frase en partes 
        String partes []  = mensaje.split(";");

        //Obtener cada parte de la linea
        if(partes.length == 3){
            String tipo = partes[0];  //Palabra o link
            String contenido = partes[1];   //Esa palabra o ese link
            String origen = partes[2];  //La url de donde lo saco
        
        //En función sea un link o una palabra hacer la labor que corresponde
         if (tipo.equals("Palabra")) {
                procesarPalabra(contenido, origen);
            } else if (tipo.equals("Link")) {
                procesarLink(contenido, origen);
        }
    }
    }

    //Metodo que recibe la palabra extraida y añade a la carpeta MD5 el link de su origem
    private static void procesarPalabra(String palabra, String url){
        
        //Sacar el md5 de la palabra
        String md5 = GenerarMD5.obtenerMD5(palabra);
    
        //guardar la información en la carpeta de los md5
        String inicial = "";
       
        //Crear carpetas en estructura de arbol
        for (int i = 0; i < 6; i++) {
            inicial += md5.charAt(i) + "/";
        }

        
        //Actualizar el archivo lastime.txt
        gestor.actualizarUltimaDescarga(inicial);

        File carpeta = new File("fase8_md5/" + inicial);

        if(!carpeta.exists()){
            carpeta.mkdirs();
        }

        //Archivo con el md5
        File file = new File(carpeta,"links.txt");
        
        //Escribir el contenido en ese archivo
        escribirEnArchivo(file,url);
    }    

    //Metodo que recibe el link extraido
    private static void procesarLink(String linkEncontrado, String urlOrigen) {
    
        //Sacar el md5 del link
        String md5Link = GenerarMD5.obtenerMD5(linkEncontrado);

        //Carpeta para los links
        String rutaInicial = "";

        for (int i = 0; i < 6; i++) {
            rutaInicial += md5Link.charAt(i) + "/";
        }
        
        //Actualizar el archivo lastime.txt
        gestor.actualizarUltimaDescarga(rutaInicial);

        File capetaLinks = new File("fase8_md5/" +rutaInicial);

        if(!capetaLinks.exists()){
            capetaLinks.mkdirs();
        }

        //Archivo con el md5
        File archivo = new File(capetaLinks, "in_links.txt");
        
        //Escribir en el archivo de links
        escribirEnArchivo(archivo, urlOrigen);  
    }

    //Metodo que escribe el contenido en los archivos
    private static synchronized void escribirEnArchivo(File archivo,String contenido){
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(archivo,true));
            
            printWriter.println(contenido);

            printWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
