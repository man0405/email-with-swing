package com.example.library.server.smtp;


import com.example.library.database.MongoDBHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmtpServer {
    private static final int SMTP_PORT = 2525; // Cổng tùy chỉnh
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        // Tải cấu hình từ file config.properties nếu cần


        MongoDBHandler dbHandler = new MongoDBHandler();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(SMTP_PORT)) {
            System.out.println("SMTP Server đang chạy trên cổng " + SMTP_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new SmtpClientHandler(clientSocket, dbHandler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dbHandler.close();
            executor.shutdown();
        }
    }
}
