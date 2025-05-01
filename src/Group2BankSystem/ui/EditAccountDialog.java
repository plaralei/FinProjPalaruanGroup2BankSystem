package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditAccountDialog extends JDialog {
    private MainFrame frame;
    private BankAccount account;
    private boolean updated = false;

    public EditAccountDialog(MainFrame frame, BankAccount account) {
        super(frame, "Edit Account", true);
        this.frame = frame;
        this.account = account;
        initializeUI();
    }

    public EditAccountDialog(MainFrame mainFrame, CreditCardAccount account) {
    }

    private void initializeUI() {
        setSize(400, 250);
        setLocationRelativeTo(frame);
        setLayout(new GridLayout(5, 2, 10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextField nameField = new JTextField(account.getAccountHolderName());
        JTextField balanceField = new JTextField(String.format("%.2f", account.getBalance()));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Bank Account",
                "Checking Account",
                "Investment Account",
                "Credit Card Account"
        });
        typeCombo.setSelectedItem(account.getAccountType());

        add(new JLabel("Account Holder Name:"));
        add(nameField);
        add(new JLabel("Balance:"));
        add(balanceField);
        add(new JLabel("Account Type:"));
        add(typeCombo);

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                updateAccount(
                        nameField.getText(),
                        Double.parseDouble(balanceField.getText()),
                        (String) typeCombo.getSelectedItem()
                );
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid balance format", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        add(saveBtn);
        add(cancelBtn);
    }

    private void updateAccount(String newName, double newBalance, String newType)
            throws InvalidAmountException {
        if (!account.getAccountType().equals(newType)) {
            try {
                account = AccountFactory.convertAccount(account, newType);
            } catch (AccountFactory.AccountConversionException e) {
                throw new RuntimeException(e);
            }
        }

        account.setAccountHolderName(newName);

        double difference = newBalance - account.getBalance();
        if (difference > 0) {
            try {
                account.deposit(difference);
            } catch (AccountClosedException e) {
                throw new RuntimeException(e);
            }
        } else if (difference < 0) {
            try {
                account.withdraw(-difference);
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            } catch (AccountClosedException e) {
                throw new RuntimeException(e);
            }
        }

        AccountManager.updateAccount(account);
        updated = true;
    }

    public boolean isUpdated() {
        return updated;
    }
}