package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductionsPage extends JFrame {

    public ProductionsPage(User user) {
        setTitle("Productions");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Choose an option:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        List<String> productionNames = IMDb.getInstance().productions.stream()
                .map(production -> production.title)
                .collect(Collectors.toList());

        JList<String> productionsList = new JList<>(productionNames.toArray(new String[0]));
        add(new JScrollPane(productionsList), BorderLayout.CENTER);

        productionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedProductionName = productionsList.getSelectedValue();
                    Production selectedProduction = IMDb.getInstance().productions.stream()
                            .filter(production -> production.title.equals(selectedProductionName))
                            .findFirst()
                            .orElse(null);

                    if (selectedProduction != null) {
                        JDialog productionDetailsDialog = new JDialog(ProductionsPage.this, "Production Details", true);
                        productionDetailsDialog.setSize(300, 350);
                        productionDetailsDialog.setLayout(new BorderLayout());

                        ImageIcon logoIcon = null;

                        if (selectedProduction.title.equals("1917")) {
                            logoIcon = new ImageIcon(new ImageIcon("images/productions/1917.png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));
                        }
                        else {
                            logoIcon = new ImageIcon(new ImageIcon("images/productions/" + selectedProduction.title.toLowerCase() + ".png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));
                        }

                        JLabel logoLabel = new JLabel();
                        logoLabel.setIcon(logoIcon);

                        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        logoPanel.add(logoLabel);

                        productionDetailsDialog.add(logoPanel, BorderLayout.NORTH);

                        JTextArea productionDetailsArea = new JTextArea();
                        productionDetailsArea.setEditable(false);
                        productionDetailsArea.setLineWrap(true);
                        productionDetailsArea.setWrapStyleWord(true);
                        productionDetailsArea.setText(selectedProduction.displayInfo());

                        productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                        JButton viewTrailerButton = new JButton("View Trailer");
                        viewTrailerButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    Desktop.getDesktop().browse(new URI(selectedProduction.trailerLink));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        productionDetailsDialog.add(viewTrailerButton, BorderLayout.SOUTH);

                        productionDetailsDialog.setLocationRelativeTo(ProductionsPage.this);
                        productionDetailsDialog.setVisible(true);
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1));

        JButton viewByGenreButton = new JButton("View productions filtered by genre");
        viewByGenreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JDialog genreDialog = new JDialog(ProductionsPage.this, "Select Genre", true);
                genreDialog.setSize(200, 100);
                genreDialog.setLayout(new BorderLayout());


                Set<Genre> allGenres = IMDb.getInstance().productions.stream()
                        .flatMap(production -> production.genres.stream())
                        .collect(Collectors.toSet());

                JComboBox<Genre> genreComboBox = new JComboBox<>(allGenres.toArray(new Genre[0]));
                genreDialog.add(genreComboBox, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Genre selectedGenre = (Genre) genreComboBox.getSelectedItem();
                        List<String> filteredProductions = IMDb.getInstance().productions.stream()
                                .filter(production -> production.genres.contains(selectedGenre))
                                .map(production -> production.title)
                                .collect(Collectors.toList());

                        JFrame filteredProductionsFrame = new JFrame("Productions - " + selectedGenre);
                        filteredProductionsFrame.setSize(350, 300);
                        filteredProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        filteredProductionsFrame.setLayout(new BorderLayout());

                        JList<String> filteredProductionsList = new JList<>(filteredProductions.toArray(new String[0]));
                        filteredProductionsFrame.add(new JScrollPane(filteredProductionsList), BorderLayout.CENTER);

                        filteredProductionsList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    String selectedProductionName = filteredProductionsList.getSelectedValue();
                                    Production selectedProduction = IMDb.getInstance().productions.stream()
                                            .filter(production -> production.title.equals(selectedProductionName))
                                            .findFirst()
                                            .orElse(null);

                                    if (selectedProduction != null) {
                                        JDialog productionDetailsDialog = new JDialog(filteredProductionsFrame, "Production Details", true);
                                        productionDetailsDialog.setSize(300, 200);
                                        productionDetailsDialog.setLayout(new BorderLayout());

                                        JTextArea productionDetailsArea = new JTextArea();
                                        productionDetailsArea.setEditable(false);
                                        productionDetailsArea.setLineWrap(true);
                                        productionDetailsArea.setWrapStyleWord(true);
                                        productionDetailsArea.setText(selectedProduction.displayInfo());

                                        productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                        productionDetailsDialog.setLocationRelativeTo(filteredProductionsFrame);
                                        productionDetailsDialog.setVisible(true);
                                    }
                                }
                            }
                        });

                        filteredProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                        filteredProductionsFrame.setVisible(true);
                        filteredProductionsFrame.toFront();

                        genreDialog.dispose();
                    }
                });
                genreDialog.add(okButton, BorderLayout.SOUTH);

                genreDialog.setLocationRelativeTo(ProductionsPage.this);
                genreDialog.setVisible(true);
            }
        });
        buttonPanel.add(viewByGenreButton);

        JButton viewByRatingsButton = new JButton("View productions filtered by number of ratings");
        viewByRatingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog ratingsDialog = new JDialog(ProductionsPage.this, "Select Minimum Number of Ratings", true);
                ratingsDialog.setSize(300, 100);
                ratingsDialog.setLayout(new BorderLayout());

                JSpinner ratingsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
                ratingsDialog.add(ratingsSpinner, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int minRatings = (int) ratingsSpinner.getValue();
                        List<String> filteredProductions = IMDb.getInstance().productions.stream()
                                .filter(production -> production.ratings.size() >= minRatings)
                                .map(production -> production.title)
                                .collect(Collectors.toList());

                        JFrame filteredProductionsFrame = new JFrame("Productions - Minimum " + minRatings + " Ratings");
                        filteredProductionsFrame.setSize(400, 300);
                        filteredProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        filteredProductionsFrame.setLayout(new BorderLayout());

                        JList<String> filteredProductionsList = new JList<>(filteredProductions.toArray(new String[0]));
                        filteredProductionsFrame.add(new JScrollPane(filteredProductionsList), BorderLayout.CENTER);

                        filteredProductionsList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    String selectedProductionName = filteredProductionsList.getSelectedValue();
                                    Production selectedProduction = IMDb.getInstance().productions.stream()
                                            .filter(production -> production.title.equals(selectedProductionName))
                                            .findFirst()
                                            .orElse(null);

                                    if (selectedProduction != null) {
                                        JDialog productionDetailsDialog = new JDialog(filteredProductionsFrame, "Production Details", true);
                                        productionDetailsDialog.setSize(300, 200);
                                        productionDetailsDialog.setLayout(new BorderLayout());

                                        JTextArea productionDetailsArea = new JTextArea();
                                        productionDetailsArea.setEditable(false);
                                        productionDetailsArea.setLineWrap(true);
                                        productionDetailsArea.setWrapStyleWord(true);
                                        productionDetailsArea.setText(selectedProduction.displayInfo());

                                        productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                        productionDetailsDialog.setLocationRelativeTo(filteredProductionsFrame);
                                        productionDetailsDialog.setVisible(true);
                                    }
                                }
                            }
                        });

                        filteredProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                        filteredProductionsFrame.setVisible(true);
                        filteredProductionsFrame.toFront();

                        ratingsDialog.dispose();
                    }
                });
                ratingsDialog.add(okButton, BorderLayout.SOUTH);

                ratingsDialog.setLocationRelativeTo(ProductionsPage.this);
                ratingsDialog.setVisible(true);
            }
        });
        buttonPanel.add(viewByRatingsButton);

        JButton viewByYearButton = new JButton("View productions filtered by year");
        viewByYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog yearDialog = new JDialog(ProductionsPage.this, "Select Year", true);
                yearDialog.setSize(300, 100);
                yearDialog.setLayout(new BorderLayout());

                JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1972, 1972, 2023, 1));
                yearDialog.add(yearSpinner, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int year = (int) yearSpinner.getValue();
                        List<String> filteredProductions = IMDb.getInstance().productions.stream()
                                .filter(production -> production.year == year)
                                .map(production -> production.title)
                                .collect(Collectors.toList());

                        JFrame filteredProductionsFrame = new JFrame("Productions - " + year);
                        filteredProductionsFrame.setSize(400, 300);
                        filteredProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        filteredProductionsFrame.setLayout(new BorderLayout());

                        JList<String> filteredProductionsList = new JList<>(filteredProductions.toArray(new String[0]));
                        filteredProductionsFrame.add(new JScrollPane(filteredProductionsList), BorderLayout.CENTER);

                        filteredProductionsList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    String selectedProductionName = filteredProductionsList.getSelectedValue();
                                    Production selectedProduction = IMDb.getInstance().productions.stream()
                                            .filter(production -> production.title.equals(selectedProductionName))
                                            .findFirst()
                                            .orElse(null);

                                    if (selectedProduction != null) {
                                        JDialog productionDetailsDialog = new JDialog(filteredProductionsFrame, "Production Details", true);
                                        productionDetailsDialog.setSize(300, 200);
                                        productionDetailsDialog.setLayout(new BorderLayout());

                                        JTextArea productionDetailsArea = new JTextArea();
                                        productionDetailsArea.setEditable(false);
                                        productionDetailsArea.setLineWrap(true);
                                        productionDetailsArea.setWrapStyleWord(true);
                                        productionDetailsArea.setText(selectedProduction.displayInfo());

                                        productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                        productionDetailsDialog.setLocationRelativeTo(filteredProductionsFrame);
                                        productionDetailsDialog.setVisible(true);
                                    }
                                }
                            }
                        });

                        filteredProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                        filteredProductionsFrame.setVisible(true);
                        filteredProductionsFrame.toFront();

                        yearDialog.dispose();
                    }
                });
                yearDialog.add(okButton, BorderLayout.SOUTH);

                yearDialog.setLocationRelativeTo(ProductionsPage.this);
                yearDialog.setVisible(true);
            }
        });
        buttonPanel.add(viewByYearButton);

        JButton viewByDirectorButton = new JButton("View productions filtered by director");
        viewByDirectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog directorDialog = new JDialog(ProductionsPage.this, "Select Director", true);
                directorDialog.setSize(300, 100);
                directorDialog.setLayout(new BorderLayout());

                Set<String> allDirectors = IMDb.getInstance().productions.stream()
                        .flatMap(production -> production.directors.stream())
                        .collect(Collectors.toSet());

                JComboBox<String> directorComboBox = new JComboBox<>(allDirectors.toArray(new String[0]));
                directorDialog.add(directorComboBox, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedDirector = (String) directorComboBox.getSelectedItem();
                        List<String> filteredProductions = IMDb.getInstance().productions.stream()
                                .filter(production -> production.directors.contains(selectedDirector))
                                .map(production -> production.title)
                                .collect(Collectors.toList());

                        JFrame filteredProductionsFrame = new JFrame("Productions - " + selectedDirector);
                        filteredProductionsFrame.setSize(400, 300);
                        filteredProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        filteredProductionsFrame.setLayout(new BorderLayout());

                        JList<String> filteredProductionsList = new JList<>(filteredProductions.toArray(new String[0]));
                        filteredProductionsFrame.add(new JScrollPane(filteredProductionsList), BorderLayout.CENTER);

                        filteredProductionsList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    String selectedProductionName = filteredProductionsList.getSelectedValue();
                                    Production selectedProduction = IMDb.getInstance().productions.stream()
                                            .filter(production -> production.title.equals(selectedProductionName))
                                            .findFirst()
                                            .orElse(null);

                                    if (selectedProduction != null) {
                                        JDialog productionDetailsDialog = new JDialog(filteredProductionsFrame, "Production Details", true);
                                        productionDetailsDialog.setSize(300, 200);
                                        productionDetailsDialog.setLayout(new BorderLayout());

                                        JTextArea productionDetailsArea = new JTextArea();
                                        productionDetailsArea.setEditable(false);
                                        productionDetailsArea.setLineWrap(true);
                                        productionDetailsArea.setWrapStyleWord(true);
                                        productionDetailsArea.setText(selectedProduction.displayInfo());

                                        productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                        productionDetailsDialog.setLocationRelativeTo(ProductionsPage.this);
                                        productionDetailsDialog.setVisible(true);
                                    }
                                }
                            }
                        });

                        filteredProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                        filteredProductionsFrame.setVisible(true);
                        filteredProductionsFrame.toFront();

                        directorDialog.dispose();
                    }
                });
                directorDialog.add(okButton, BorderLayout.SOUTH);

                directorDialog.setLocationRelativeTo(ProductionsPage.this);
                directorDialog.setVisible(true);
            }
        });
        buttonPanel.add(viewByDirectorButton);

        JButton viewByActorButton = new JButton("View productions filtered by actor");
        viewByActorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog actorDialog = new JDialog(ProductionsPage.this, "Select Actor", true);
                actorDialog.setSize(300, 100);
                actorDialog.setLayout(new BorderLayout());

                Set<String> allActors = IMDb.getInstance().productions.stream()
                        .flatMap(production -> production.actors.stream())
                        .collect(Collectors.toSet());

                JComboBox<String> actorComboBox = new JComboBox<>(allActors.toArray(new String[0]));
                actorDialog.add(actorComboBox, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedActor = (String) actorComboBox.getSelectedItem();
                        List<String> filteredProductions = IMDb.getInstance().productions.stream()
                                .filter(production -> production.actors.contains(selectedActor))
                                .map(production -> production.title)
                                .collect(Collectors.toList());

                        JFrame filteredProductionsFrame = new JFrame("Productions - " + selectedActor);
                        filteredProductionsFrame.setSize(400, 300);
                        filteredProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        filteredProductionsFrame.setLayout(new BorderLayout());

                        JList<String> filteredProductionsList = new JList<>(filteredProductions.toArray(new String[0]));
                        filteredProductionsFrame.add(new JScrollPane(filteredProductionsList), BorderLayout.CENTER);

                        filteredProductionsList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    String selectedProductionName = filteredProductionsList.getSelectedValue();
                                    Production selectedProduction = IMDb.getInstance().productions.stream()
                                            .filter(production -> production.title.equals(selectedProductionName))
                                            .findFirst()
                                            .orElse(null);

                                    if (selectedProduction != null) {
                                        JDialog productionDetailsDialog = new JDialog(filteredProductionsFrame, "Production Details", true);
                                        productionDetailsDialog.setSize(300, 200);
                                        productionDetailsDialog.setLayout(new BorderLayout());

                                        JTextArea productionDetailsArea = new JTextArea();
                                        productionDetailsArea.setEditable(false);
                                        productionDetailsArea.setLineWrap(true);
                                        productionDetailsArea.setWrapStyleWord(true);
                                        productionDetailsArea.setText(selectedProduction.displayInfo());

                                        productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                        productionDetailsDialog.setLocationRelativeTo(ProductionsPage.this);
                                        productionDetailsDialog.setVisible(true);
                                    }
                                }
                            }
                        });

                        filteredProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                        filteredProductionsFrame.setVisible(true);
                        filteredProductionsFrame.toFront();

                        actorDialog.dispose();
                    }
                });
                actorDialog.add(okButton, BorderLayout.SOUTH);

                actorDialog.setLocationRelativeTo(ProductionsPage.this);
                actorDialog.setVisible(true);
            }
        });
        buttonPanel.add(viewByActorButton);

        JButton viewByRankingButton = new JButton("View productions sorted by ranking");
        viewByRankingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> sortedProductions = IMDb.getInstance().productions.stream()
                        .sorted((production1, production2) -> Double.compare(production2.ranking, production1.ranking))
                        .map(production -> production.title)
                        .collect(Collectors.toList());

                JFrame sortedProductionsFrame = new JFrame("Productions - Sorted by Ranking");
                sortedProductionsFrame.setSize(400, 300);
                sortedProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                sortedProductionsFrame.setLayout(new BorderLayout());

                JList<String> sortedProductionsList = new JList<>(sortedProductions.toArray(new String[0]));
                sortedProductionsFrame.add(new JScrollPane(sortedProductionsList), BorderLayout.CENTER);

                sortedProductionsList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            String selectedProductionName = sortedProductionsList.getSelectedValue();
                            Production selectedProduction = IMDb.getInstance().productions.stream()
                                    .filter(production -> production.title.equals(selectedProductionName))
                                    .findFirst()
                                    .orElse(null);

                            if (selectedProduction != null) {
                                JDialog productionDetailsDialog = new JDialog(sortedProductionsFrame, "Production Details", true);
                                productionDetailsDialog.setSize(300, 200);
                                productionDetailsDialog.setLayout(new BorderLayout());

                                JTextArea productionDetailsArea = new JTextArea();
                                productionDetailsArea.setEditable(false);
                                productionDetailsArea.setLineWrap(true);
                                productionDetailsArea.setWrapStyleWord(true);
                                productionDetailsArea.setText(selectedProduction.displayInfo());

                                productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                productionDetailsDialog.setLocationRelativeTo(ProductionsPage.this);
                                productionDetailsDialog.setVisible(true);
                            }
                        }
                    }
                });

                sortedProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                sortedProductionsFrame.setVisible(true);
                sortedProductionsFrame.toFront();
            }
        });
        buttonPanel.add(viewByRankingButton);

        JButton viewByTitleButton = new JButton("View productions sorted by title");
        viewByTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> sortedProductions = IMDb.getInstance().productions.stream()
                        .sorted((production1, production2) -> production1.title.compareTo(production2.title))
                        .map(production -> production.title)
                        .collect(Collectors.toList());

                JFrame sortedProductionsFrame = new JFrame("Productions - Sorted by Title");
                sortedProductionsFrame.setSize(400, 300);
                sortedProductionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                sortedProductionsFrame.setLayout(new BorderLayout());

                JList<String> sortedProductionsList = new JList<>(sortedProductions.toArray(new String[0]));
                sortedProductionsFrame.add(new JScrollPane(sortedProductionsList), BorderLayout.CENTER);

                sortedProductionsList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            String selectedProductionName = sortedProductionsList.getSelectedValue();
                            Production selectedProduction = IMDb.getInstance().productions.stream()
                                    .filter(production -> production.title.equals(selectedProductionName))
                                    .findFirst()
                                    .orElse(null);

                            if (selectedProduction != null) {
                                JDialog productionDetailsDialog = new JDialog(sortedProductionsFrame, "Production Details", true);
                                productionDetailsDialog.setSize(300, 200);
                                productionDetailsDialog.setLayout(new BorderLayout());

                                JTextArea productionDetailsArea = new JTextArea();
                                productionDetailsArea.setEditable(false);
                                productionDetailsArea.setLineWrap(true);
                                productionDetailsArea.setWrapStyleWord(true);
                                productionDetailsArea.setText(selectedProduction.displayInfo());

                                productionDetailsDialog.add(new JScrollPane(productionDetailsArea), BorderLayout.CENTER);

                                productionDetailsDialog.setLocationRelativeTo(ProductionsPage.this);
                                productionDetailsDialog.setVisible(true);
                            }
                        }
                    }
                });

                sortedProductionsFrame.setLocationRelativeTo(ProductionsPage.this);
                sortedProductionsFrame.setVisible(true);
                sortedProductionsFrame.toFront();
            }
        });
        buttonPanel.add(viewByTitleButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}