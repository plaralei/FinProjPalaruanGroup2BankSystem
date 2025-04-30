package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CreateAccountPanel extends JPanel {
    private final MainFrame frame;

    public CreateAccountPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Create New Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField initialDepositField = new JTextField();
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{
                "Bank Account", "Checking Account", "Investment Account", "Credit Card Account"
        });

        formPanel.add(new JLabel("Account Holder Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Initial Deposit:"));
        formPanel.add(initialDepositField);
        formPanel.add(new JLabel("Account Type:"));
        formPanel.add(accountTypeCombo);

        JPanel buttonPanel = new JPanel();
        JButton createBtn = new JButton("Create Account");
        createBtn.addActionListener(e -> createAccount(
                nameField.getText(),
                initialDepositField.getText(),
                (String) accountTypeCombo.getSelectedItem()
        ));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showCard(MainFrame.MENU));

        buttonPanel.add(createBtn);
        buttonPanel.add(backBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createAccount(String name, String depositStr, String accountType) {
        try {
            double initialDeposit = Double.parseDouble(depositStr);
            BankAccount account = AccountFactory.createAccount(
                    generateAccountNumber(),
                    name,
                    initialDeposit,
                    accountType
            );
            AccountManager.addAccount(account);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\n" +
                            "Account Number: " + account.getAccountNumber() + "\n" +
                            "Account Type: " + account.getAccountType());
            frame.showCard(MainFrame.MENU);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid deposit amount", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidAmountException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateAccountNumber() {
        return "AC" + System.currentTimeMillis();
    }
}