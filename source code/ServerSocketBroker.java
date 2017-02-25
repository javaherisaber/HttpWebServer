import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Mahdi on 5/1/2017.
 * Accept incoming requests and response with HTTP protocol
 */
class ServerSocketBroker {

    static int PORT_NUMBER = 12000;

    void acceptSocketConnection() {
        try {
            ServerSocket serverSock = new ServerSocket(PORT_NUMBER);
            while (true) {
                Socket clientSocket = serverSock.accept();
                Thread t = new Thread(new ConnectionSocketHandler(clientSocket));
                t.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
