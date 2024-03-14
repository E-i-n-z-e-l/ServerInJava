package ServerClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {

    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;


    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    /**
     * Конструктор класса ClientManager принимает объект Socket в качестве параметра и инициализирует поля socket,
     * bufferedReader и bufferedWriter.
     *
     * @param socket
     */
    public ClientManager(Socket socket, String recipientName) {
        this.socket = socket;
        // Будет хранить имя получателя личного сообщения;
        try {
            /* Создание bufferedReader и bufferedWriter осуществляется на основе потоков ввода-вывода сокета. */
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /* Считывается имя клиента из bufferedReader и присваивается полю name. */
            name = bufferedReader.readLine();

            clients.add(this); // Экземпляр ClientManager добавляется в список клиентов clients;
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: " + name + " подключился к чату.");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Реализация метода run() из интерфейса Runnable.
     */
    @Override
    public void run() {
        String massageFromClient;

        while (socket.isConnected()) { // В цикле while проверяется, подключен ли клиент к серверу;
            try {
                /* Внутри цикла происходит чтение сообщения от клиента через bufferedReader. */
                massageFromClient = bufferedReader.readLine();
                /* Полученное сообщение передается методу broadcastMessage для рассылки всем остальным клиентам. */
                broadcastMessage(massageFromClient);
            }
            /* Если возникает ошибка ввода-вывода, вызывается метод closeEverything() и выходит из цикла. */ catch (
                    IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    //    /**
//     * Метод broadcastMessage используется для отправки сообщения всем остальным клиентам, кроме отправителя.
//     *
//     * @param message
//     */
//    private void broadcastMessage(String message){
//        /* Происходит перебор всех клиентов в списке clients. */
//        for (ClientManager client: clients) {
//            try {
//                /* Если клиент не является отправителем сообщения (имя клиента не совпадает с name),
//                сообщение отправляется клиенту через bufferedWriter. */
//                if (!client.name.equals(name)) {
//                    client.bufferedWriter.write(message);
//                    client.bufferedWriter.newLine();
//                    client.bufferedWriter.flush();
//                }
//            }
//            /* Если возникает ошибка ввода-вывода, вызывается метод closeEverything(). */
//            catch (IOException e){
//                closeEverything(socket, bufferedReader, bufferedWriter);
//            }
//        }
//    }
    private void broadcastMessage(String message) {
        if (message.startsWith("@")) {
            String[] splitMessage = message.split(" ", 2);
            String recipient = splitMessage[0].substring(1);
            String trimmedMessage = splitMessage[1].trim();
            for (ClientManager client : clients) {
                if (client.name.equals(recipient)) {
                    try {
                        client.bufferedWriter.write("Личное сообщение от " + name + ": " + trimmedMessage);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                    break;
                }
            }
        } else {
            for (ClientManager client : clients) {
                try {
                    if (!client.name.equals(name)) {
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }


    /**
     * Метод closeEverything используется для закрытия сокета, bufferedReader и bufferedWriter.
     *
     * @param socket
     * @param bufferedReader
     * @param bufferedWriter
     */
    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // Удаление клиента из коллекции:
        removeClient();
        try {
            // Завершаем работу буфера на чтение данных:
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            // Завершаем работу буфера для записи данных:
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            // Закрытие соединения с клиентским сокетом:
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод removeClient используется для удаления клиента из списка clients при отключении.
     */
    private void removeClient() {
        clients.remove(this); // Удаляется текущий экземпляр ClientManager из списка clients;
        System.out.println(name + " покинул чат."); // Выводится сообщение о выходе клиента из чата;

        /* Отправляется широковещательное сообщение о выходе клиента из чата на сервер. */
        broadcastMessage("Server: " + name + " покинул чат.");
    }

}
