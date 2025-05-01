package Group2BankSystem.ui;

import java.util.Optional;
import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankAccountPanel extends JPanel implements ManageAccountPanel.Refreshable {
    private final ManageAccountPanel parent;
    private final JTable accountTable;
    private final BankAccountTableModel tableModel;

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

    private JButton createOperationButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(action);
        return button;
    }

    private BankAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first");
            return null;
        }
        return tableModel.getAccountAt(row);
    }

    private void editAccount(ActionEvent e) {
        BankAccount account = getSelectedAccount();
        if (account != null) {
            new EditAccountDialog(parent.getMainFrame(), account).setVisible(true);
            refresh();
        }
    }

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

    private void viewBalance(ActionEvent e) {
        BankAccount account = getSelectedAccount();
        if (account == null) return;

        JOptionPane.showMessageDialog(this,
                String.format("Account Balance: %.2f", account.getBalance()),
                "Account Balance", JOptionPane.INFORMATION_MESSAGE);
    }

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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refresh() {
        tableModel.refresh();
    }
}