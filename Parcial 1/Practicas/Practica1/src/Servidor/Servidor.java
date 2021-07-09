package Servidor;

// SERVIDOR
import java.net.*;
import java.io.*;
import java.util.zip.*;

public class Servidor {

    public static String sep = System.getProperty("file.separator");
    private static String rutaServer = "." + sep + "serverP1" + sep;
    private static File[] list;
    private static String rutaActual = "";
    private static int numVeces = 0;

    /**
     * *******************************************************************************************
     * 0. RECIBIR ARCHIVOS
     * *******************************************************************************************
     */
    // Valor de la bandera = 0
    public static void RecibirArchivos(DataInputStream dis, String nombre) throws IOException {
        long tam = dis.readLong();
        String pathDestino = dis.readUTF();
        nombre = rutaServer + pathDestino;

        System.out.println("\nSe recibe el archivo " + nombre + " con " + tam + "bytes");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream

        long recibidos = 0;
        int n = 0, porciento = 0;
        byte[] b = new byte[2000];

        while (recibidos < tam) {
            n = dis.read(b);
            dos.write(b, 0, n);
            dos.flush();
            recibidos += n;
            porciento = (int) ((recibidos * 100) / tam);
            System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + tam + " bytes");
        } // while

        System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " recibido.");
        dos.close();
        dis.close();
    } // RecibirArchivos

    /**
     * *******************************************************************************************
     * 1. ACTUALIZAR CLIENTES
     * *******************************************************************************************
     */
    //Valor de la bandera = 1
    public static void ActualizarCliente(Socket cl, DataInputStream dis, String path, int bandera) throws IOException {
        File archivosRuta = new File(path);

        if (!archivosRuta.exists()) {
            archivosRuta.mkdir();
        }//if

        if (bandera == 1) {
            rutaActual = rutaActual + sep + archivosRuta.getName();
            System.out.println("Ubicacion: " + rutaActual);
        }

        list = archivosRuta.listFiles();

        DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // OutputStream

        dos.writeInt(list.length);
        dos.flush();

        String info = "";
        int tipo = 0;

        for (File f : list) {
            if (f.isDirectory()) {
                tipo = 1;
                if (bandera == 0) {//Ruta raiz - Inicio
                    info = "." + sep + f.getName();
                } else {//Abrir ruta y concatenar
                    info = "." + rutaActual + sep + f.getName();
                }
            }//if
            else {
                tipo = 2;
                if (bandera == 0) {//Ruta raiz - Inicio
                    info = f.getName();
                    //info = f.getName() + "  -------  " + f.length() + " bytes";
                } else {//Abrir ruta y concatenar
                    info = "." + rutaActual + sep + f.getName();
                    //info = "." + rutaActual + sep + f.getName() + "  -------  " + f.length() + " bytes";
                }
            }//else
            dos.writeUTF(info);
            dos.flush();
            dos.writeInt(tipo);
            dos.flush();

            tipo = 0;
        }//for
        dos.close();
        System.out.println("Informacion enviada al cliente: Carpeta actualizada.");
    }//Actualizar

    /**
     * *******************************************************************************************
     * CREAR ARCHIVO .ZIP
     * *******************************************************************************************
     */
    public static void crearZIP(DataInputStream dis, int tam) {
        try {
            //Enviamos los indices de los archivos seleccionados
            String[] nombreArchivos = new String[tam];
            String aux = "";
            int i, j;
            for (i = 0; i < tam; i++) {
                nombreArchivos[i] = dis.readUTF();
                System.out.println("\nArchivo: " + nombreArchivos[i]);
            }

            // Quito ./ al nombre del directorio
            char aux1 = ' ', aux2 = ' ';
            String nombre = "";
            for (i = 0; i < tam; i++) {
                aux1 = nombreArchivos[i].charAt(0);
                if (aux1 == '.') {
                    for (j = 2; j < nombreArchivos[i].length(); j++) {
                        nombre = nombre + Character.toString(nombreArchivos[i].charAt(j));
                    }
                    nombreArchivos[i] = nombre;
                    nombre = "";
                }
            }
            String destino = rutaServer + "Download" + numVeces + ".zip";
            FileOutputStream fos = new FileOutputStream(destino);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            String sourceFile = "";
            for (i = 0; i < tam; i++) {
                // Le doy la ruta de mi archivo o directorio
                sourceFile = rutaServer + nombreArchivos[i];
                File fileToZip = new File(sourceFile);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
                sourceFile = " ";
            }
            zipOut.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *******************************************************************************************
     * ZIP
     * *******************************************************************************************
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(sep)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + sep));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + sep + childFile.getName(), zipOut);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;

        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
        System.out.println("Archivos comprimidos en un ZIP. Listo para enviar...");
    }

    /**
     * *******************************************************************************************
     * EnviarArchivo
     * *******************************************************************************************
     */
    public static void EnviarArchivo(DataOutputStream dos, File f) {
        try {
            String nombre = f.getName();
            long tam = f.length();
            String path = f.getAbsolutePath();
            System.out.println("\nSe envia el archivo " + nombre + " con " + tam + " bytes");
            DataInputStream disArchivo = new DataInputStream(new FileInputStream(path)); // InputStream

            //Se envia info de los archivos
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();

            long enviados = 0;
            int n = 0, porciento = 0;
            byte[] b = new byte[2000];

            while (enviados < tam) {
                n = disArchivo.read(b);
                dos.write(b, 0, n);
                dos.flush();
                enviados += n;
                porciento = (int) ((enviados * 100) / tam);
                System.out.println("\r Enviando el " + porciento + "% --- " + enviados + "/" + tam + " bytes");
            } //while

            System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " enviado.");

            disArchivo.close();
            dos.close();
        } // try
        catch (Exception e) {
            e.printStackTrace();
        }
    } // Enviar archivo

    /**
     * *******************************************************************************************
     * EnviarArchivo
     * *******************************************************************************************
     */
    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public static void EliminarArchivo(DataInputStream dis, int tam, DataOutputStream dos) {
        try {
            String[] nombreArchivos = new String[tam];
            String aux = "";
            int i, j;
            for (i = 0; i < tam; i++) {
                nombreArchivos[i] = dis.readUTF();
                String nombre = nombreArchivos[i];
                boolean bandera = false;
                if (nombre.indexOf(".") == 0) {
                    nombre = nombre.substring(2,nombre.length());
                    bandera = true;
                }
                System.out.println("\nArchivo: " + nombre);
                File f = new File(rutaServer + nombre);
                if (bandera) {
                    deleteDir(f);
                    System.out.println("Carpeta eliminada");
                } else {
                    if (f.delete()) {
                        System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " Eliminado.");
                    } else {
                        System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " no eliminado.");

                    }
                }
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
            }

        } // try
        catch (Exception e) {
            e.printStackTrace();
        }
    } // Enviar archivo

    /**
     * *******************************************************************************************
     * MAIN
     * *******************************************************************************************
     */
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(4444);
            s.setReuseAddress(true);
            System.out.println("Servidor de archivos iniciado, esperando cliente...");

            // Espera clientes
            for (;;) {
                Socket cl = s.accept();
                System.out.println("\n\nCliente conectado desde " + cl.getInetAddress() + " " + cl.getPort());
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
                DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

                int bandera = dis.readInt();

                if (bandera == 0) {
                    //Subir un archivo -> El servidor recibe
                    String nombre = dis.readUTF();
                    RecibirArchivos(dis, nombre);
                } else if (bandera == 1) {
                    //Ver archivos / Actualizar -> El servidor envia los nombres de los archivos
                    //Bandera = 0. Se actualiza la carpeta raiz
                    rutaActual = "";
                    ActualizarCliente(cl, dis, rutaServer, 0);

                } else if (bandera == 2) {
                    //Descargar archivos -> El servidor prepara y envia archivos
                    //Subir archivos -> El servidor recibe
                    int tam = dis.readInt();
                    String path = "Download" + numVeces + ".zip";
                    path = rutaServer + path;
                    System.out.println("" + path);
                    File archivoZip = new File(path);
                    System.out.println("" + archivoZip.getAbsoluteFile());

                    crearZIP(dis, tam);

                    if (archivoZip.exists()) {
                        //System.out.println("Si existeee");
                        System.out.println("La path del archivo esta en: " + path + " Con nombre: " + archivoZip.getName());
                        EnviarArchivo(dos, archivoZip);
                        // Lo elimino porque no debe estar en el servidor, solo lo hice temporalmente
                        if (archivoZip.delete()) {
                            System.out.println("Archivo temporal Download" + numVeces + ".zip eliminado");
                        }
                    }

                    numVeces++;

                } else if (bandera == 3) {
                    //Abrir carpeta -> El servidor envia los nombres de los contenidos de la carpeta seleccionada
                    int ubicacionRuta = dis.readInt();
                    //Bandera = 1. Se navega dentro de una carpeta
                    String nuevaRuta = "" + list[ubicacionRuta].getAbsoluteFile();
                    ActualizarCliente(cl, dis, nuevaRuta, 1);
                } else if (bandera == 4) {
                    //Subir archivos -> El servidor recibe
                    String rutaDirectorio = dis.readUTF();
                    String path = rutaServer + rutaDirectorio;
                    File archivosRuta = new File(path);
                    if (!archivosRuta.exists()) {
                        archivosRuta.mkdir();
                    }
                } else if (bandera == 5) {
                    int tam = dis.readInt();
                    EliminarArchivo(dis, tam, dos);
                } else {
                    System.out.println("Error al atender la solicitud del cliente.");
                }
                dis.close();
                cl.close();
            }//for
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main
}
