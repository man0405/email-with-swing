package com.example.library.server.smtp;

import com.example.library.database.MongoDBHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.*;


public class SmtpClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private StringBuilder dataBuffer;

    private MongoDBHandler dbHandler;

    public SmtpClientHandler(Socket socket, MongoDBHandler dbHandler) {
        this.clientSocket = socket;
        this.dbHandler = dbHandler;
        this.dataBuffer = new StringBuilder();
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // Gửi thông điệp chào mừng
            out.write("220 Simple SMTP Server\r\n");
            out.flush();

            String line;
            boolean isData = false;

            while ((line = in.readLine()) != null) {
                System.out.println("SMTP Client: " + line);

                if (line.startsWith("HELO") || line.startsWith("EHLO")) {
                    out.write("250 Hello " + line.substring(5) + "\r\n");
                } else if (line.startsWith("MAIL FROM:")) {
                    senderEmail = line.substring(10).trim().replaceAll("[<>]", "");
                    out.write("250 OK\r\n");
                } else if (line.startsWith("RCPT TO:")) {
                    recipientEmail = line.substring(8).trim().replaceAll("[<>]", "");
                    out.write("250 OK\r\n");
                } else if (line.equals("DATA")) {
                    out.write("354 End data with <CR><LF>.<CR><LF>\r\n");
                    isData = true;
                } else if (line.equals(".")) {
                    // Kết thúc phần DATA
                    isData = false;
                    // Giả định rằng dòng đầu tiên của DATA là Subject
                    String[] dataLines = dataBuffer.toString().split("\r\n", 2);
                    if (dataLines.length >= 2 && dataLines[0].startsWith("Subject: ")) {
                        subject = dataLines[0].substring(9).trim();
                        String content = dataLines[1];
                        dbHandler.saveEmail(senderEmail, recipientEmail, subject, content);
                        out.write("250 OK\r\n");
                        System.out.println("Email from " + senderEmail + " to " + recipientEmail + " saved.");
                    } else {
                        out.write("501 Syntax error in parameters or arguments\r\n");
                    }
                    dataBuffer.setLength(0); // Xóa buffer
                } else if (line.equals("QUIT")) {
                    out.write("221 Bye\r\n");
                    break;
                } else {
                    if (isData) {
                        dataBuffer.append(line).append("\r\n");
                    } else {
                        out.write("500 Unrecognized command\r\n");
                    }
                }
                out.flush();
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}