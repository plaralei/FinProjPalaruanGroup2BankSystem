package Group2BankSystem.ui;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Banking System Main Menu");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(label, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton createBtn = new JButton("Create Account");
        createBtn.addActionListener(e -> frame.showCard(MainFrame.CREATE));
        gbc.gridy++;
        add(createBtn, gbc);

        JButton manageBtn = new JButton("Manage Accounts");
        manageBtn.addActionListener(e -> frame.showCard(MainFrame.MANAGE));
        gbc.gridy++;
        add(manageBtn, gbc);

        JButton searchBtn = new JButton("Search Accounts");
        searchBtn.addActionListener(e -> frame.showCard(MainFrame.SEARCH));
        gbc.gridy++;
        add(searchBtn, gbc);

        JButton reportsBtn = new JButton("Generate Reports");
        reportsBtn.addActionListener(e -> frame.showCard(MainFrame.REPORTS));
        gbc.gridy++;
        add(reportsBtn, gbc);

        JButton statementsBtn = new JButton("Account Statements");
        statementsBtn.addActionListener(e -> frame.showCard(MainFrame.STATEMENTS));
        gbc.gridy++;
        add(statementsBtn, gbc);

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy++;
        add(exitBtn, gbc);
    }
}