package Group2BankSystem.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a financial transaction in the banking system.
 * This class implements Serializable to allow transaction objects
 * to be serialized for storage or transmission.
 */
public class Transaction implements Serializable {

    /** Enumeration of possible transaction categories. */
    public enum TransactionCategory {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        FEE,
        INTEREST,
        PAYMENT,
        CHECK_ENCASHMENT,
        CREDIT_CHARGE,
        ACCOUNT_CLOSED,
        GENERAL
    }

    /** Unique identifier for the transaction. */
    private final String transactionId;

    /** The date and time when the transaction occurred. */
    private final Date date;

    /** The account number associated with the transaction. */
    private final String accountNumber;

    /** The type of transaction (e.g., DEPOSIT, WITHDRAWAL). */
    private final String type;

    /** The category of the transaction. */
    private TransactionCategory category;

    /** The amount involved in the transaction. */
    private double amount;

    /** A description of the transaction. */
    private final String description;

    /** Indicates whether the transaction has been reconciled. */
    private boolean isReconciled;

    /**
     * Constructs a Transaction with the specified details.
     *
     * @param transactionId the unique identifier for the transaction
     * @param date the date and time of the transaction
     * @param accountNumber the account number associated with the transaction
     * @param type the type of transaction
     * @param amount the amount involved in the transaction
     * @param description a description of the transaction
     */
    public Transaction(String transactionId, Date date, String accountNumber,
                       String type, double amount, String description) {
        this.transactionId = transactionId;
        this.date = date;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.isReconciled = false; // Default to not reconciled
        determineCategory(); // Determine the transaction category
    }

    /**
     * Determines the category of the transaction based on its type.
     */
    private void determineCategory() {
        if (type.equalsIgnoreCase("DEPOSIT")) category = TransactionCategory.DEPOSIT;
        else if (type.equalsIgnoreCase("WITHDRAWAL")) category = TransactionCategory.WITHDRAWAL;
        else if (type.equalsIgnoreCase("TRANSFER")) category = TransactionCategory.TRANSFER;
        else if (type.equalsIgnoreCase("INTEREST")) category = TransactionCategory.INTEREST;
        else if (type.equalsIgnoreCase("CREDIT_CHARGE")) category = TransactionCategory.CREDIT_CHARGE;
        else if (type.equalsIgnoreCase("PAYMENT")) category = TransactionCategory.PAYMENT;
        else if (type.equalsIgnoreCase("CHECK_ENCASHMENT")) category = TransactionCategory.CHECK_ENCASHMENT;
        else if (type.equalsIgnoreCase("ACCOUNT_CLOSED")) category = TransactionCategory.ACCOUNT_CLOSED;
        else category = TransactionCategory.GENERAL; // Default category
    }

    // Getters for transaction properties
    public String getTransactionId() { return transactionId; }
    public Date getDate() { return date; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public TransactionCategory getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public boolean isReconciled() { return isReconciled; }

    // Setters for transaction properties
    public void setAmount(double amount) { this.amount = amount; }
    public void setReconciled(boolean reconciled) { isReconciled = reconciled; }
    /**
     * Returns the date of the transaction formatted as a string.
     *
     * @return the formatted date string
     */
    public String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * Returns a CSV representation of the transaction.
     *
     * @return a CSV string representing the transaction
     */
    public String toCSVString() {
        return String.join(",",
                transactionId,
                getFormattedDate(),
                accountNumber,
                type,
                category.name(),
                String.valueOf(amount),
                description,
                String.valueOf(isReconciled)
        );
    }

    /**
     * Returns a string representation of the transaction.
     *
     * @return a string describing the transaction
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", date=" + getFormattedDate() +
                ", accountNumber='" + accountNumber + '\'' +
                ", type='" + type + '\'' +
                ", category=" + category +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", isReconciled=" + isReconciled +
                '}';
    }
}

