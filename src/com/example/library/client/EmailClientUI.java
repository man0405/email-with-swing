package com.example.library.client;

import com.example.library.database.MongoDBHandler;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EmailClientUI {
    private JFrame frame;
    private JTextArea emailListArea;
    private JTextArea emailContentArea;
    private JTextField toField, subjectField;
    private MongoDBHandler dbHandler;
    private String currentUser;
    private EmailSender emailSender;
    private EmailReceiver emailReceiver;
    private String password;

    public EmailClientUI(String username,String password) {
        this.currentUser = username;
        this.password = password;
        dbHandler = new MongoDBHandler();
        emailSender = new EmailSender("localhost", 2525, username);
        emailReceiver = new EmailReceiver("localhost", 8110, username, password);

        frame = new JFrame("Email Client");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        emailListArea = new JTextArea(10, 50);
        emailListArea.setEditable(false);
        JScrollPane emailListScroll = new JScrollPane(emailListArea);
        panel.add(emailListScroll, BorderLayout.NORTH);

        emailContentArea = new JTextArea(10, 50);
        emailContentArea.setEditable(true);
        JScrollPane emailContentScroll = new JScrollPane(emailContentArea);
        panel.add(emailContentScroll, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new GridLayout(3, 2));

        JLabel toLabel = new JLabel("To: ");
        sendPanel.add(toLabel);
        toField = new JTextField();
        sendPanel.add(toField);

        JLabel subjectLabel = new JLabel("Subject: ");
        sendPanel.add(subjectLabel);
        subjectField = new JTextField();
        sendPanel.add(subjectField);

        JButton sendButton = new JButton("Send Email");
        sendPanel.add(sendButton);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String to = toField.getText();
                String subject = subjectField.getText();
                String content = emailContentArea.getText();
                if (!to.isEmpty() && !subject.isEmpty() && !content.isEmpty()) {
                    emailSender.sendEmail(to, subject, content);
                    JOptionPane.showMessageDialog(frame, "Email sent successfully!");
                    emailContentArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields!");
                }
            }
        });

        panel.add(sendPanel, BorderLayout.SOUTH);

        JButton refreshButton = new JButton("Refresh Inbox");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshEmailList();
            }
        });
        panel.add(refreshButton, BorderLayout.WEST);

        JButton receiveButton = new JButton("Receive Emails");
        receiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emails = emailReceiver.receiveEmails();
                emailListArea.setText(emails);
            }
        });
        panel.add(receiveButton, BorderLayout.EAST);

        refreshEmailList();

        frame.setVisible(true);
    }

    private void refreshEmailList() {
        List<Document> emails = dbHandler.getUserEmails(currentUser);
        emailListArea.setText("");

        for (Document email : emails) {
            String emailSummary = "From: " + email.getString("from") +
                    " | Subject: " + email.getString("subject") +
                    " | Timestamp: " + email.getLong("timestamp") + "\n";
            emailListArea.append(emailSummary);
        }
    }

    public static void main(String[] args) {
        new EmailClientUI("testUser" ,"testPassword");
    }
}