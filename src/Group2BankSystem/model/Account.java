package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;
import java.io.Serializable;
import java.util.Date;

public abstract class Account implements Serializable {
    protected String accountNumber;
    protected String accountHolderName;
    protected double balance;
    protected String accountType;
    protected boolean isActive;
    protected Date dateCreated;
    protected Date dateLastUpdated;

    public Account(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        if (initialDeposit < 0) throw new InvalidAmountException("Initial deposit cannot be negative");
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.isActive = true;
        this.dateCreated = new Date();
        this.dateLastUpdated = new Date();
    }

    public abstract boolean deposit(double amount) throws InvalidAmountException, AccountClosedException;

    public abstract boolean withdraw(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException;

    public void closeAccount() {
        isActive = false;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "ACCOUNT_CLOSED", 0, "Account closed");
    }

    protected void updateLastModifiedDate() {
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

    public abstract void setActive(boolean active);

    protected abstract void validateSufficientFunds(double amount) throws InsufficientFundsException;

    public abstract boolean transfer(BankAccount target, double amountValue);
}
