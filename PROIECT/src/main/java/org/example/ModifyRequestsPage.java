package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyRequestsPage extends JFrame {

    private JList<String> requestsList;
    private User user;

    public ModifyRequestsPage(User user) {
        this.user = user;
        setTitle("Modify Requests");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Requests:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        updateRequestsList();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton createButton = new JButton("Create Request");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRequest();
            }
        });
        buttonPanel.add(createButton);

        JButton withdrawButton = new JButton("Withdraw Request");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdrawRequest();
            }
        });
        buttonPanel.add(withdrawButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JScrollPane scrollPane;

    private void updateRequestsList() {
        if (user instanceof Regular) {
            Regular regular = (Regular) user;
            List<String> requests = regular.requestsSent.stream()
                    .map(request -> request.description)
                    .collect(Collectors.toList());

            if (scrollPane != null) {
                remove(scrollPane);
            }

            requestsList = new JList<>(requests.toArray(new String[0]));
            scrollPane = new JScrollPane(requestsList);
            add(scrollPane, BorderLayout.CENTER);
            revalidate();
            repaint();
        } else if (user instanceof Contributor) {
            Contributor contributor = (Contributor) user;
            List<String> requests = contributor.requestsSent.stream()
                    .map(request -> request.description)
                    .collect(Collectors.toList());

            if (scrollPane != null) {
                remove(scrollPane);
            }

            requestsList = new JList<>(requests.toArray(new String[0]));
            scrollPane = new JScrollPane(requestsList);
            add(scrollPane, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void createRequest() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(400, 200));
        String[] requestTypes = {"DELETE_ACCOUNT", "MOVIE_ISSUE", "ACTOR_ISSUE", "OTHERS"};
        JComboBox<String> requestTypeComboBox = new JComboBox<>(requestTypes);
        JTextField descriptionField = new JTextField(20);

        List<String> movieTitles;
        List<String> actorNames;

        if (user instanceof Contributor) {
            Contributor contributor = (Contributor) user;
            movieTitles = IMDb.getInstance().productions.stream()
                    .filter(p -> !contributor.contributions.contains(p))
                    .map(Production::getTitle)
                    .collect(Collectors.toList());
            actorNames = IMDb.getInstance().actors.stream()
                    .filter(a -> !contributor.contributions.contains(a))
                    .map(Actor::getName)
                    .collect(Collectors.toList());
        } else {
            movieTitles = IMDb.getInstance().productions.stream()
                    .map(Production::getTitle)
                    .collect(Collectors.toList());
            actorNames = IMDb.getInstance().actors.stream()
                    .map(Actor::getName)
                    .collect(Collectors.toList());
        }

        JComboBox<String> movieComboBox = new JComboBox<>(movieTitles.toArray(new String[0]));
        JComboBox<String> actorComboBox = new JComboBox<>(actorNames.toArray(new String[0]));

        panel.add(new JLabel("Select the type of request:"));
        panel.add(requestTypeComboBox);
        panel.add(new JLabel("Enter the description of the request:"));
        panel.add(descriptionField);

        JLabel movieLabel = new JLabel("Select a movie:");
        JLabel actorLabel = new JLabel("Select an actor:");

        requestTypeComboBox.addActionListener(e -> {
            String requestType = (String) requestTypeComboBox.getSelectedItem();
            if ("MOVIE_ISSUE".equals(requestType)) {
                if (panel.isAncestorOf(actorLabel)) {
                    panel.remove(actorLabel);
                    panel.remove(actorComboBox);
                }
                if (!panel.isAncestorOf(movieLabel)) {
                    panel.add(movieLabel, 2);
                    panel.add(movieComboBox, 3);
                }
            } else if ("ACTOR_ISSUE".equals(requestType)) {
                if (panel.isAncestorOf(movieLabel)) {
                    panel.remove(movieLabel);
                    panel.remove(movieComboBox);
                }
                if (!panel.isAncestorOf(actorLabel)) {
                    panel.add(actorLabel, 2);
                    panel.add(actorComboBox, 3);
                }
            } else {
                panel.remove(movieLabel);
                panel.remove(movieComboBox);
                panel.remove(actorLabel);
                panel.remove(actorComboBox);
            }
            panel.revalidate();
            panel.repaint();
        });

        int option = JOptionPane.showConfirmDialog(this, panel, "Create Request", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String requestType = (String) requestTypeComboBox.getSelectedItem();
            if (descriptionField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description for the request!");
                return;
            } else {
                String description = descriptionField.getText();
                String movieName = (String) movieComboBox.getSelectedItem();
                String actorName = (String) actorComboBox.getSelectedItem();

                Request newRequest = new Request();
                newRequest.description = description;
                newRequest.senderName = user.username;
                newRequest.setDate(LocalDateTime.now());
                newRequest.setRequestType(RequestTypes.valueOf(requestType));

                if ("MOVIE_ISSUE".equals(requestType)) {
                    newRequest.movieTitle = movieName;
                    for (User user : IMDb.getInstance().users) {
                        if (user instanceof Staff) {
                            Staff s = (Staff) user;
                            for (Object t : s.contributions) {
                                if (t instanceof Production) {
                                    Production p = (Production) t;
                                    if (p.title.equals(movieName)) {
                                        newRequest.recipientName = s.username;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (Object t : Admin.ContributionsHolder.contributions) {
                        if (t instanceof Production) {
                            Production p = (Production) t;
                            if (p.title.equals(movieName)) {
                                newRequest.recipientName = "ADMIN";
                                break;
                            }
                        }
                    }
                } else if ("ACTOR_ISSUE".equals(requestType)) {
                    newRequest.actorName = actorName;
                    for (User user : IMDb.getInstance().users) {
                        if (user instanceof Staff) {
                            Staff s = (Staff) user;
                            for (Object t : s.contributions) {
                                if (t instanceof Actor) {
                                    Actor a = (Actor) t;
                                    if (a.name.equals(actorName)) {
                                        newRequest.recipientName = s.username;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (Object t : Admin.ContributionsHolder.contributions) {
                        if (t instanceof Actor) {
                            Actor a = (Actor) t;
                            if (a.name.equals(actorName)) {
                                newRequest.recipientName = "ADMIN";
                                break;
                            }
                        }
                    }
                } else {
                    newRequest.recipientName = "ADMIN";
                }

                if (user instanceof Regular) {
                    Regular regular = (Regular) user;
                    regular.createRequest(newRequest);
                } else if (user instanceof Contributor) {
                    Contributor contributor = (Contributor) user;
                    contributor.createRequest(newRequest);
                }

                JOptionPane.showMessageDialog(this, "Request created successfully!");
                updateRequestsList();
            }
        }
    }

    private void withdrawRequest() {
        String selectedRequest = requestsList.getSelectedValue();
        if (selectedRequest != null) {
            Request toRemove = null;
            if (user instanceof Regular) {
                Regular regular = (Regular) user;
                for (Request request : regular.requestsSent) {
                    if (request.description.equals(selectedRequest)) {
                        toRemove = request;
                        break;
                    }
                }
                if (toRemove != null) {
                    regular.removeRequest(toRemove);
                    updateRequestsList();
                }
            } else if (user instanceof Contributor) {
                Contributor contributor = (Contributor) user;
                for (Request request : contributor.requestsSent) {
                    if (request.description.equals(selectedRequest)) {
                        toRemove = request;
                        break;
                    }
                }
                if (toRemove != null) {
                    contributor.removeRequest(toRemove);
                    updateRequestsList();
                }
            }
        }
    }
}