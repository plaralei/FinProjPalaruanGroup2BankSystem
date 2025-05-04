package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CheckingAccountPanel extends JPanel implements ManageAccountPanel.Refreshable {
    private final ManageAccountPanel parent;
    private final CheckingAccountTableModel tableModel;
    private final JTable accountTable;

    public CheckingAccountPanel(ManageAccountPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        tableModel = new CheckingAccountTableModel();
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel.add(createOperationButton("Edit Account", this::editAccount));
        buttonPanel.add(createOperationButton("Deposit", this::deposit));
        buttonPanel.add(createOperationButton("Encash Check", this::encashCheck));
        buttonPanel.add(createOperationButton("View Balance", this::viewBalance));
        buttonPanel.add(createOperationButton("Close Account", this::closeAccount));
        buttonPanel.add(new JLabel());

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createOperationButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private CheckingAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first");
            return null;
        }
        return tableModel.getAccountAt(row);
    }

    private void editAccount(ActionEvent e) {
        CheckingAccount account = getSelectedAccount();
        if (account != null) {
            new EditAccountDialog(parent.getMainFrame(), account).setVisible(true);
            refresh();
        }
    }

    private void deposit(ActionEvent e) {
        CheckingAccount account = getSelectedAccount();
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (account.deposit(amount)) {
                JOptionPane.showMessageDialog(this, "Deposit successful");
                refresh();
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        } catch (InvalidAmountException | AccountClosedException ex) {
            showError(ex.getMessage());
        }
    }

    private void encashCheck(ActionEvent e) {
        CheckingAccount account = getSelectedAccount();
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(this, "Enter check amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (account.encashCheck(amount)) {
                JOptionPane.showMessageDialog(this, "Check encashed successfully");
                refresh();
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        } catch (InvalidAmountException | InsufficientFundsException | AccountClosedException ex) {
            showError(ex.getMessage());
        }
    }

    private void viewBalance(ActionEvent e) {
        CheckingAccount account = getSelectedAccount();
        if (account == null) return;

        String message = String.format(
                "Current Balance: %.2f\n" +
                        "Overdraft Limit: %.2f\n" +
                        "Available Balance: %.2f",
                account.getBalance(),
                account.getOverdraftLimit(),
                account.getBalance()
        );

        JOptionPane.showMessageDialog(this, message, "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void closeAccount(ActionEvent e) {
        CheckingAccount account = getSelectedAccount();
        if (account == null) return;

        System.out.println("Account balance before closing: " + account.getBalance()); // Debug line

        if (account.getBalance() > 0) {
            JOptionPane.showMessageDialog(this,
                    "You must withdraw the remaining balance before closing the account.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to close this account?",
                "Confirm Close", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            account.closeAccount();
            JOptionPane.showMessageDialog(this, "Account closed successfully");
            refresh();
        }
    }


    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void refresh() {
        tableModel.refresh();
    }
}