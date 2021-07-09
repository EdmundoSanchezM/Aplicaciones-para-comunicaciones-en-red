import java.util.Date;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.FileChannel;

class Requests{
    private Mime mime;
    private String headers;
    int bufferSize = 10240;
    private ByteBuffer b2;

    public void response( String statusLine , SocketChannel ch ){
        try{
            mime = new Mime();
            if ( statusLine == null ){
                String empty = "<html><head><title>Servidor WEB</title><body> <br>statusLinea Vacia</br></body></html>";
                b2 = ByteBuffer.wrap(empty.getBytes());
                ch.write( b2 );
            }
            else{
                headers = "HTTP/1.1 200 OK \r\n" +
                          "Server: webServer/1.0 \n" +
                          "Content-Type: text/html \n"+
                          "Date: " + new Date() + " \n\r\n";

                if( statusLine.startsWith("GET") ){
                    
                    if( statusLine.indexOf("?")>1&&statusLine.indexOf("?")<20){ 
                        System.out.println("mememememoeomeeom_:"+statusLine.indexOf("?" ));
                            String[] reqLineas = statusLine.split("\n");
                        String response = getParameters( reqLineas[0] );
                        b2 = ByteBuffer.wrap(headers.getBytes());
                        ch.write( b2 );
                        b2 = ByteBuffer.wrap(response.getBytes());
                        ch.write( b2 );
	                    System.out.println("Respuesta GET: \n" + response);
                        
                        
                    }else{
                        String fileName = getFileName( 5 , statusLine );
                        fileName = getMetadata( fileName );
                        System.out.println("Archivo: " + fileName);
                        b2 = ByteBuffer.wrap(headers.getBytes());
                        ch.write( b2 );
	                sendSource( fileName , ch );
                    }
                } 
                else if ( statusLine.startsWith("POST") ){
                    String response = getParameters( statusLine );
                    b2 = ByteBuffer.wrap(headers.getBytes());
                    ch.write( b2 );
                    b2 = ByteBuffer.wrap(response.getBytes());
                    ch.write( b2 );
                    System.out.println("Respuesta POST: \n" + response);
                } 
                else if ( statusLine.startsWith("HEAD") ){
                    String fileName = getFileName( 6 , statusLine );
                    System.out.println("Respuesta HEAD:");
                    fileName = getMetadata( fileName );
                    b2 = ByteBuffer.wrap(headers.getBytes());
                    ch.write( b2 );
                }
                else if( statusLine.startsWith("DELETE") ){
                    String fileName = getFileName( 8 , statusLine );
                    deleteSource(fileName , ch);
                }
                else{
                    String error501 =   "HTTP/1.1 501 Not Implemented \r\n" +
		              				    "Server: webServer/1.0 \n" +
                                        "Content-Type: text/html \n" +
                                        "Date: " + new Date() + " \n\r\n";

                    b2 = ByteBuffer.wrap(error501.getBytes());
                    ch.write( b2 );
                    sendSource("error/501.html",ch);
                    System.out.println("Respuesta ERROR 501: \n" + error501);
                }
                System.out.println("Cliente Atendido\n");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getParameters( String statusLine){
        String method = "",line;
                           
        if( statusLine.startsWith("GET")){
            method = "GET";
        	// Line: GET /?Nombre=&Direccion=&Telefono=&Comentarios= HTTP/1.1
        	StringTokenizer tokens = new StringTokenizer( statusLine , "?" );
            line = tokens.nextToken();
            line = tokens.nextToken();
            tokens = new StringTokenizer(line, " ");
            line = tokens.nextToken();
        }else{
            method= "POST";
            String[] reqLineas = statusLine.split("\n");
            System.out.println("Tam: " + reqLineas.length);
            int ult = reqLineas.length - 1;
            line = reqLineas[ult];
            System.out.println("Obtener: " +  line );
        }

        StringTokenizer tokens = new StringTokenizer(line, "&");
        String html = headers +
					"<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Metodo " + method +
                    "</title></head><body><center><h2>Parametros obtenidos, Método " 
                    + method + "</h2><br>\n" + "<table border='0'><tr><th>Parametro</th><th>Valor</th></tr>";

        while(tokens.hasMoreTokens()) {
        	String parameters = tokens.nextToken();
        	StringTokenizer paramValue = new StringTokenizer( parameters , "=" );
        	String param = "";
            String value = "";
            if ( paramValue.hasMoreTokens() )
                param = paramValue.nextToken();
            if ( paramValue.hasMoreTokens() )
        	    value = paramValue.nextToken();
        	html = html + "<tr><td><b>" + param + "</b></td><td>" + value + "</td></tr>\n";
        }
        html = html + "</table></center></body></html>\r\n";
        return html;
    }

    private String getFileName( int start , String statusLine ){
        String file = statusLine.substring( start , statusLine.length() );
        String source = file.substring( 0 , file.indexOf(" ") );
        if( source.compareTo("") == 0 )
            source = "index.html";
        return source;
    }

    private String getMetadata( String fileName ){
        String responseHead = "";
        try{
            File file = new File( fileName );
        	String statusResponse = "HTTP/1.1 200 OK \r\n";
        	if( !file.exists() ){
        		fileName= "error/404.html"; // Recurso no encontrado
        		statusResponse = "HTTP/1.1 404 Not Found \r\n";
        	}
        	else if( file.isDirectory() ) {
        		fileName = "error/403.html"; // Recurso privado
        		statusResponse = "HTTP/1.1 403 Forbidden \r\n";
        	}
    		DataInputStream d = new DataInputStream( new FileInputStream(fileName) ); 
    		int len = d.available();
            int index = fileName.indexOf(".");
            String extension = fileName.substring( index + 1 , fileName.length() );
            d.close();
            responseHead = statusResponse + 
		                    "Server: webServer/1.0 \n" +
                            "Content-Type: " + mime.get(extension) + " \n" +
                            "Date: " + new Date() + " \n" + 
                            "Content-Length: " + len + " \n\r\n";
            this.headers = responseHead;
        }catch( Exception e ) {
            e.printStackTrace();
        }
        System.out.println( responseHead );
        return fileName;
    }
    
    private void sendSource( String fileName , SocketChannel ch ){
        try{
            File file = new File( fileName );
            if ( file.exists() && !file.isDirectory() ){
                ByteBuffer buffer = ByteBuffer.allocate( bufferSize );
                FileInputStream is = new FileInputStream( fileName );
                FileChannel source = is.getChannel();
				int res;
				int counter = 0;
				buffer.clear();
				while( ( res = source.read( buffer ) ) != -1 ){							
                    counter += res;
                    System.out.print("\rEnviando "+ counter +" bytes");
					buffer.flip();
                    while( buffer.hasRemaining() ){
                        ch.write(buffer);
                    }
                    buffer.clear();
                }
                System.out.print("\n");
				source.close(); 	
				is.close();	
            }
        }catch( Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void deleteSource( String fileName , SocketChannel ch ){
        File f = new File( fileName );
        try{
            if( f.exists() ){
                String[] name = fileName.split("/");
                if( f.delete() ){
                    System.out.println( "Archivo: " + fileName + " eliminado");
                    String delete = headers +
                                      "<html><head><meta charset='UTF-8'><title>202 Succesuful</title></head>" +
                                      "<body><h1>202 Recurso eliminado exitosamente.</h1>" +
                                      "<p>El recurso " + name[1] + " ha sido eliminado permanentemente del servidor.</p>" + 
                                      "</body></html>";
                    b2 = ByteBuffer.wrap(delete.getBytes());
                    ch.write( b2 );
                    
                    System.out.println("Respuesta DELETE: \n" + delete);
                }
                else{
                    System.out.println( "El archivo: " + fileName + " no puede ser eliminado");
                    String error =  "HTTP/1.1  500 Internal server error \r\n" +
                                    "Server: webServer/1.0 \n" +
                                    "Content-Type: text/html \n" +
                                    "Date: " + new Date() + " \n\r\n" +
                                    "<html><head><meta charset='UTF-8'><title>Server Error</title></head>" +
                                    "<body><h1>500 Internal server error</h1>" +
                                    "<p>El recurso: " + name[1] + " no se puede borrar.</p>" +
                                    "</body></html>";
                    b2 = ByteBuffer.wrap(error.getBytes());
                    ch.write( b2 );
                    System.out.println("Respuesta DELETE: \n" + error);
                }
            }
            else{
                String error = "HTTP/1.1 404 Not Found \r\n" +
                                "Server: webServer/1.0 \n" +
                                "Content-Type: text/html \n" +
                                "Date: " + new Date() + " \n\r\n" +
                                "<html><head><meta charset='UTF-8'><title>404 Not found</title></head>" +
                                "<body><h1>404 Not found</h1>" +
                                "<p>El recurso: " + fileName + " no se encontro.</p>" +
                                "</body></html>";
                b2 = ByteBuffer.wrap(error.getBytes());
                ch.write( b2 );
                System.out.println("Respuesta DELETE: \n" + error);
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        System.out.println( "El archivo: " + fileName + " no existe");
    }
}