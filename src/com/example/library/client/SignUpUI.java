package com.example.library.client;

import com.example.library.database.MongoDBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SignUpUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private MongoDBHandler dbHandler;

    public SignUpUI() {
        dbHandler = new MongoDBHandler();

        // Khởi tạo frame
        frame = new JFrame("Đăng ký tài khoản");
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

        // Button Đăng ký
        JButton signUpButton = new JButton("Đăng ký");
        panel.add(signUpButton);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (dbHandler.addUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Tạo tài khoản thành công! Bạn có thể đăng nhập ngay.");
                    frame.dispose();
                    new LoginUI(); // Quay lại giao diện đăng nhập
                } else {
                    JOptionPane.showMessageDialog(frame, "Tài khoản đã tồn tại!");
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new SignUpUI();
    }
}