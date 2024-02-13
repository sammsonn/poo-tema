package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ModifyUsersPage extends JFrame {

    public ModifyUsersPage(User user) {
        setTitle("Modify Users");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddUserPage(user);
            }
        });
        add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeleteUserPage(user);
            }
        });
        add(deleteButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class AddUserPage extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField countryField;
    private JTextField genderField;
    private JTextField birthDateField;
    private JRadioButton regularButton;
    private JRadioButton contributorButton;
    private JRadioButton adminButton;

    public AddUserPage(User user) {
        setTitle("Add User");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(11, 2));

        firstNameField = new JTextField();
        add(new JLabel("First Name:"));
        add(firstNameField);

        lastNameField = new JTextField();
        add(new JLabel("Last Name:"));
        add(lastNameField);

        emailField = new JTextField();
        add(new JLabel("Email:"));
        add(emailField);

        countryField = new JTextField();
        add(new JLabel("Country:"));
        add(countryField);

        genderField = new JTextField();
        add(new JLabel("Gender:"));
        add(genderField);

        birthDateField = new JTextField();
        add(new JLabel("Birth Date (YYYY-MM-DD):"));
        add(birthDateField);

        regularButton = new JRadioButton("Regular");
        contributorButton = new JRadioButton("Contributor");
        adminButton = new JRadioButton("Admin");

        ButtonGroup group = new ButtonGroup();
        group.add(regularButton);
        group.add(contributorButton);
        group.add(adminButton);

        add(new JLabel("User Type:"));
        JPanel panel = new JPanel();
        panel.add(regularButton);
        panel.add(contributorButton);
        panel.add(adminButton);
        add(panel);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String email = emailField.getText();
                String country = countryField.getText();
                String gender = genderField.getText();
                int age = 0;
                LocalDateTime birthday = null;
                if (!birthDateField.getText().isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        LocalDate date = LocalDate.parse(birthDateField.getText(), formatter);
                        birthday = date.atStartOfDay();
                        age = LocalDate.now().getYear() - date.getYear();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Birth date must be in the format YYYY-MM-DD!");
                        return;
                    }
                }

                if (firstName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "First Name is mandatory!");
                    return;
                }

                if (lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Last Name is mandatory!");
                    return;
                }

                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Email is mandatory!");
                    return;
                }

                if (country.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Country is mandatory!");
                    return;
                }

                if (gender.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Gender is mandatory!");
                    return;
                }

                if (birthday == null) {
                    JOptionPane.showMessageDialog(null, "Birth date is mandatory!");
                    return;
                }

                if (!regularButton.isSelected() && !contributorButton.isSelected() && !adminButton.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Please select a user type!");
                    return;
                }


                String username = IMDb.getInstance().generateUsername(firstName, lastName);
                String password = IMDb.getInstance().generatePassword();


                User.Information info = null;
                try {
                    info = new User.Information.InformationBuilder(email, password)
                            .name(firstName + " " + lastName)
                            .country(country)
                            .age(age)
                            .gender(gender)
                            .birthday(birthday)
                            .build();
                } catch (InformationIncompleteException ex) {
                    ex.printStackTrace();
                }


                User newUser = null;
                if (regularButton.isSelected()) {
                    newUser = UserFactory.factory(AccountType.Regular);
                    newUser.accountType = AccountType.Regular;
                    newUser.experience = 0;
                } else if (contributorButton.isSelected()) {
                    newUser = UserFactory.factory(AccountType.Contributor);
                    newUser.accountType = AccountType.Contributor;
                    newUser.experience = 0;
                } else if (adminButton.isSelected()) {
                    newUser = UserFactory.factory(AccountType.Admin);
                    newUser.accountType = AccountType.Admin;
                    newUser.experience = 999;
                }


                if (newUser != null) {
                    newUser.info = info;
                    newUser.username = username;


                    Admin a = (Admin) user;
                    a.addUser(newUser);


                    JOptionPane.showMessageDialog(null, "User " + username + " created successfully with password: " + password + ".");

                    dispose();
                }
            }
        });
        add(addButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class DeleteUserPage extends JFrame {
    private JList<String> userList;

    public DeleteUserPage(User user) {
        Admin a = (Admin) user;
        setTitle("Delete User");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        final String[][] usersArray = {IMDb.getInstance().users.stream()
                .filter(u -> !u.equals(user))
                .map(User::getUsername)
                .toArray(String[]::new)};
        userList = new JList<String>(usersArray[0]);
        add(new JScrollPane(userList), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUsername = userList.getSelectedValue();
                if (selectedUsername != null) {
                    User selectedUser = IMDb.getInstance().users.stream()
                            .filter(u -> u.getUsername().equals(selectedUsername))
                            .findFirst()
                            .orElse(null);
                    if (selectedUser != null) {
                        a.removeUser(selectedUser);
                        JOptionPane.showMessageDialog(null, "User deleted successfully!");
                        usersArray[0] = IMDb.getInstance().users.stream()
                                .filter(u -> !u.equals(user))
                                .map(User::getUsername)
                                .toArray(String[]::new);
                        userList.setListData(usersArray[0]);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a user to delete!");
                }
            }
        });
        add(deleteButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
