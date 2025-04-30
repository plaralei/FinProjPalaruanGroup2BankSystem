package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;
import java.io.Serializable;
import java.util.Date;

public class BankAccount implements Serializable {
    protected String accountNumber;
    protected String accountHolderName;
    protected double balance;
    protected String accountType;
    protected boolean isActive;
    protected Date dateCreated;
    protected Date dateLastUpdated;

    public BankAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        if (initialDeposit < 0) throw new InvalidAmountException("Initial deposit cannot be negative");

        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.accountType = "Bank Account";
        this.isActive = true;
        this.dateCreated = new Date();
        this.dateLastUpdated = new Date();
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public boolean isActive() { return isActive; }
    public Date getDateCreated() { return dateCreated; }
    public Date getDateLastUpdated() { return dateLastUpdated; }

    public void setAccountHolderName(String name) {
        this.accountHolderName = name;
        updateLastModifiedDate();
    }

    protected void updateLastModifiedDate() {
        this.dateLastUpdated = new Date();
    }

    public boolean deposit(double amount) throws InvalidAmountException, AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be positive");

        balance += amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "DEPOSIT", amount, "Cash deposit");
        return true;
    }

    public boolean withdraw(double amount) throws InsufficientFundsException,
            AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be positive");
        if (amount > balance) throw new InsufficientFundsException(balance, amount);

        balance -= amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "WITHDRAWAL", -amount, "Cash withdrawal");
        return true;
    }

    public boolean transfer(BankAccount recipient, double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (!recipient.isActive) throw new AccountClosedException(recipient.getAccountNumber());
        if (amount <= 0) throw new InvalidAmountException("Transfer amount must be positive");
        if (amount > balance) throw new InsufficientFundsException(balance, amount);

        this.balance -= amount;
        recipient.balance += amount;
        this.updateLastModifiedDate();
        recipient.updateLastModifiedDate();

        TransactionManager.addTransaction(accountNumber, "TRANSFER_OUT", -amount,
                "Transfer to " + recipient.getAccountNumber());
        TransactionManager.addTransaction(recipient.getAccountNumber(), "TRANSFER_IN", amount,
                "Transfer from " + accountNumber);

        return true;
    }

    public void closeAccount() {
        isActive = false;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "ACCOUNT_CLOSED", 0, "Account closed");
    }
}