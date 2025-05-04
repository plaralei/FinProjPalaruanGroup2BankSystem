package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A panel that provides user interface components for managing {@link InvestmentAccount}s.
 * Includes options to edit, deposit, view balance, compute interest, and close accounts.
 */
public class InvestmentAccountPanel extends JPanel implements ManageAccountPanel.Refreshable {
    private final ManageAccountPanel parent;
    private final InvestmentAccountTableModel tableModel;
    private final JTable accountTable;

    /**
     * Constructs an InvestmentAccountPanel.
     *
     * @param parent the parent {@link ManageAccountPanel} containing this panel
     */
    public InvestmentAccountPanel(ManageAccountPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        tableModel = new InvestmentAccountTableModel();
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel.add(createOperationButton("Edit Account", this::editAccount));
        buttonPanel.add(createOperationButton("Deposit", this::deposit));
        buttonPanel.add(createOperationButton("Compute Interest", this::computeInterest));
        buttonPanel.add(createOperationButton("View Balance", this::viewBalance));
        buttonPanel.add(createOperationButton("Close Account", this::closeAccount));
        buttonPanel.add(new JLabel()); // Empty cell for layout

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a JButton for the given operation.
     *
     * @param text   the button label
     * @param action the action listener to perform when clicked
     * @return the created JButton
     */
    private JButton createOperationButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    /**
     * Retrieves the currently selected {@link InvestmentAccount}.
     *
     * @return the selected InvestmentAccount, or null if none selected
     */
    private InvestmentAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first");
            return null;
        }
        return tableModel.getAccountAt(row);
    }

    /**
     * Opens a dialog to edit the selected account.
     *
     * @param e the action event
     */
    private void editAccount(ActionEvent e) {
        InvestmentAccount account = getSelectedAccount();
        if (account != null) {
            new EditAccountDialog(parent.getMainFrame(), account).setVisible(true);
            refresh();
        }
    }

    /**
     * Prompts user to enter deposit amount and deposits it into the selected account.
     *
     * @param e the action event
     */
    private void deposit(ActionEvent e) {
        InvestmentAccount account = getSelectedAccount();
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
     * Computes and displays the monthly interest for the selected account.
     *
     * @param e the action event
     */
    private void computeInterest(ActionEvent e) {
        InvestmentAccount account = getSelectedAccount();
        if (account == null) return;

        double interest = account.computeMonthlyInterest();
        JOptionPane.showMessageDialog(this,
                String.format("Monthly interest: %.2f", interest),
                "Interest Calculation", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the balance and interest details of the selected account.
     *
     * @param e the action event
     */
    private void viewBalance(ActionEvent e) {
        InvestmentAccount account = getSelectedAccount();
        if (account == null) return;

        String message;
        message = String.format("Current Balance: %.2f\n" +
                        "Interest Rate: %.2f%%\n" +
                        "Total Interest Earned: %.2f",
                new Object[]{account.getBalance(), account.getInterestRate() , account.getTotalInterestEarned()});

        JOptionPane.showMessageDialog(this, message, "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prompts user for confirmation and closes the selected account.
     *
     * @param e the action event
     */
    private void closeAccount(ActionEvent e) {
        InvestmentAccount account = getSelectedAccount();
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
     * Shows an error message in a dialog.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Refreshes the account table model.
     */
    @Override
    public void refresh() {
        tableModel.refresh();
    }
}