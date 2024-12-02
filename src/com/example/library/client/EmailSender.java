package com.example.library.client;
// File: EmailSender.java
import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class EmailSender {
    private String smtpHost;
    private int smtpPort;
    private String username;

    public EmailSender(String smtpHost, int smtpPort, String username) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            Socket socket = new Socket(smtpHost, smtpPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read server welcome message
            System.out.println("Server: " + in.readLine());

            out.write("HELO localhost\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            out.write("MAIL FROM:<" + username + ">\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            out.write("RCPT TO:<" + to + ">\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            out.write("DATA\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            String data = "Subject: " + subject + "\r\n" +
                    "From: " + username + "\r\n" +
                    "To: " + to + "\r\n\r\n" +
                    body + "\r\n.\r\n";

            out.write(data);
            out.flush();
            System.out.println("Server: " + in.readLine());

            out.write("QUIT\r\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            socket.close();
            JOptionPane.showMessageDialog(null, "Email sent successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to send email.");
        }
    }
}
