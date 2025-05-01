package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreditCardAccountPanel extends JPanel implements ManageAccountPanel.Refreshable {
    private final ManageAccountPanel parent;
    private final CreditCardAccountTableModel tableModel;
    private final JTable accountTable;

    public CreditCardAccountPanel(ManageAccountPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        tableModel = new CreditCardAccountTableModel();
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel.add(createOperationButton("Edit Account", this::editAccount));
        buttonPanel.add(createOperationButton("Charge Card", this::chargeCard));
        buttonPanel.add(createOperationButton("Make Payment", this::makePayment));
        buttonPanel.add(createOperationButton("View Details", this::viewDetails));
        buttonPanel.add(createOperationButton("Close Account", this::closeAccount));
        buttonPanel.add(new JLabel());
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createOperationButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private CreditCardAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first");
            return null;
        }
        return tableModel.getAccountAt(row);
    }

    private void editAccount(ActionEvent e) {
        CreditCardAccount account = getSelectedAccount();
        if (account != null) {
            new EditAccountDialog(parent.getMainFrame(), account).setVisible(true);
            refresh();
        }
    }

    private void chargeCard(ActionEvent e) {
        CreditCardAccount account = getSelectedAccount();
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(this, "Enter charge amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (account.chargeToCard(amount)) {
                JOptionPane.showMessageDialog(this, "Charge successful");
                refresh();
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        }
    }

    private void makePayment(ActionEvent e) {
        CreditCardAccount account = getSelectedAccount();
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(this, "Enter payment amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (account.payCard(amount)) {
                JOptionPane.showMessageDialog(this, "Payment successful");
                refresh();
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        }
    }

    private void viewDetails(ActionEvent e) {
        CreditCardAccount account = getSelectedAccount();
        if (account == null) return;

        String message = String.format(
                "Balance Due: %.2f\n" +
                        "Credit Limit: %.2f\n" +
                        "Available Credit: %.2f",
                -account.getBalance(),
                account.getCreditLimit(),
                account.getAvailableCredit()
        );

        JOptionPane.showMessageDialog(this, message, "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void closeAccount(ActionEvent e) {
        CreditCardAccount account = getSelectedAccount();
        if (account == null) return;

        if (account.getBalance() < 0) {
            showError("Cannot close account with outstanding balance");
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