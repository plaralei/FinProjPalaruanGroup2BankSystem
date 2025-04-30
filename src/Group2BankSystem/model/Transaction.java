package Group2BankSystem.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    public enum TransactionCategory {
        DEPOSIT, WITHDRAWAL, TRANSFER, FEE, INTEREST, PAYMENT, ADJUSTMENT,
        CHECK_ENCASHMENT, CREDIT_CHARGE, ACCOUNT_CLOSED
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
                       String type, TransactionCategory category,
                       double amount, String description) {
        this.transactionId = transactionId;
        this.date = date;
        this.accountNumber = accountNumber;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.isReconciled = false;
    }

    public String getTransactionId() { return transactionId; }
    public Date getDate() { return date; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public TransactionCategory getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public boolean isReconciled() { return isReconciled; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(TransactionCategory category) { this.category = category; }
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
                String.valueOf(isReconciled));
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %.2f | %s",
                getFormattedDate(), type, accountNumber, amount, description);
    }
}