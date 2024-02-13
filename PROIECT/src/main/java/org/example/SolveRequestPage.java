package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SolveRequestPage extends JFrame {

    private JList<String> requestsList;
    private User user;

    public SolveRequestPage(User user, MenuPage menuPage) {
        this.user = user;
        setTitle("Solve Request");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        updateRequestsList(user);

        JButton solveButton = new JButton("Solve Request");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveRequest(menuPage);
            }
        });
        add(solveButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private DefaultListModel<String> listModel;

    private JScrollPane scrollPane;

    private void updateRequestsList(User user) {
        if (user instanceof Contributor) {
            Staff staff = (Staff) user;
            listModel = new DefaultListModel<>();
            for (Object request : staff.requests) {
                Request request1 = (Request) request;
                listModel.addElement(request1.description);
            }
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            listModel = new DefaultListModel<>();
            for (Object request : admin.requests) {
                Request request1 = (Request) request;
                listModel.addElement(request1.description);
            }
            for (Object request : Admin.RequestsHolder.requests) {
                Request request1 = (Request) request;
                listModel.addElement(request1.description);
            }
        }

        requestsList = new JList<>(listModel);

        if (scrollPane != null) {
            remove(scrollPane);
        }

        scrollPane = new JScrollPane(requestsList);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void solveRequest(MenuPage menuPage) {
        String selectedRequest = requestsList.getSelectedValue();
        if (selectedRequest != null) {
            IMDb imdb = IMDb.getInstance();
            Request requestToRemove = null;
            for (Request request : imdb.requests) {
                if (request.description.equals(selectedRequest)) {
                    requestToRemove = request;
                    break;
                }
            }
            if (requestToRemove != null) {
                requestToRemove.solved = true;
                User sender = null;
                for (User u : IMDb.getInstance().users) {
                    if (u.username.equals(requestToRemove.senderName)) {
                        sender = u;
                        break;
                    }
                }
                int option = JOptionPane.showOptionDialog(this, "Do you want to accept or reject the request?", "Choose an option",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Accept", "Reject"}, null);
                if (option == JOptionPane.YES_OPTION) {
                    requestToRemove.accepted = true;
                    if (requestToRemove.getRequestType().equals(RequestTypes.DELETE_ACCOUNT)) {
                        Admin a = (Admin) user;
                        a.removeUser(sender);
                    } else if (sender instanceof Regular) {
                        Regular r = (Regular) sender;
                        if (!requestToRemove.getRequestType().equals(RequestTypes.OTHERS)) {
                            Context context = new Context(new AcceptedRequestExperienceStrategy());
                            r.updateExperience(context.executeStrategy());
                        }
                        r.removeRequest(requestToRemove);
                    } else if (sender instanceof Contributor) {
                        Contributor c = (Contributor) sender;
                        if (!requestToRemove.getRequestType().equals(RequestTypes.OTHERS)) {
                            Context context = new Context(new AcceptedRequestExperienceStrategy());
                            c.updateExperience(context.executeStrategy());
                        }
                        c.removeRequest(requestToRemove);
                    }
                    requestToRemove.notifyObservers();
                    listModel.removeElement(selectedRequest);
                    JOptionPane.showMessageDialog(this, "Request accepted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (option == JOptionPane.NO_OPTION) {
                    if (sender instanceof Regular) {
                        Regular r = (Regular) sender;
                        r.removeRequest(requestToRemove);
                    } else if (sender instanceof Contributor) {
                        Contributor c = (Contributor) sender;
                        c.removeRequest(requestToRemove);
                    }
                    requestToRemove.notifyObservers();
                    listModel.removeElement(selectedRequest);
                    JOptionPane.showMessageDialog(this, "Request rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                updateRequestsList(user);
            }
        }
    }
}