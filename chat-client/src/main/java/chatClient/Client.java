package chatClient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Класс Client представляет клиентскую часть чата, где инициализируются сокет и потоки ввода/вывода
 * для общения с сервером.<p></p>
 * Методы listenForMessage() и sendMessage() обеспечивают чтение и отправку сообщений между клиентом и
 * сервером с помощью потоков. <p></p>
 * Конструктор и метод closeEverything() отвечают за создание и закрытие ресурсов, связанных с сокетом и потоками.
 */
public class Client {
    private final Socket socket;
    private final String name;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Client(Socket socket, String userName){
        this.socket = socket;
        name = userName;
        try
        {
            /* Создаем объекты BufferedWriter и BufferedReader для записи и чтения данных из сокета соответственно. */
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        /* Если возникает ошибка ввода-вывода, вызывается метод closeEverything() для закрытия всех ресурсов. */
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }


    }

    /**
     * Метод, используемый для закрытия сокета, BufferedReader и BufferedWriter.<p></p>
     * Если какой-либо из ресурсов не равен null, происходит закрытие данного ресурса.<p></p>
     * Если возникает ошибка ввода-вывода при закрытии ресурсов, печатается трассировка стека исключения.
     * @param socket
     * @param bufferedReader
     * @param bufferedWriter
     */
    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Метод запускает новый поток, в котором происходит прослушивание входящих сообщений от сервера.<p></p>
     *
     */
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()){ // В цикле while проверяется, подключен ли клиент к серверу;
                    try {
                        /* Внутри цикла происходит чтение строки из bufferedReader с помощью метода readLine(). */
                        message = bufferedReader.readLine();
                        System.out.println(message); // Прочитанное сообщение выводится на консоль;
                    }
                    /* Если возникает ошибка ввода-вывода, вызывается метод closeEverything() для закрытия всех ресурсов. */
                    catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

//    /**
//     * Метод отправляет сообщения на сервер.<p></p>
//     *
//     */
//    public void sendMessage(){
//        try {
//            /* Сначала отправляется имя пользователя, а затем в цикле while отправляются
//            все последующие сообщения, введенные пользователем с консоли. */
//            bufferedWriter.write(name);
//            bufferedWriter.newLine();
//            bufferedWriter.flush(); // Немедленная отправка сообщения;
//
//            Scanner scanner = new Scanner(System.in);
//            while (socket.isConnected()) {
//                String message = scanner.nextLine();
//
//                /* Считанная строка записывается в bufferedWriter с добавлением имени пользователя. */
//                bufferedWriter.write(name + ": " + message);
//
//                /* Затем текст переходит на новую строку и сбрасывается из буфера с помощью метода flush(). */
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//            }
//            /* Если возникает ошибка ввода-вывода, вызывается метод closeEverything() для закрытия всех ресурсов. */
//        } catch (IOException e){
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//    }

    public void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                if (message.startsWith("@")) {
                    bufferedWriter.write(message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    bufferedWriter.write(name + ": " + message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
}
