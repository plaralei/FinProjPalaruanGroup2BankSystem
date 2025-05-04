package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A panel for managing credit card accounts in the banking system.
 * This class provides a user interface for displaying credit card accounts
 * and performing various operations such as making charges, payments,
 * and viewing account details.
 */
public class CreditCardAccountPanel extends JPanel implements ManageAccountPanel.Refreshable {
    /** Reference to parent management panel. */
    private final ManageAccountPanel parent;

    /** Table model for displaying credit card accounts. */
    private final CreditCardAccountTableModel tableModel;

    /** Table component displaying credit card accounts. */
    private final JTable accountTable;

    /**
     * Constructs a new CreditCardAccountPanel with a reference to its parent panel.
     * Initializes the UI components and sets up the table for displaying accounts.
     *
     * @param parent the parent ManageAccountPanel that contains this panel
     */
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

    /**
     * Creates a styled button with the specified text and action listener.
     *
     * @param text the text to display on the button
     * @param action the action listener to handle button clicks
     * @return a configured JButton
     */
    private JButton createOperationButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    /**
     * Retrieves the currently selected credit card account from the table.
     * Shows an error message if no account is selected.
     *
     * @return the selected CreditCardAccount, or null if no account is selected
     */
    private CreditCardAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first");
            return null;
        }
        return tableModel.getAccountAt(row);
    }

    /**
     * Opens the edit account dialog for the selected credit card account.
     *
     * @param e the action event from the edit button
     */
    private void editAccount(ActionEvent e) {
        CreditCardAccount account = getSelectedAccount();
        if (account != null) {
            new EditAccountDialog(parent.getMainFrame(), account).setVisible(true);
            refresh();
        }
    }

    /**
     * Handles charging an amount to the selected credit card account.
     * Prompts the user for a charge amount and processes the transaction.
     *
     * @param e the action event from the charge button
     */
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

    /**
     * Handles making a payment on the selected credit card account.
     * Prompts the user for a payment amount and processes the transaction.
     *
     * @param e the action event from the payment button
     */
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

    /**
     * Displays detailed information about the selected credit card account.
     * Shows current balance due, credit limit, and available credit.
     *
     * @param e the action event from the view details button
     */
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

    /**
     * Handles closing the selected credit card account.
     * Checks if the account can be closed (no outstanding balance)
     * and prompts for confirmation before closing.
     *
     * @param e the action event from the close account button
     */
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

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Refreshes the table data to reflect any changes in accounts.
     * Implements the Refreshable interface required by ManageAccountPanel.
     */
    @Override
    public void refresh() {
        tableModel.refresh();
    }
}