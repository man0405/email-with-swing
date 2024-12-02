package com.example.library.server.pop3;


import com.example.library.database.MongoDBHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pop3Server {
    private static final int POP3_PORT = 8110; // Cổng tùy chỉnh
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {

        MongoDBHandler dbHandler = new MongoDBHandler();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(POP3_PORT)) {
            System.out.println("POP3 Server đang chạy trên cổng " + POP3_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new Pop3ClientHandler(clientSocket, dbHandler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dbHandler.close();
            executor.shutdown();
        }
    }
}
