package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class ActorsPage extends JFrame {
    private JScrollPane scrollPane;

    public ActorsPage() {
        setTitle("Actors");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Actors:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JComboBox<String> sortOptions = new JComboBox<>(new String[]{"Unsorted", "Sorted by name"});
        sortOptions.addActionListener(e -> updateActorsList((String) sortOptions.getSelectedItem()));
        add(sortOptions, BorderLayout.SOUTH);

        updateActorsList((String) sortOptions.getSelectedItem());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateActorsList(String sortOption) {
        List<String> actorNames;
        if ("Sorted by name".equals(sortOption)) {
            actorNames = IMDb.getInstance().actors.stream()
                    .map(actor -> actor.name)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            actorNames = IMDb.getInstance().actors.stream()
                    .map(actor -> actor.name)
                    .collect(Collectors.toList());
        }

        JList<String> actorsList = new JList<>(actorNames.toArray(new String[0]));
        actorsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedActorName = actorsList.getSelectedValue();
                    Actor selectedActor = IMDb.getInstance().actors.stream()
                            .filter(actor -> actor.name.equals(selectedActorName))
                            .findFirst()
                            .orElse(null);

                    if (selectedActor != null) {
                        JDialog actorDetailsDialog = new JDialog(ActorsPage.this, "Actor Details", true);
                        actorDetailsDialog.setSize(300, 200);
                        actorDetailsDialog.setLayout(new BorderLayout());

                        ImageIcon logoIcon = new ImageIcon(new ImageIcon("images/actors/" + selectedActor.name.toLowerCase() + ".png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));

                        JLabel logoLabel = new JLabel();
                        logoLabel.setIcon(logoIcon);

                        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        logoPanel.add(logoLabel);

                        actorDetailsDialog.add(logoPanel, BorderLayout.NORTH);

                        JTextArea actorDetailsArea = new JTextArea();
                        actorDetailsArea.setEditable(false);
                        actorDetailsArea.setLineWrap(true);
                        actorDetailsArea.setWrapStyleWord(true);
                        actorDetailsArea.setText(selectedActor.displayInfo());

                        actorDetailsDialog.add(new JScrollPane(actorDetailsArea), BorderLayout.CENTER);

                        actorDetailsDialog.setLocationRelativeTo(ActorsPage.this);
                        actorDetailsDialog.setVisible(true);
                    }
                }
            }
        });
        if (scrollPane != null) {
            remove(scrollPane);
        }
        scrollPane = new JScrollPane(actorsList);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}