/**
 * Created by Mahdi on 5/1/2017.
 * Internet Engineering project
 */
class MainClass {

    public static void main(String[] args){
        ServerSocketBroker handler = new ServerSocketBroker();
        handler.acceptSocketConnection();
    }

}
