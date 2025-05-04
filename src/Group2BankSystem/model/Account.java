package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Abstract class representing a bank account.
 * Provides common properties and methods for different types of accounts.
 */
public abstract class Account implements Serializable {

    protected String accountNumber;
    protected String accountHolderName;
    protected double balance;
    protected String accountType;
    protected boolean isActive;
    protected Date dateCreated;
    protected Date dateLastUpdated;

    /**
     * Constructs a new Account with an account number, account holder name, and an initial deposit.
     *
     * @param accountNumber the unique identifier for the account
     * @param accountHolderName the name of the account holder
     * @param initialDeposit the initial deposit to start the account
     * @throws InvalidAmountException if the initial deposit is negative
     */
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

    /**
     * Deposits a specified amount into the account.
     *
     * @param amount the amount to deposit
     * @return true if the deposit was successful, false otherwise
     * @throws InvalidAmountException if the deposit amount is invalid
     * @throws AccountClosedException if the account is closed
     */
    public abstract boolean deposit(double amount) throws InvalidAmountException, AccountClosedException;

    /**
     * Withdraws a specified amount from the account.
     *
     * @param amount the amount to withdraw
     * @return true if the withdrawal was successful, false otherwise
     * @throws InsufficientFundsException if there are not enough funds in the account
     * @throws AccountClosedException if the account is closed
     * @throws InvalidAmountException if the withdrawal amount is invalid
     */
    public abstract boolean withdraw(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException;

    /**
     * Closes the account. The account can only be closed if the balance is zero.
     */
    public void closeAccount() {
        if (balance > 0) {
            System.out.println("Account cannot be closed. Balance must be zero.");
            return;
        }

        isActive = false;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "ACCOUNT_CLOSED", 0, "Account closed");
        System.out.println("Account successfully closed.");
    }

    /**
     * Updates the date when the account was last modified.
     */
    protected void updateLastModifiedDate() {
        this.dateLastUpdated = new Date();
    }

    /**
     * Gets the account number.
     *
     * @return the account number
     */
    public String getAccountNumber() { return accountNumber; }

    /**
     * Gets the account holder's name.
     *
     * @return the name of the account holder
     */
    public String getAccountHolderName() { return accountHolderName; }

    /**
     * Gets the current balance of the account.
     *
     * @return the account balance
     */
    public double getBalance() { return balance; }

    /**
     * Gets the type of the account (e.g., checking, savings).
     *
     * @return the account type
     */
    public String getAccountType() { return accountType; }

    /**
     * Checks if the account is active.
     *
     * @return true if the account is active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Gets the date when the account was created.
     *
     * @return the account creation date
     */
    public Date getDateCreated() { return dateCreated; }

    /**
     * Gets the date when the account was last updated.
     *
     * @return the date of last update
     */
    public Date getDateLastUpdated() { return dateLastUpdated; }

    /**
     * Sets the account holder's name and updates the last modified date.
     *
     * @param name the new account holder name
     */
    public void setAccountHolderName(String name) {
        this.accountHolderName = name;
        updateLastModifiedDate();
    }

    /**
     * Sets the account's active status.
     *
     * @param active true if the account is to be set as active, false otherwise
     */
    public abstract void setActive(boolean active);

    /**
     * Validates that there are sufficient funds for a withdrawal or transfer.
     *
     * @param amount the amount to check
     * @throws InsufficientFundsException if there are not enough funds
     */
    protected abstract void validateSufficientFunds(double amount) throws InsufficientFundsException;

    /**
     * Transfers a specified amount to another account.
     *
     * @param target the target account for the transfer
     * @param amountValue the amount to transfer
     * @return true if the transfer was successful, false otherwise
     */
    public abstract boolean transfer(BankAccount target, double amountValue);
}
