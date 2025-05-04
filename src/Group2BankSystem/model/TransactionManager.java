package Group2BankSystem.model;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages financial transactions in the banking system.
 * This class handles the addition, retrieval, updating, and persistence of transactions.
 */
public class TransactionManager {

    /** The file where transactions are stored. */
    private static final String TRANSACTIONS_FILE = "transactions.dat";

    /** A map of all transactions, keyed by transaction ID. */
    private static final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    /** A map of transactions grouped by account number. */
    private static final Map<String, List<Transaction>> accountTransactions = new ConcurrentHashMap<>();

    static {
        loadTransactions(); // Load transactions from file on startup
    }

    /**
     * Adds a new transaction to the system.
     *
     * @param accountNumber the account number associated with the transaction
     * @param type the type of transaction (e.g., DEPOSIT, WITHDRAWAL)
     * @param amount the amount involved in the transaction
     * @param description a description of the transaction
     */
    public static synchronized void addTransaction(String accountNumber, String type,
                                                   double amount, String description) {
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), new Date(), accountNumber, type, amount, description);

        transactions.put(transaction.getTransactionId(), transaction);
        accountTransactions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
        saveTransactions(); // Save transactions to file after adding
    }

    /**
     * Retrieves a list of transactions associated with a specific account.
     *
     * @param accountNumber the account number to retrieve transactions for
     * @return a list of transactions for the specified account, or an empty list if none exist
     */
    public static List<Transaction> getTransactionsByAccount(String accountNumber) {
        return accountTransactions.getOrDefault(accountNumber, Collections.emptyList());
    }

    /**
     * Retrieves a list of transactions that occurred within a specified date range.
     *
     * @param start the start date of the range
     * @param end the end date of the range
     * @return a list of transactions within the specified date range
     */
    public static List<Transaction> getTransactionsByDateRange(Date start, Date end) {
        return transactions.values().stream()
                .filter(t -> !t.getDate().before(start) && !t.getDate().after(end))
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Searches for transactions that contain a specified query string in their details.
     *
     * @param query the string to search for in transaction details
     * @return a list of transactions that match the search query
     */
    public static List<Transaction> searchTransactions(String query) {
        String lowerQuery = query.toLowerCase();
        return transactions.values().stream()
                .filter(t -> t.toString().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

/**
 * Updates the amount of an existing transaction.
 *
 * @param transactionId the ID of the transaction to update
 * @param newAmount the new amount to set for the transaction
 * @return true if the transaction was successfully updated, false otherwise
 */
public static synchronized boolean updateTransaction(String transactionId, double newAmount) {
    Transaction transaction = transactions.get(transactionId);
    if (transaction != null) {
        double oldAmount = transaction.getAmount();
        transaction.setAmount(newAmount);
        AccountManager.getAccountByNumber(transaction.getAccountNumber()).ifPresent(account -> {
            account.setBalance(account.getBalance() - oldAmount + newAmount);
        });
        saveTransactions(); // Save changes to file after updating
        return true;
    }
    return false;
}

    /**
     * Reloads transactions from the file, overwriting the current in-memory data.
     */
    public static synchronized void reloadTransactions() {
        loadTransactions();
    }

    /**
     * Saves all transactions to the specified file.
     */
    private static synchronized void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            oos.writeObject(new ArrayList<>(transactions.values()));
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    /**
     * Loads transactions from the specified file into memory.
     */
    @SuppressWarnings("unchecked")
    private static synchronized void loadTransactions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTIONS_FILE))) {
            List<Transaction> loaded = (List<Transaction>) ois.readObject();
            transactions.clear();
            accountTransactions.clear();
            loaded.forEach(t -> {
                transactions.put(t.getTransactionId(), t);
                accountTransactions.computeIfAbsent(t.getAccountNumber(),
                        k -> new ArrayList<>()).add(t);
            });
        } catch (FileNotFoundException e) {
            System.out.println("No existing transactions file. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }

    /**
     * Retrieves a summary of transactions by type within a specified date range.
     *
     * @param start the start date of the range
     * @param end the end date of the range
     * @return a map summarizing transactions by type
     */
    public static Map<Object, Object> getSummaryByType(Date start, Date end) {
        return null; // Implementation to be defined
    }

    /**
     * Retrieves a summary of transactions by category within a specified date range.
     *
     * @param start the start date of the range
     * @param end the end date of the range
     * @return a map summarizing transactions by category
     */
    public static Map<Object, Object> getSummaryByCategory(Date start, Date end) {
        return null; // Implementation to be defined
    }
}
