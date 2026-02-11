package fase8;

public class GenerarMD5 {

    //Metodo para obtener el MD5
    public static String obtenerMD5(String palabra){

    String md5 = "";

        try{

            //Import necesario en Java para usar MD5
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");

            //Sacar los bytes de la palabra
            byte[] bytes = md.digest(palabra.getBytes());

            //Aplicar el MD5
            for(byte b : bytes){
                //Convertir cada byte a hex y concatenarlo a la variable md5
                md5 += String.format("%02x", b);
            }

        }  catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }
        
        return md5;
    }
}
