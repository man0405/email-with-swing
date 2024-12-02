package com.example.library.server.pop3;

// File: src/server/pop3/Pop3ClientHandler.java


import com.example.library.database.MongoDBHandler;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Pop3ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    private boolean isAuthenticated = false;
    private String username;

    private MongoDBHandler dbHandler;

    private List<Document> userEmails;

    public Pop3ClientHandler(Socket socket, MongoDBHandler dbHandler) {
        this.clientSocket = socket;
        this.dbHandler = dbHandler;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // Gửi thông điệp chào mừng
            out.write("+OK POP3 server ready\r\n");
            out.flush();

            String line;

            while ((line = in.readLine()) != null) {
                System.out.println("POP3 Client: " + line);

                if (line.startsWith("USER")) {
                    username = line.substring(5).trim();
                    out.write("+OK User accepted\r\n");
                } else if (line.startsWith("PASS")) {
                    String password = line.substring(5).trim();
                    // Xác thực người dùng
                    if (dbHandler.authenticateUser(username, password)) {
                        isAuthenticated = true;
                        // Lấy danh sách email của người dùng
                        userEmails = dbHandler.getUserEmails(username);
                        out.write("+OK Authenticated\r\n");
                    } else {
                        out.write("-ERR Authentication failed\r\n");
                    }
                } else if (line.equals("STAT") && isAuthenticated) {
                    // Trả về số lượng và kích thước của email
                    int messageCount = userEmails.size();
                    int totalSize = getTotalSize(userEmails);
                    out.write("+OK " + messageCount + " " + totalSize + "\r\n");
                } else if (line.startsWith("LIST") && isAuthenticated) {
                    if (line.equals("LIST")) {
                        out.write("+OK Listing messages\r\n");
                        for (int i = 0; i < userEmails.size(); i++) {
                            int size = userEmails.get(i).getString("content").length();
                            out.write((i + 1) + " " + size + "\r\n");
                        }
                        out.write(".\r\n");
                    } else {
                        try {
                            int msgNumber = Integer.parseInt(line.substring(5).trim());
                            if (msgNumber >= 1 && msgNumber <= userEmails.size()) {
                                int size = userEmails.get(msgNumber - 1).getString("content").length();
                                out.write("+OK " + msgNumber + " " + size + "\r\n");
                            } else {
                                out.write("-ERR No such message\r\n");
                            }
                        } catch (NumberFormatException e) {
                            out.write("-ERR Invalid message number\r\n");
                        }
                    }
                } else if (line.startsWith("RETR") && isAuthenticated) {
                    try {
                        int msgNumber = Integer.parseInt(line.substring(5).trim());
                        if (msgNumber >= 1 && msgNumber <= userEmails.size()) {
                            Document email = userEmails.get(msgNumber - 1);
                            String content = email.getString("content");
                            out.write("+OK " + content.length() + " octets\r\n");
                            out.write(content + "\r\n.\r\n");
                        } else {
                            out.write("-ERR No such message\r\n");
                        }
                    } catch (NumberFormatException e) {
                        out.write("-ERR Invalid message number\r\n");
                    }
                } else if (line.equals("QUIT")) {
                    out.write("+OK Bye\r\n");
                    break;
                } else {
                    out.write("-ERR Unrecognized command\r\n");
                }
                out.flush();
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getTotalSize(List<Document> emails) {
        int totalSize = 0;
        for (Document email : emails) {
            totalSize += email.getString("content").length();
        }
        return totalSize;
    }
}
