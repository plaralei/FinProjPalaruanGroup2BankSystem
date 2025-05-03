package Group2BankSystem.model;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionManager {
    private static final String TRANSACTIONS_FILE = "transactions.dat";
    private static final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    private static final Map<String, List<Transaction>> accountTransactions = new ConcurrentHashMap<>();

    static {
        loadTransactions();
    }

    public static synchronized void addTransaction(String accountNumber, String type,
                                                   double amount, String description) {
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), new Date(), accountNumber, type, amount, description);

        transactions.put(transaction.getTransactionId(), transaction);
        accountTransactions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
        saveTransactions();
    }

    public static List<Transaction> getTransactionsByAccount(String accountNumber) {
        return accountTransactions.getOrDefault(accountNumber, Collections.emptyList());
    }

    public static List<Transaction> getTransactionsByDateRange(Date start, Date end) {
        return transactions.values().stream()
                .filter(t -> !t.getDate().before(start) && !t.getDate().after(end))
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
    }

    public static List<Transaction> searchTransactions(String query) {
        String lowerQuery = query.toLowerCase();
        return transactions.values().stream()
                .filter(t -> t.toString().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public static synchronized boolean updateTransaction(String transactionId, double newAmount) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            double oldAmount = transaction.getAmount();
            transaction.setAmount(newAmount);
            AccountManager.getAccountByNumber(transaction.getAccountNumber()).ifPresent(account -> {
                account.setBalance(account.getBalance() - oldAmount + newAmount);
            });
            saveTransactions();
            return true;
        }
        return false;
    }

    public static synchronized void reloadTransactions() {
        loadTransactions();
    }

    private static synchronized void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            oos.writeObject(new ArrayList<>(transactions.values()));
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

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

    public static Map<Object, Object> getSummaryByType(Date start, Date end) {
        return null;
    }

    public static Map<Object, Object> getSummaryByCategory(Date start, Date end) {
        return null;
    }
}
