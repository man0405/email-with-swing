package com.example.library.client;

import com.example.library.database.MongoDBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private MongoDBHandler dbHandler;

    public LoginUI() {
        dbHandler = new MongoDBHandler();

        // Khởi tạo frame
        frame = new JFrame("Đăng nhập");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Panel chính
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new GridLayout(3, 2));

        // Tên đăng nhập
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        panel.add(usernameLabel);
        usernameField = new JTextField();
        panel.add(usernameField);

        // Mật khẩu
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Button Đăng nhập
        JButton loginButton = new JButton("Đăng nhập");
        panel.add(loginButton);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (dbHandler.authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Đăng nhập thành công!");
                    frame.dispose();
                    // Mở giao diện gửi/nhận email (EmailClientUI)
                    new EmailClientUI(username, password);
                } else {
                    JOptionPane.showMessageDialog(frame, "Tên đăng nhập hoặc mật khẩu không đúng!");
                }
            }
        });

        // Button Đăng ký
        JButton signUpButton = new JButton("Đăng ký tài khoản mới");
        panel.add(signUpButton);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new SignUpUI(); // Mở giao diện đăng ký
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}