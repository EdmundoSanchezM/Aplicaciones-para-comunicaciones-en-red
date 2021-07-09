import java.util.Iterator;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;

public class ServerHTTP {
    public static void main(String[] args){
        int pto = 9000;
        Requests request;
        try{
            ServerSocketChannel s = ServerSocketChannel.open();
            s.configureBlocking(false);
            s.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            s.socket().bind(new InetSocketAddress(pto));
            Selector sel = Selector.open();
            s.register(sel,SelectionKey.OP_ACCEPT);
            System.out.println("\t*********** Servidor HTTP ***********\n");
            while(true){
                sel.select();
                Iterator<SelectionKey>it= sel.selectedKeys().iterator();
                while( it.hasNext() ){
                    SelectionKey k = (SelectionKey)it.next();
                    it.remove();
                    if( k.isAcceptable() ){
                        SocketChannel cl = s.accept();
                        System.out.println("Cliente conectado desde->"+cl.socket().getInetAddress().getHostAddress()+":"+cl.socket().getPort());
                        cl.configureBlocking(false);
                        cl.register(sel,SelectionKey.OP_READ);
                        continue;
                    }
                    if( k.isReadable() ){
                        String statusLine;
                        SocketChannel ch =(SocketChannel)k.channel();
                        ByteBuffer b = ByteBuffer.allocate(2000);
                        b.clear();
                        int n = ch.read(b);
                        b.flip();
                        if( n != -1)
                            statusLine = new String(b.array(),0,n);
                        else
                            statusLine = "";
                        request = new Requests();
                        request.response( statusLine, ch );
                        ch.close();
                        continue;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
