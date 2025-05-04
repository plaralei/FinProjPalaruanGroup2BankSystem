package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.table.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@code BankAccountTableModel} class is a custom table model for displaying
 * {@link BankAccount} data in a JTable. It defines the structure and content of the table,
 * including columns for account number, account holder, balance, and account status.
 * <p>
 * This model is used in conjunction with the {@code BankAccountPanel}.
 * </p>
 *
 * @author
 */
public class BankAccountTableModel extends AbstractTableModel {
    private final String[] columns = {"Account Number", "Account Holder", "Balance", "Status"};
    private List<BankAccount> accounts;

    /**
     * Constructs a new {@code BankAccountTableModel} and loads all active {@code BankAccount} instances.
     */
    public BankAccountTableModel() {
        refresh();
    }

    /**
     * Returns the number of rows in the table, which corresponds to the number of bank accounts.
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
     * Returns the name of the column at the specified index.
     *
     * @param column the column index
     * @return the name of the column
     */
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    /**
     * Returns the value to display at the specified cell.
     *
     * @param row the row index
     * @param column the column index
     * @return the cell value
     */
    @Override
    public Object getValueAt(int row, int column) {
        BankAccount account = accounts.get(row);
        return switch (column) {
            case 0 -> account.getAccountNumber();
            case 1 -> account.getAccountHolderName();
            case 2 -> String.format("%.2f", account.getBalance());
            case 3 -> account.isActive() ? "Active" : "Closed";
            default -> null;
        };
    }

    /**
     * Returns the {@code BankAccount} at the specified row index.
     *
     * @param row the row index
     * @return the {@code BankAccount} at the given row
     */
    public BankAccount getAccountAt(int row) {
        return accounts.get(row);
    }

    /**
     * Refreshes the list of accounts in the table by reloading data from the {@code AccountManager}.
     * Only instances of {@code BankAccount} are included.
     */
    public void refresh() {
        accounts = AccountManager.getAccounts().stream()
                .filter(acc -> acc.getClass() == BankAccount.class)
                .collect(Collectors.toList());
        fireTableDataChanged();
    }
}
