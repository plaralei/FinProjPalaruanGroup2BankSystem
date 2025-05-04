package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AccountStatementPanel extends JPanel {
    private final JComboBox<String> accountComboBox;
    private final JComboBox<String> timeFrameComboBox;
    private final JButton generateButton;
    private final JButton printButton;
    private final DefaultTableModel statementTableModel;
    private final JTable statementTable;
    private final JLabel balanceLabel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public AccountStatementPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        accountComboBox = new JComboBox<>();
        accountComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        populateAccountComboBox();

        timeFrameComboBox = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Yearly"});
        timeFrameComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        generateButton = new JButton("Generate Statement");
        styleButton(generateButton);
        generateButton.addActionListener(e -> generateStatement());

        printButton = new JButton("Print Statement");
        styleButton(printButton);
        printButton.addActionListener(e -> printStatement());

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBackground(Color.WHITE);
        row1.add(new JLabel("Select Account:"));
        row1.add(accountComboBox);
        row1.add(Box.createHorizontalStrut(15));
        row1.add(new JLabel("Select Time Frame:"));
        row1.add(timeFrameComboBox);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBackground(Color.WHITE);
        row2.add(generateButton);
        row2.add(printButton);

        topPanel.add(row1);
        topPanel.add(row2);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Date", "Type", "Amount", "Description"};
        statementTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 2) return Double.class;
                return String.class;
            }
        };
        statementTable = new JTable(statementTableModel);
        customizeTable(statementTable);

        JScrollPane scrollPane = new JScrollPane(statementTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(12, 46, 97), 2));
        add(scrollPane, BorderLayout.CENTER);

        balanceLabel = new JLabel("Current Balance: ₱0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(balanceLabel, BorderLayout.SOUTH);
    }

    private void populateAccountComboBox() {
        accountComboBox.removeAllItems();
        List<BankAccount> accounts = AccountManager.getAccounts();
        for (BankAccount acc : accounts) {
            accountComboBox.addItem(acc.getAccountNumber() + " - " + acc.getAccountHolderName());
        }
    }

    private void generateStatement() {
        if (accountComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedAccountInfo = (String) accountComboBox.getSelectedItem();
        String accountNumber = selectedAccountInfo.split(" - ")[0];

        BankAccount account = AccountManager.getAccountByNumber(accountNumber).orElse(null);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String timeFrame = (String) timeFrameComboBox.getSelectedItem();
        Date startDate = getStartDate(timeFrame);
        Date endDate = new Date();

        List<Transaction> transactions = TransactionManager.getTransactionsByDateRange(startDate, endDate);

        transactions.removeIf(t -> !t.getAccountNumber().equals(accountNumber));

        statementTableModel.setRowCount(0);
        for (Transaction t : transactions) {
            statementTableModel.addRow(new Object[]{
                    DATETIME_FORMAT.format(t.getDate()),
                    t.getType(),
                    t.getAmount(),
                    t.getDescription()
            });
        }
        balanceLabel.setText(String.format("Current Balance: ₱%.2f", account.getBalance()));
    }

    private Date getStartDate(String timeFrame) {
        Calendar cal = Calendar.getInstance();
        switch (timeFrame) {
            case "Monthly":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "Quarterly":
                int currentMonth = cal.get(Calendar.MONTH);
                int startMonth = currentMonth / 3 * 3;
                cal.set(Calendar.MONTH, startMonth);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "Yearly":
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void printStatement() {
        try {
            boolean complete = statementTable.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Printing Complete", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Printing Cancelled", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Printing Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(12, 46, 97));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(8, 32, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(12, 46, 97));
            }
        });
    }

    private void customizeTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}