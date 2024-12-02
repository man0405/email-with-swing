package com.example.library.client;

// File: EmailReceiver.java
import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class EmailReceiver {
    private String pop3Host;
    private int pop3Port;
    private String username;
    private String password;

    public EmailReceiver(String pop3Host, int pop3Port, String username, String password) {
        this.pop3Host = pop3Host;
        this.pop3Port = pop3Port;
        this.username = username;
        this.password = password;
    }

    public String receiveEmails() {
        StringBuilder emails = new StringBuilder();
        try {
            Socket socket = new Socket(pop3Host, pop3Port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read server welcome message
            System.out.println("Server: " + in.readLine());

            out.write("USER " + username + "\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            out.write("PASS " + password + "\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            out.write("STAT\r\n");
            out.flush();
            String statResponse = in.readLine();
            System.out.println("Server: " + statResponse);

            if (!statResponse.startsWith("+OK")) {
                JOptionPane.showMessageDialog(null, "Authentication failed.");
                socket.close();
                return "";
            }

            String[] statParts = statResponse.split(" ");
            int messageCount = Integer.parseInt(statParts[1]);

            for (int i = 1; i <= messageCount; i++) {
                out.write("RETR " + i + "\r\n");
                out.flush();
                String line;
                while (!(line = in.readLine()).equals(".")) {
                    emails.append(line).append("\n");
                }
                emails.append("\n");
            }

            out.write("QUIT\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            socket.close();
            JOptionPane.showMessageDialog(null, "Emails received successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to receive emails.");
        }
        return emails.toString();
    }
}
