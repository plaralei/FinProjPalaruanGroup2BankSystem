package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;

import javax.swing.*;
import java.awt.*;

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

    // Constructor for CreditCardAccount (delegating to the same UI)
    public EditAccountDialog(MainFrame frame, CreditCardAccount account) {
        this(frame, (BankAccount) account);
    }

    private void initializeUI() {
        setSize(450, 300);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Account Holder Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField nameField = new JTextField(account.getAccountHolderName(), 20);

        JLabel balanceLabel = new JLabel("Balance:");
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField balanceField = new JTextField(String.format("%.2f", account.getBalance()), 20);

        JLabel typeLabel = new JLabel("Account Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Bank Account",
                "Checking Account",
                "Investment Account",
                "Credit Card Account"
        });
        typeCombo.setSelectedItem(account.getAccountType());

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; add(nameLabel, gbc);
        gbc.gridx = 1; add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy++; add(balanceLabel, gbc);
        gbc.gridx = 1; add(balanceField, gbc);
        gbc.gridx = 0; gbc.gridy++; add(typeLabel, gbc);
        gbc.gridx = 1; add(typeCombo, gbc);
        gbc.gridx = 0; gbc.gridy++; add(saveBtn, gbc);
        gbc.gridx = 1; add(cancelBtn, gbc);

        // Listeners
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
    }

    private void updateAccount(String newName, double newBalance, String newType)
            throws InvalidAmountException {
        if (!account.getAccountType().equals(newType)) {
            try {
                account = AccountFactory.convertAccount(account, newType); // Replace reference
            } catch (AccountFactory.AccountConversionException e) {
                throw new RuntimeException("Account type conversion failed: " + e.getMessage());
            }
        }

        account.setAccountHolderName(newName);

        double currentBalance = account.getBalance();
        double difference = newBalance - currentBalance;

        try {
            if (difference > 0) {
                account.deposit(difference);
            } else if (difference < 0) {
                account.withdraw(-difference);
            }
        } catch (AccountClosedException | InsufficientFundsException e) {
            throw new RuntimeException(e);
        }

        AccountManager.updateAccount(account);
        updated = true;
    }

    public boolean isUpdated() {
        return updated;
    }
}
