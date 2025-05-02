package Group2BankSystem.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
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

    private final String transactionId;
    private final Date date;
    private final String accountNumber;
    private final String type;
    private TransactionCategory category;
    private double amount;
    private final String description;
    private boolean isReconciled;


    public Transaction(String transactionId, Date date, String accountNumber,
                       String type,
                       double amount, String description) {
        this.transactionId = transactionId;
        this.date = date;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.isReconciled = false;
        determineCategory();
    }

    private void determineCategory() {
        if (type.equalsIgnoreCase("DEPOSIT")) category = TransactionCategory.DEPOSIT;
        else if (type.equalsIgnoreCase("WITHDRAWAL")) category = TransactionCategory.WITHDRAWAL;
        else if (type.equalsIgnoreCase("TRANSFER")) category = TransactionCategory.TRANSFER;
        else if (type.equalsIgnoreCase("INTEREST")) category = TransactionCategory.INTEREST;
        else if (type.equalsIgnoreCase("CREDIT_CHARGE")) category = TransactionCategory.CREDIT_CHARGE;
        else if (type.equalsIgnoreCase("PAYMENT")) category = TransactionCategory.PAYMENT;
        else if (type.equalsIgnoreCase("CHECK_ENCASHMENT")) category = TransactionCategory.CHECK_ENCASHMENT;
        else if (type.equalsIgnoreCase("ACCOUNT_CLOSED")) category = TransactionCategory.ACCOUNT_CLOSED;
        else category = TransactionCategory.GENERAL;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public Date getDate() { return date; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public TransactionCategory getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public boolean isReconciled() { return isReconciled; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setReconciled(boolean reconciled) { isReconciled = reconciled; }

    public String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

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
