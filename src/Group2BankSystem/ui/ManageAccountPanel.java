package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for managing different types of bank accounts in the banking system.
 * This class provides a user interface for account management with multiple views:
 * account type selection, account listing, and account-specific operations.
 */
public class ManageAccountPanel extends JPanel {
    /** Reference to the parent MainFrame. */
    private final MainFrame frame;

    /** Card layout for navigating between different panels. */
    private final CardLayout cardLayout = new CardLayout();

    /** Container for all card panels. */
    private final JPanel cardPanel = new JPanel(cardLayout);

    /** The selected account type for filtering accounts. */
    private String selectedAccountType;

    /** Table for displaying account listings. */
    private JTable accountsTable;

    /** Table model for managing account data display. */
    private DefaultTableModel accountsTableModel;

    /** The currently selected account for operations. */
    private BankAccount currentAccount;

    /** Panel for displaying task content. */
    private JPanel taskContentPanel;

    /**
     * Constructs a ManageAccountPanel with a reference to the main application frame.
     *
     * @param frame the parent MainFrame that contains this panel
     */
    public ManageAccountPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        initializeUI();
    }

    /**
     * Initializes the user interface components and layout.
     * Sets up the card layout with type selection, accounts, and tasks panels.
     */
    private void initializeUI() {
        JPanel typeSelectionPanel = createTypeSelectionPanel();
        JPanel accountsPanel = createAccountsPanel();
        JPanel tasksPanel = createTasksPanel();

        cardPanel.add(typeSelectionPanel, "TYPE_SELECTION");
        cardPanel.add(accountsPanel, "ACCOUNTS");
        cardPanel.add(tasksPanel, "TASKS");

        add(cardPanel, BorderLayout.CENTER);
        add(createBackButton(), BorderLayout.SOUTH);

        cardLayout.show(cardPanel, "TYPE_SELECTION");
    }

    /**
     * Creates the account type selection panel with different account options.
     *
     * @return a JPanel containing account type selection cards
     */
    private JPanel createTypeSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 30, 30));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[][] accounts = {
                {"Bank Account", "🏦", "Deposit/Withdraw/Transfer"},
                {"Checking Account", "🏧", "Check-enabled Transactions"},
                {"Investment Account", "📈", "Monthly Interest Earnings"},
                {"Credit Card Account", "💳", "Credit Card Management"}
        };

        for (String[] acc : accounts) {
            panel.add(createColoredAccountTypeCard(acc[0], acc[1], acc[2]));
        }
        return panel;
    }

    /**
     * Creates a styled card representing an account type option.
     *
     * @param title the name of the account type
     * @param emoji a visual icon/emoji for the account type
     * @param description the description of the account type functionality
     * @return a styled JPanel representing the account type
     */
    private JPanel createColoredAccountTypeCard(String title, String emoji, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(224, 235, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(60, 110, 200), 2));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 200), 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel emojiLabel = new JLabel(emoji, SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        emojiLabel.setForeground(new Color(40, 80, 160));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(10, 40, 100));

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(new Color(30, 30, 80));
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        descArea.setFocusable(false);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);

        card.add(emojiLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedAccountType = title;
                loadAccounts();
                cardLayout.show(cardPanel, "ACCOUNTS");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(200, 220, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(12, 46, 97), 3),
                        BorderFactory.createEmptyBorder(24, 24, 24, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(224, 235, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 120, 200), 2),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }
        });
        return card;
    }

    /**
     * Creates the accounts panel for displaying and selecting accounts.
     *
     * @return a JPanel containing the accounts table and operation buttons
     */
    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Managing Accounts");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(12, 46, 97));

        JLabel typeLabel = new JLabel();
        typeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        typeLabel.setForeground(new Color(80, 80, 80));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(typeLabel, BorderLayout.EAST);

        this.accountTypeLabel = typeLabel;

        String[] columns = {"Account Number", "Account Holder", "Balance", "Status"};
        accountsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        accountsTable = new JTable(accountsTableModel);
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountsTable.setRowHeight(30);
        accountsTable.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        accountsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(12, 46, 97), 2));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(new Color(245, 247, 250));

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> refreshAccounts());
        styleRefreshButton(refreshBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "TYPE_SELECTION"));
        styleBackButton(backBtn);

        JButton selectBtn = new JButton("Select Account");
        selectBtn.addActionListener(e -> onAccountSelected());
        stylePrimaryButton(selectBtn);

        buttonsPanel.add(refreshBtn);
        buttonsPanel.add(backBtn);
        buttonsPanel.add(selectBtn);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /** Label that displays the currently selected account type. */
    private JLabel accountTypeLabel;

    /**
     * Applies consistent styling to refresh button.
     *
     * @param btn the JButton to style
     */
    private void styleRefreshButton(JButton btn) {
        Color base = new Color(40, 167, 69);
        Color hover = new Color(33, 136, 56);
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon refreshIcon = new ImageIcon(getClass().getResource("/resources/refresh.png"));
            if (refreshIcon.getIconWidth() > 0) {
                btn.setIcon(refreshIcon);
            }
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }
        });
    }

    /**
     * Loads accounts of the selected type into the accounts table.
     * Updates the UI to display account information and handles empty results.
     */
    private void loadAccounts() {
        if (selectedAccountType != null) {
            accountsTableModel.setRowCount(0);
            accountTypeLabel.setText("Type: " + selectedAccountType);

            List<? extends BankAccount> accounts;

            switch (selectedAccountType) {
                case "Bank Account" -> accounts = AccountManager.getAccounts(BankAccount.class);
                case "Checking Account" -> accounts = AccountManager.getAccounts(CheckingAccount.class);
                case "Investment Account" -> accounts = AccountManager.getAccounts(InvestmentAccount.class);
                case "Credit Card Account" -> accounts = AccountManager.getAccounts(CreditCardAccount.class);
                default -> accounts = AccountManager.getAccounts();
            }

            for (BankAccount acc : accounts) {
                accountsTableModel.addRow(new Object[]{
                        acc.getAccountNumber(),
                        acc.getAccountHolderName(),
                        String.format("%.2f", acc.getBalance()),
                        acc.isActive() ? "Active" : "Closed"
                });
            }

            if (accounts.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No " + selectedAccountType + " accounts found.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Refreshes account data by reloading from the data source.
     * Displays a loading dialog during the refresh process.
     */
    private void refreshAccounts() {
        JDialog loadingDialog = new JDialog(frame, "Refreshing Data", false);
        JLabel loadingLabel = new JLabel("Refreshing account data...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadingDialog.add(loadingLabel);
        loadingDialog.setSize(250, 100);
        loadingDialog.setLocationRelativeTo(this);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                AccountManager.reloadAccounts();
                return null;
            }

            @Override
            protected void done() {
                loadAccounts();
                loadingDialog.dispose();
            }
        };

        loadingDialog.setVisible(true);
        worker.execute();
    }

    /**
     * Creates the tasks panel for displaying account-specific operations.
     *
     * @return a JPanel containing the tabbed pane for account operations
     */
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane taskTabs = new JTabbedPane();
        taskTabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        this.taskTabs = taskTabs;

        panel.add(taskTabs, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            currentAccount = null;
            taskTabs.removeAll();
            cardLayout.show(cardPanel, "ACCOUNTS");
        });
        styleBackButton(backBtn);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setBackground(new Color(245, 247, 250));
        backPanel.add(backBtn);
        panel.add(backPanel, BorderLayout.SOUTH);

        return panel;
    }

    /** Tabbed pane for organizing account operation tasks. */
    private JTabbedPane taskTabs;

    /**
     * Handles the selection of an account from the accounts table.
     * Creates task tabs based on the selected account type.
     */
    private void onAccountSelected() {
        int row = accountsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an account.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String accNum = (String) accountsTableModel.getValueAt(row, 0);
        Optional<BankAccount> optAccount = AccountManager.getAccountByNumber(accNum);
        if (optAccount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selected account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentAccount = optAccount.get();
        createTaskTabsForAccount(currentAccount);
        cardLayout.show(cardPanel, "TASKS");
    }

    /**
     * Creates appropriate task tabs based on the account type.
     * Different account types have different available operations.
     *
     * @param account the bank account for which to create task tabs
     */
    private void createTaskTabsForAccount(BankAccount account) {
        taskTabs.removeAll();

        if (account instanceof CheckingAccount) {
            addTaskTab("Edit Account", createEditAccountPanel(account));
            addTaskTab("Deposit", createDepositPanel(account));
            addTaskTab("Encash Check", createEncashCheckPanel((CheckingAccount) account));
            addTaskTab("Inquire Credit Balance", createInquireCreditBalancePanel(account));
            addTaskTab("Close Account", createCloseAccountPanel(account));

        } else if (account instanceof InvestmentAccount) {
            addTaskTab("Edit Account", createEditAccountPanel(account));
            addTaskTab("Deposit", createDepositPanel(account));
            addTaskTab("Compute Monthly Interest", createComputeInterestPanel((InvestmentAccount) account));
            addTaskTab("Inquire Balance", createInquireBalancePanel(account));
            addTaskTab("Close Account", createCloseAccountPanel(account));

        } else if (account instanceof CreditCardAccount) {
            addTaskTab("Edit Account", createEditAccountPanel(account));
            addTaskTab("Charge to Card", createChargeCardPanel((CreditCardAccount) account));
            addTaskTab("Pay Card", createPayCardPanel((CreditCardAccount) account));
            addTaskTab("Inquire Charges and Credit", createInquireChargesPanel((CreditCardAccount) account));
            addTaskTab("Close Account", createCloseAccountPanel(account));

        } else if (account instanceof BankAccount) {
            addTaskTab("Edit Account", createEditAccountPanel(account));
            addTaskTab("Deposit", createDepositPanel(account));
            addTaskTab("Withdrawal", createWithdrawalPanel(account));
            addTaskTab("Transfer Money", createTransferMoneyPanel(account));
            addTaskTab("Inquire Balance", createInquireBalancePanel(account));
            addTaskTab("Close Account", createCloseAccountPanel(account));

        }
    }

    /**
     * Adds a task tab to the tabbed pane.
     *
     * @param title the title of the tab
     * @param panel the panel to add as tab content
     */
    private void addTaskTab(String title, JPanel panel) {
        taskTabs.addTab(title, panel);
    }

    /**
     * Creates a panel for editing account holder information.
     *
     * @param account the account to edit
     * @return a JPanel with account editing functionality
     */
    private JPanel createEditAccountPanel(BankAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Account Holder Name:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField nameField = new JTextField(account.getAccountHolderName(), 20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            account.setAccountHolderName(newName);
            AccountManager.updateAccount(account);
            JOptionPane.showMessageDialog(panel, "Account updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(label);
        panel.add(nameField);
        panel.add(saveBtn);

        return panel;
    }

    /**
     * Creates a panel for depositing money into an account.
     *
     * @param account the account to deposit to
     * @return a JPanel with deposit functionality
     */
    private JPanel createDepositPanel(BankAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Deposit Amount:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton depositBtn = new JButton("Deposit");
        depositBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        depositBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();
                account.deposit(amt);
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, "Deposit successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter a valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(depositBtn);

        return panel;
    }

    /**
     * Creates a panel for withdrawing money from an account.
     *
     * @param account the account to withdraw from
     * @return a JPanel with withdrawal functionality
     */
    private JPanel createWithdrawalPanel(BankAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Withdrawal Amount:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        withdrawBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();
                account.withdraw(amt);
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, "Withdrawal successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter a valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(withdrawBtn);

        return panel;
    }

    /**
     * Creates a panel for transferring money between accounts.
     *
     * @param account the source account for the transfer
     * @return a JPanel with transfer functionality
     */
    private JPanel createTransferMoneyPanel(BankAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel labelAmount = new JLabel("Transfer Amount:");
        labelAmount.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel labelTarget = new JLabel("Target Account Number:");
        labelTarget.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField targetField = new JTextField(15);
        targetField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton transferBtn = new JButton("Transfer");
        transferBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        transferBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

                String targetAccNum = targetField.getText().trim();
                if (targetAccNum.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please enter target account number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Optional<BankAccount> targetOpt = AccountManager.getAccountByNumber(targetAccNum);
                if (targetOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Target account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = account.transfer(targetOpt.get(), amt);
                if (success) {
                    AccountManager.updateAccount(account);
                    AccountManager.updateAccount(targetOpt.get());
                    JOptionPane.showMessageDialog(panel, "Transfer successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "Transfer failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(labelAmount);
        panel.add(amountField);
        panel.add(labelTarget);
        panel.add(targetField);
        panel.add(transferBtn);

        return panel;
    }

    /**
     * Creates a panel for closing an account.
     *
     * @param account the account to close
     * @return a JPanel with account closing functionality
     */
    private JPanel createCloseAccountPanel(BankAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JButton closeBtn = new JButton("Close Account");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to close this account?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                account.closeAccount();
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, "Account closed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panel.add(closeBtn);
        return panel;
    }

    /**
     * Creates a panel for encashing checks on a checking account.
     *
     * @param account the checking account to encash from
     * @return a JPanel with check encashment functionality
     */
    private JPanel createEncashCheckPanel(CheckingAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Encash Check Amount:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton encashBtn = new JButton("Encash");
        encashBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        encashBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

                account.encashCheck(amt);
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, "Check encashed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(encashBtn);

        return panel;
    }

    /**
     * Creates a panel for inquiring credit balance information.
     *
     * @param account the account to inquire about
     * @return a JPanel with credit balance display
     */
    private JPanel createInquireCreditBalancePanel(BankAccount account) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String text;
        if (account instanceof CreditCardAccount cca) {
            text = String.format("Current Credit Balance:\n%.2f", cca.getBalance());
        } else {
            text = "Not a Credit Card Account.";
        }
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textArea.setEditable(false);
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a panel for computing monthly interest for investment accounts.
     *
     * @param account the investment account to compute interest for
     * @return a JPanel with interest computation functionality
     */
    private JPanel createComputeInterestPanel(InvestmentAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Compute Monthly Interest");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton computeBtn = new JButton("Compute");
        computeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        computeBtn.addActionListener(e -> {
            try {
                double interest = account.computeMonthlyInterest();
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, String.format("Interest computed: %.2f", interest), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(computeBtn);
        return panel;
    }

    /**
     * Creates a panel for charging to a credit card account.
     *
     * @param account the credit card account to charge to
     * @return a JPanel with charge functionality
     */
    private JPanel createChargeCardPanel(CreditCardAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Charge Amount:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton chargeBtn = new JButton("Charge");
        chargeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chargeBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

                account.charge(amt);
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, "Charge successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(chargeBtn);
        return panel;
    }

    /**
     * Creates a panel for making payments to a credit card account.
     *
     * @param account the credit card account to make payments to
     * @return a JPanel with payment functionality
     */
    private JPanel createPayCardPanel(CreditCardAccount account) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Payment Amount:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton payBtn = new JButton("Pay");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

                account.makePayment(amt);
                AccountManager.updateAccount(account);
                JOptionPane.showMessageDialog(panel, "Payment successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(payBtn);
        return panel;
    }

    /**
     * Creates a panel for inquiring about credit card charges and limits.
     *
     * @param account the credit card account to inquire about
     * @return a JPanel with credit charges and limits display
     */
    private JPanel createInquireChargesPanel(CreditCardAccount account) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textArea.setEditable(false);
        textArea.setText(String.format("Charges: %.2f\nCredit Limit: %.2f\nAvailable Credit: %.2f",
                account.getBalance(), account.getCreditLimit(), account.getAvailableCredit()));

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a panel for inquiring about account balance.
     *
     * @param account the account to inquire about
     * @return a JPanel with balance display
     */
    private JPanel createInquireBalancePanel(BankAccount account) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textArea.setEditable(false);
        textArea.setText(String.format("Current Balance: %.2f", account.getBalance()));

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a back navigation button for the panel.
     * The button behavior depends on the current visible card.
     *
     * @return a styled JButton for back navigation
     */
    private JButton createBackButton() {
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            Component visible = getVisibleCard();
            if ("TASKS".equals(visible.getName())) {
                cardLayout.show(cardPanel, "ACCOUNTS");
            } else if ("ACCOUNTS".equals(visible.getName())) {
                cardLayout.show(cardPanel, "TYPE_SELECTION");
            } else {
                frame.showCard(MainFrame.MAIN_MENU);
            }
        });
        styleBackButton(backBtn);
        return backBtn;
    }

    /**
     * Gets the currently visible card component.
     *
     * @return the currently visible Component or null if none is visible
     */
    private Component getVisibleCard() {
        for (Component comp : cardPanel.getComponents()) {
            if (comp.isVisible()) {
                return comp;
            }
        }
        return null;
    }

    /**
     * Applies consistent styling to primary buttons.
     *
     * @param btn the JButton to style
     */
    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(12, 46, 97));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    /**
     * Applies consistent styling to back buttons.
     * Sets colors, fonts, borders, and hover effects.
     *
     * @param btn the JButton to style
     */
    private void styleBackButton(JButton btn) {
        Color base = new Color(108, 117, 125);
        Color hover = new Color(90, 98, 105);
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }
        });
    }

    /**
     * Returns a reference to the parent MainFrame.
     *
     * @return the parent MainFrame instance
     */
    public MainFrame getMainFrame() {
        return frame;
    }

    /**
     * Interface for panels that can refresh their data.
     * Implemented by panels that need to update their display based on data changes.
     */
    public interface Refreshable {
        /**
         * Refreshes the panel's data display.
         */
        void refresh();
    }
}