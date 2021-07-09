package Cliente;

import javax.swing.JFileChooser;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

/*Funciones del cliente que haran las peticiones que se requieran al servidor*/
public class Cliente {

    private static int pto = 4444;
    private static String host = "127.0.0.1";
    private static String rutaDirectorios = "";
    public static String sep = System.getProperty("file.separator");
    public static int[] tipoFile;

    /**
     * *******************************************************************************************
     * ABRIR CARPETA
     * *******************************************************************************************
     */
    // Funcion abrir carpetas del servidor en el cliente
    public static void AbrirCarpeta(int indice) {
        try {
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream

            //La bandera tiene el valor de 3 = AbrirCarpeta
            dos.writeInt(3);
            dos.flush();

            //Enviamos el indice en donde se encuentra la carpeta dentro del arreglo de Files[]
            dos.writeInt(indice);
            dos.flush();

            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

            int numArchivos = dis.readInt();
            tipoFile = new int[numArchivos];

            for (int i = 0; i < numArchivos; i++) {
                String archivoRecibido = dis.readUTF();
                DropBox.modelo.addElement(archivoRecibido);
                tipoFile[i] = dis.readInt();
            }//for

            dis.close();
            dos.close();
            cl.close();
            System.out.println("Nueva carpeta abierta: Request recibido.");

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }

    /**
     * *******************************************************************************************
     * ENVIAR ARCHIVO
     * *******************************************************************************************
     */
    /*
		Descripción: La función permite enviar un archivo o directorio.
		Parametros: Archivo a enviar, Ruta de dónde se encuentra ese archivo
		Regresa: Nada, solo envía el archivo.
     */
    public static void EnviarArchivo(File f, String pathOrigen, String pathDestino) {
        try {
            if (f.isFile()) {
                Socket cl = new Socket(host, pto);
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream

                String nombre = f.getName();
                long tam = f.length();

                System.out.println("\nSe envia el archivo " + pathOrigen + " con " + tam + " bytes");
                DataInputStream dis = new DataInputStream(new FileInputStream(pathOrigen)); // InputStream

                //La bandera tiene el valor de 0 = Subir archivo
                dos.writeInt(0);
                dos.flush();

                //Se envia info de los archivos
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                dos.writeUTF(pathDestino);
                dos.flush();

                long enviados = 0;
                int pb = 0;
                int n = 0, porciento = 0;
                byte[] b = new byte[2000];

                while (enviados < tam) {
                    n = dis.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados += n;
                    porciento = (int) ((enviados * 100) / tam);
                    System.out.println("\r Enviando el " + porciento + "% --- " + enviados + "/" + tam + " bytes");
                } //while

                JOptionPane.showMessageDialog(null, "Se ha subido el archivo " + nombre + " con tamanio: " + tam);
                dis.close();
                dos.close();
                cl.close();
            } // If
            else {
                Socket cl = new Socket(host, pto);
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                String nombre = f.getName();
                String ruta = f.getAbsolutePath();
                System.out.println("Nombre: " + nombre + " Ruta: " + ruta);

                String aux = rutaDirectorios;
                rutaDirectorios = rutaDirectorios + sep + nombre;

                //La bandera tiene el valor de 4 = Subir Carpeta
                dos.writeInt(4);
                dos.flush();

                //Se envia info de los archivos
                dos.writeUTF(rutaDirectorios);
                dos.flush();

                // Envio los archivos que pertenecen al directorio creado
                File folder = new File(ruta);
                File[] files = folder.listFiles();

                for (File file : files) {
                    String path = rutaDirectorios + sep + file.getName();
                    System.out.println("Ruta destino en el servidor:" + path);
                    EnviarArchivo(file, file.getAbsolutePath(), path);
                }// for

                rutaDirectorios = aux;
                dos.close();
                cl.close();
            } // Else		
        } // try
        catch (Exception e) {
            e.printStackTrace();
        }
    } // Enviar archivo

    /**
     * *******************************************************************************************
     * SELECCIONAR ARCHIVOS
     * *******************************************************************************************
     */
    // Envia muchos archivos al servidor
    public static void SeleccionarArchivos() {
        try {
            JFileChooser jf = new JFileChooser();
            jf.setMultiSelectionEnabled(true);
            jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int r = jf.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {
                rutaDirectorios = "";
                File[] files = jf.getSelectedFiles();
                for (File file : files) {
                    String rutaOrigen = file.getAbsolutePath();
                    // Tipo caso base: La primera vez que mandemos un archivo
                    // Siempre estará en la raíz del servidor
                    EnviarArchivo(file, rutaOrigen, file.getName());
                }//for
                DropBox.modelo.clear();
                Actualizar();
            }//if   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *******************************************************************************************
     * ACTUALIZAR
     * *******************************************************************************************
     */
    public static void Actualizar() {
        try {
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream

            //La bandera tiene el valor de 1 = Actualizar 
            dos.writeInt(1);
            dos.flush();

            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

            int numArchivos = dis.readInt();
            tipoFile = new int[numArchivos];

            for (int i = 0; i < numArchivos; i++) {
                String archivoRecibido = dis.readUTF();
                DropBox.modelo.addElement(archivoRecibido);
                tipoFile[i] = dis.readInt();
            }//for

            dis.close();
            dos.close();
            cl.close();
            System.out.println("Carpeta del cliente actualizada.");

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//Actualizar

    /**
     * *******************************************************************************************
     * RECIBIR ARCHIVOS
     * *******************************************************************************************
     */
    public static void RecibirArchivos(String[] nombresArchivos, int tama) {
        try {
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

            //La bandera tiene el valor de 2 = Descargar seleccion
            dos.writeInt(2);
            dos.flush();

            dos.writeInt(tama);
            dos.flush();

            //Enviamos los indices de los archivos seleccionados
            String aux = "";

            for (int i = 0; i < tama; i++) {
                aux = nombresArchivos[i];
                dos.writeUTF(aux);
                dos.flush();
            }

            String nombre = System.getProperty("user.home") + "/Escritorio/";

            nombre = nombre + dis.readUTF();

            long tam = dis.readLong();
            System.out.println("\nSe recibe el archivo " + nombre + " con " + tam + "bytes");

            DataOutputStream dosArchivo = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream

            long recibidos = 0;
            int n = 0, porciento = 0;
            byte[] b = new byte[2000];

            while (recibidos < tam) {
                n = dis.read(b);
                dosArchivo.write(b, 0, n);
                dosArchivo.flush();
                recibidos += n;
                porciento = (int) ((recibidos * 100) / tam);
                System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + tam + " bytes");
            } // while

            JOptionPane.showMessageDialog(null, "Se ha descargado el archivo " + nombre + " con tamanio: " + tam);
            dos.close();
            dis.close();
            dosArchivo.close();
            cl.close();

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }

    /**
     * *******************************************************************************************
     * ELIMINAR ARCHIVO
     * *******************************************************************************************
     */
    /*
		Descripción: La función permite eliminar un archivo o directorio.
		Parametros: Archivo a eliminar
		Regresa: Nada, solo elimina el archivo.
     */
    public static void EliminarArchivo(String[] nombresArchivos, int tama) {
        try {
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

            //La bandera tiene el valor de 2 = Descargar seleccion
            dos.writeInt(5);
            dos.flush();

            dos.writeInt(tama);
            dos.flush();

            //Enviamos los indices de los archivos seleccionados
            String aux = "";

            for (int i = 0; i < tama; i++) {
                aux = nombresArchivos[i];
                dos.writeUTF(aux);
                dos.flush();
            }

            String nombre = dis.readUTF();
            long tam = dis.readLong();
            JOptionPane.showMessageDialog(null, "Se ha eliminado el archivo o carptea " + nombre + " con tamanio: " + tam);

            dos.close();
            dis.close();
            cl.close();

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    } // Eliminar archivo
}
