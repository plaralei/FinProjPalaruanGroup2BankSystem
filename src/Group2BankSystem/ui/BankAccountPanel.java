package Group2BankSystem.ui;

import java.util.Optional;
import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The {@code BankAccountPanel} class provides a user interface panel for managing bank accounts.
 * <p>
 * It displays a table of all accounts and provides operations such as:
 * edit, deposit, withdraw, transfer, view balance, and close account.
 * </p>
 * This panel is typically used within the {@code ManageAccountPanel}.
 *
 * @author
 */
public class BankAccountPanel extends JPanel implements ManageAccountPanel.Refreshable {
    private final ManageAccountPanel parent;
    private final JTable accountTable;
    private final BankAccountTableModel tableModel;

    /**
     * Constructs a {@code BankAccountPanel} with a table of accounts and action buttons.
     *
     * @param parent the parent {@code ManageAccountPanel} that owns this panel
     */
    public BankAccountPanel(ManageAccountPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        tableModel = new BankAccountTableModel();
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setRowHeight(30);
        accountTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel.add(createOperationButton("Edit Account", this::editAccount));
        buttonPanel.add(createOperationButton("Deposit", this::deposit));
        buttonPanel.add(createOperationButton("Withdraw", this::withdraw));
        buttonPanel.add(createOperationButton("Transfer", this::transfer));
        buttonPanel.add(createOperationButton("View Balance", this::viewBalance));
        buttonPanel.add(createOperationButton("Close Account", this::closeAccount));

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a button with the given label and action.
     *
     * @param text   the text to display on the button
     * @param action the action to perform when the button is clicked
     * @return the created {@code JButton}
     */
    private JButton createOperationButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(action);
        return button;
    }

    /**
     * Gets the currently selected bank account in the table.
     *
     * @return the selected {@code BankAccount}, or {@code null} if none is selected
     */
    private BankAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first");
            return null;
        }
        return tableModel.getAccountAt(row);
    }

    /**
     * Opens the edit dialog for the selected account.
     *
     * @param e the action event triggered by clicking the "Edit Account" button
     */
    private void editAccount(ActionEvent e) {
        BankAccount account = getSelectedAccount();
        if (account != null) {
            new EditAccountDialog(parent.getMainFrame(), account).setVisible(true);
            refresh();
        }
    }

    /**
     * Prompts the user to enter a deposit amount and deposits it into the selected account.
     *
     * @param e the action event triggered by clicking the "Deposit" button
     */
    private void deposit(ActionEvent e) {
        BankAccount account = getSelectedAccount();
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

    /**
     * Prompts the user to enter a withdrawal amount and withdraws it from the selected account.
     *
     * @param e the action event triggered by clicking the "Withdraw" button
     */
    private void withdraw(ActionEvent e) {
        BankAccount account = getSelectedAccount();
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (account.withdraw(amount)) {
                JOptionPane.showMessageDialog(this, "Withdrawal successful");
                refresh();
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        } catch (InvalidAmountException | InsufficientFundsException | AccountClosedException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Prompts the user for a recipient account number and amount, then transfers funds from the selected account.
     *
     * @param e the action event triggered by clicking the "Transfer" button
     */
    private void transfer(ActionEvent e) {
        BankAccount fromAccount = getSelectedAccount();
        if (fromAccount == null) return;

        String toAccountNum = JOptionPane.showInputDialog(this, "Enter recipient account number:");
        Optional<BankAccount> toAccount = AccountManager.getAccountByNumber(toAccountNum);

        if (toAccount.isEmpty()) {
            showError("Recipient account not found");
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter transfer amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (fromAccount.transfer(toAccount.get(), amount)) {
                JOptionPane.showMessageDialog(this, "Transfer successful");
                refresh();
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        }
    }

    /**
     * Displays the balance of the selected account in a dialog.
     *
     * @param e the action event triggered by clicking the "View Balance" button
     */
    private void viewBalance(ActionEvent e) {
        BankAccount account = getSelectedAccount();
        if (account == null) return;

        JOptionPane.showMessageDialog(this,
                String.format("Account Balance: %.2f", account.getBalance()),
                "Account Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Closes the selected account after confirming with the user.
     *
     * @param e the action event triggered by clicking the "Close Account" button
     */
    private void closeAccount(ActionEvent e) {
        BankAccount account = getSelectedAccount();
        if (account == null) return;

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
     * Displays an error dialog with the provided message.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Refreshes the account table to reflect any updates or changes.
     */
    public void refresh() {
        tableModel.refresh();
    }
}
