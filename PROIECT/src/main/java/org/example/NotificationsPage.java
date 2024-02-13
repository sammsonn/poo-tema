package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationsPage extends JFrame {

    public NotificationsPage(User user) {
        setTitle("Notifications");
        setSize(950, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Notifications:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        List<String> notifications = user.notifications;

        JList<String> notificationsList = new JList<>(notifications.toArray(new String[0]));
        add(new JScrollPane(notificationsList), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}