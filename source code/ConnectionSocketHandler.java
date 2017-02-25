import java.io.*;
import java.net.Socket;

/**
 * Created by Mahdi on 5/1/2017.
 * handling http response through a connection socket
 */
class ConnectionSocketHandler implements Runnable {

    private Socket clientSocket;
    private String httpRequest;
    private BufferedReader reader;

    ConnectionSocketHandler(Socket socket) {
        try {
            System.out.println("New Incoming Request!!");
            this.clientSocket = socket;
            InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
            reader = new BufferedReader(isReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        readRequest();
        ResponseEntity entity = new HttpHelper().getResponseEntity(httpRequest);
        if (entity.getFile() == null) {
            writeEmptyResponse(entity.getHeader());
        } else {
            writeBinaryResponse(entity.getFile(), entity.getHeader());
        }
    }

    private void readRequest() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    break;
                httpRequest += line + "\n";
            }
            System.out.println(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEmptyResponse(String header) {
        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new BufferedOutputStream(clientSocket.getOutputStream()), "UTF-8")
            );
            out.write(header);
            out.flush();
            out.close();
            clientSocket.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBinaryResponse(File file, String header) {
        try {
            byte[] buffer = new byte[(int) file.length()];
            BufferedOutputStream outStream = new BufferedOutputStream(clientSocket.getOutputStream());
            PrintWriter out = new PrintWriter(outStream);
            out.write(header);
            out.flush();
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
            for (int read = inStream.read(buffer); read >= 0; read = inStream.read(buffer))
                outStream.write(buffer, 0, read);
            inStream.close();
            outStream.close();
            clientSocket.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
