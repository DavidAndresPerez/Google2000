package fase2;


public class _Principal {
    
    public static void main(String[] args) {

        String archivo = "seeds.txt";

        //Clase gestor del archivo
        GestorArchivo gestor = new GestorArchivo(archivo);

        gestor.leerArchivo();

        //Instanciar tres hilos
        Lector t1 = new Lector(gestor, "thread1.txt");
        Lector t2 = new Lector(gestor, "thread2.txt");
        Lector t3 = new Lector(gestor, "thread3.txt");

        //Lanzarlos
        t1.start();
        t2.start();
        t3.start();

    }
}
