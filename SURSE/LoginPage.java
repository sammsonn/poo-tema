package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame implements ActionListener {

    private Container container;
    private JLabel userLabel;
    private JTextField userTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginPage() throws ClassNotFoundException {
        setTitle("Login Form");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        container = getContentPane();
        container.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(20, 20, 20, 20);
        constraints.anchor = GridBagConstraints.LINE_START;

        userLabel = new JLabel("Email");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        constraints.gridx = 0;
        constraints.gridy = 0;
        container.add(userLabel, constraints);

        userTextField = new JTextField();
        userTextField.setFont(new Font("Arial", Font.PLAIN, 25));
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.ipadx = 200;
        container.add(userTextField, constraints);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.ipadx = 0;
        container.add(passwordLabel, constraints);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 25));
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipadx = 200;
        container.add(passwordField, constraints);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 25));
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.ipadx = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        container.add(loginButton, constraints);
        loginButton.addActionListener(this);

        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.LINE_START;
        container.add(messageLabel, constraints);

        setLocationRelativeTo(null);
        setVisible(true);
        toFront();
    }

    public void actionPerformed(ActionEvent e) {
        String email = userTextField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (validateLogin(email, password) != null) {
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Login successful");
            new MainPage(validateLogin(email, password));
            dispose();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid username or password");
        }
    }

    private User validateLogin(String email, String password) {
        User user = null;
        for (User user1 : IMDb.getInstance().users) {
            if (user1.info.getCredentials().getEmail().equals(email) && user1.info.getCredentials().getPassword().equals(password)) {
                user = user1;
            }
        }
        return user;
    }
}
