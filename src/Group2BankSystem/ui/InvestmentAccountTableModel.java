package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.table.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A table model for displaying a list of {@link InvestmentAccount}s in a JTable.
 * Provides columns for account number, holder name, balance, interest rate, and status.
 */
public class InvestmentAccountTableModel extends AbstractTableModel {
    private final String[] columns = {"Account Number", "Account Holder", "Balance", "Interest Rate", "Status"};
    private List<InvestmentAccount> accounts;

    /**
     * Constructs a new InvestmentAccountTableModel and loads current accounts.
     */
    public InvestmentAccountTableModel() {
        refresh();
    }

    /**
     * Returns the number of rows (accounts) in the table.
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return accounts.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns the name of the specified column.
     *
     * @param column the index of the column
     * @return the column name
     */
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    /**
     * Returns the value at the specified row and column.
     *
     * @param row the row index
     * @param column the column index
     * @return the cell value
     */
    @Override
    public Object getValueAt(int row, int column) {
        InvestmentAccount account = accounts.get(row);
        return switch (column) {
            case 0 -> account.getAccountNumber();
            case 1 -> account.getAccountHolderName();
            case 2 -> String.format("%.2f", account.getBalance());
            case 3 -> {yield String.format("%.2f%%",  account.getInterestRate());}
            case 4 -> account.isActive() ? "Active" : "Closed";
            default -> null;
        };
    }

    /**
     * Returns the InvestmentAccount at the specified row.
     *
     * @param row the row index
     * @return the InvestmentAccount object
     */
    public InvestmentAccount getAccountAt(int row) {
        return accounts.get(row);
    }

    /**
     * Refreshes the table data by reloading all investment accounts from the AccountManager.
     */
    public void refresh() {
        accounts = AccountManager.getAccounts().stream()
                .filter(acc -> acc instanceof InvestmentAccount)
                .map(acc -> (InvestmentAccount) acc)
                .collect(Collectors.toList());
        fireTableDataChanged();
    }
}