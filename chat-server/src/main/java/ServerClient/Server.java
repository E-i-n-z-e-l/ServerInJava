package ServerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Метод runServer() выполняет основную логику работы сервера.
     */
    public void runServer(){
        try {
            /* В цикле while проверяется, не закрыт ли серверный сокет serverSocket.
            *  Если серверный сокет serverSocket не закрыт, вызывается метод accept() серверного сокета. */
            while (!serverSocket.isClosed()) {

                /* Метод accept() блокирует выполнение программы, пока не будет установлено соединение с клиентом. */
                Socket socket = serverSocket.accept();

                /* После установления соединения создается объект класса ClientManager, передавая ему сокет. */
                ClientManager clientManager = new ClientManager(socket);
                System.out.println("Подключен новый клиент!");

                /* Создается новый поток Thread для каждого клиента и запускается с объектом ClientManager.
                *  Это позволяет обрабатывать каждого клиента в отдельном потоке. */
                Thread thread = new Thread(clientManager);
                thread.start();
            }
        }
        catch (IOException e){
            closeSocket();
        }
    }




    /**
     * Метод closeSocket() используется для закрытия серверного сокета serverSocket.<p></p>
     * Если serverSocket не равен null, вызывается метод close() для закрытия серверного сокета.<p></p>
     * Если возникает ошибка при закрытии серверного сокета, печатается трассировка стека исключения.
     */
    private void closeSocket(){
        try{
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
