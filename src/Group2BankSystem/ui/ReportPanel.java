package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportPanel extends JPanel {
    private final MainFrame frame;
    private final JTable reportTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> typeCombo;
    private final JFormattedTextField startDateField;
    private final JFormattedTextField endDateField;

    public ReportPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        typeCombo = new JComboBox<>(new String[]{"All", "Deposits", "Withdrawals", "Transfers", "Fees", "Interest"});

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDateField = new JFormattedTextField(dateFormat);
        endDateField = new JFormattedTextField(dateFormat);
        startDateField.setValue(new Date());
        endDateField.setValue(new Date());

        JButton filterButton = new JButton("Apply Filters");
        JButton exportButton = new JButton("Export CSV");

        filterPanel.add(new JLabel("Transaction Type:"));
        filterPanel.add(typeCombo);
        filterPanel.add(new JLabel("Start Date:"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("End Date:"));
        filterPanel.add(endDateField);
        filterPanel.add(filterButton);
        filterPanel.add(exportButton);

        String[] columns = {"Date", "Account", "Type", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only amount is editable
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        JButton backButton = new JButton("Back to Main");
        backButton.addActionListener(e -> frame.showCard(MainFrame.MENU));

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        filterButton.addActionListener(this::applyFilters);
        reportTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                updateTransactionAmount(e.getFirstRow());
            }
        });
    }

    private void applyFilters(ActionEvent e) {
        Date startDate = (Date) startDateField.getValue();
        Date endDate = (Date) endDateField.getValue();
        String type = (String) typeCombo.getSelectedItem();

        tableModel.setRowCount(0);
        TransactionManager.getTransactionsByDateRange(startDate, endDate)
                .stream()
                .filter(t -> type.equals("All") || t.getType().equalsIgnoreCase(type))
                .forEach(t -> tableModel.addRow(new Object[]{
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(t.getDate()),
                        t.getAccountNumber(),
                        t.getType(),
                        t.getAmount(),
                        t.getDescription()
                }));
    }

    private void updateTransactionAmount(int row) {
        String transactionId = (String) tableModel.getValueAt(row, 0);
        double newAmount = (Double) tableModel.getValueAt(row, 3);

        if (TransactionManager.updateTransaction(transactionId, newAmount)) {
            JOptionPane.showMessageDialog(this, "Transaction updated successfully");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update transaction",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}