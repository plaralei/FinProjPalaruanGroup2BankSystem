package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StatementPanel extends JPanel {
    private final MainFrame frame;
    private final JTable statementTable;
    private final DefaultTableModel tableModel;
    private final JFormattedTextField startDateField;
    private final JFormattedTextField endDateField;
    private final JTextField accountField;

    public StatementPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        accountField = new JTextField();
        startDateField = new JFormattedTextField(dateFormat);
        endDateField = new JFormattedTextField(dateFormat);
        startDateField.setValue(new Date());
        endDateField.setValue(new Date());

        JButton generateBtn = new JButton("Generate Statement");
        JButton exportBtn = new JButton("Export to PDF");

        inputPanel.add(new JLabel("Account Number:"));
        inputPanel.add(accountField);
        inputPanel.add(new JLabel("Start Date:"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date:"));
        inputPanel.add(endDateField);
        inputPanel.add(generateBtn);
        inputPanel.add(exportBtn);

        String[] columns = {"Date", "Description", "Amount", "Balance"};
        tableModel = new DefaultTableModel(columns, 0);
        statementTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(statementTable);

        JButton backBtn = new JButton("Back to Main");
        backBtn.addActionListener(e -> frame.showCard(MainFrame.MENU));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);

        generateBtn.addActionListener(this::generateStatement);
        exportBtn.addActionListener(this::exportStatement);
    }

    private void generateStatement(ActionEvent e) {
        String accountNumber = accountField.getText().trim();
        Date startDate = (Date) startDateField.getValue();
        Date endDate = (Date) endDateField.getValue();

        tableModel.setRowCount(0);

        AccountManager.getAccountByNumber(accountNumber).ifPresentOrElse(
                account -> {
                    List<Transaction> transactions = TransactionManager.getTransactionsByAccount(accountNumber)
                            .stream()
                            .filter(t -> !t.getDate().before(startDate) && !t.getDate().after(endDate))
                            .sorted(Comparator.comparing(Transaction::getDate))
                            .toList();

                    double runningBalance = account.getBalance();
                    for (Transaction t : transactions) {
                        runningBalance -= t.getAmount(); // Adjust based on transaction type
                        tableModel.addRow(new Object[]{
                                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(t.getDate()),
                                t.getDescription(),
                                String.format("%.2f", t.getAmount()),
                                String.format("%.2f", runningBalance)
                        });
                    }
                },
                () -> JOptionPane.showMessageDialog(this, "Account not found")
        );
    }

    private void exportStatement(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "PDF export feature not implemented yet");
    }
}

