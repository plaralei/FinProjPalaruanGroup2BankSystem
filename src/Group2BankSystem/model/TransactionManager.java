package Group2BankSystem.model;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {
    private static final String TRANSACTIONS_FILE = "transactions.dat";
    private static final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());
    private static final Object fileLock = new Object();

    static {
        loadTransactions();
    }

    public static List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public static void addTransaction(String accountNumber, String type,
                                      double amount, String description) {
        synchronized(transactions) {
            String transactionId = "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
            Transaction.TransactionCategory category = determineCategory(type);
            Transaction transaction = new Transaction(
                    transactionId,
                    new Date(),
                    accountNumber,
                    type,
                    category,
                    amount,
                    description
            );
            transactions.add(transaction);
            saveTransactions();
        }
    }

    private static Transaction.TransactionCategory determineCategory(String type) {
        return switch(type.toUpperCase()) {
            case "DEPOSIT" -> Transaction.TransactionCategory.DEPOSIT;
            case "WITHDRAWAL" -> Transaction.TransactionCategory.WITHDRAWAL;
            case "TRANSFER_IN", "TRANSFER_OUT" -> Transaction.TransactionCategory.TRANSFER;
            case "INTEREST" -> Transaction.TransactionCategory.INTEREST;
            case "CHECK_ENCASHMENT" -> Transaction.TransactionCategory.CHECK_ENCASHMENT;
            case "CREDIT_CHARGE" -> Transaction.TransactionCategory.CREDIT_CHARGE;
            case "ACCOUNT_CLOSED" -> Transaction.TransactionCategory.ACCOUNT_CLOSED;
            default -> Transaction.TransactionCategory.ADJUSTMENT;
        };
    }

    public static List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .collect(Collectors.toList());
    }

    public static List<Transaction> getTransactionsByDateRange(Date start, Date end) {
        return transactions.stream()
                .filter(t -> !t.getDate().before(start) && !t.getDate().after(end))
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
    }

    public static Map<String, Double> getSummaryByType(Date start, Date end) {
        return transactions.stream()
                .filter(t -> !t.getDate().before(start) && !t.getDate().after(end))
                .collect(Collectors.groupingBy(
                        Transaction::getType,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public static Map<Transaction.TransactionCategory, Double> getSummaryByCategory(Date start, Date end) {
        return transactions.stream()
                .filter(t -> !t.getDate().before(start) && !t.getDate().after(end))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public static boolean updateTransaction(String transactionId, double newAmount) {
        synchronized(transactions) {
            return transactions.stream()
                    .filter(t -> t.getTransactionId().equals(transactionId))
                    .findFirst()
                    .map(t -> {
                        t.setAmount(newAmount);
                        saveTransactions();
                        return true;
                    })
                    .orElse(false);
        }
    }

    public static Transaction getTransactionById(String transactionId) {
        return transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElse(null);
    }

    public static List<Transaction> searchTransactions(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return transactions.stream()
                .filter(t -> t.getAccountNumber().contains(keyword) ||
                        t.getDescription().toLowerCase().contains(lowerKeyword) ||
                        t.getType().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static void loadTransactions() {
        synchronized(fileLock) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTIONS_FILE))) {
                List<Transaction> loaded = (List<Transaction>) ois.readObject();
                transactions.clear();
                transactions.addAll(loaded);
            } catch (FileNotFoundException e) {
                System.out.println("No existing transactions file. Starting fresh.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading transactions: " + e.getMessage());
                transactions.clear();
            }
        }
    }

    public static void saveTransactions() {
        synchronized(fileLock) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
                oos.writeObject(new ArrayList<>(transactions));
            } catch (IOException e) {
                System.err.println("Error saving transactions: " + e.getMessage());
            }
        }
    }

    public static void exportToCSV(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("TransactionID,Date,AccountNumber,Type,Category,Amount,Description,Reconciled");
            transactions.forEach(t -> writer.println(t.toCSVString()));
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
        }
    }
}