package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchPage extends JFrame {

    private JTextField searchField;
    private JTextArea searchResultArea;

    public SearchPage() {
        setTitle("Search");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Search:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        searchField = new JTextField();
        centerPanel.add(searchField);

        searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setLineWrap(true);
        searchResultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(searchResultArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        add(searchButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void performSearch() {
        String query = searchField.getText();
        String result = "";
        boolean found = false;

        for (Actor actor : IMDb.getInstance().actors) {
            if (actor.name.equalsIgnoreCase(query)) {
                found = true;
                result += actor.displayInfo() + "\n";
            }
        }

        for (Production production : IMDb.getInstance().productions) {
            if (production.title.equalsIgnoreCase(query)) {
                found = true;
                if (production instanceof Movie) {
                    Movie movie = (Movie) production;
                    result += movie.displayInfo() + "\n";
                } else if (production instanceof Series) {
                    Series series = (Series) production;
                    result += series.displayInfo() + "\n";
                }
            }
        }

        if (!found) {
            result = "The name you entered is not in the system!";
        }

        searchResultArea.setText(result);
    }
}