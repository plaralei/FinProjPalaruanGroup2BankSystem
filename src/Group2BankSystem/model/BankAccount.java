package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;
import java.io.Serializable;

/**
 * Represents a general-purpose bank account with basic operations such as deposit, withdraw,
 * and support for interest calculation and card transactions.
 */
public abstract class BankAccount extends Account implements Serializable {
    protected double minimumBalance;

    /**
     * Constructs a new BankAccount with the given details.
     *
     * @param accountNumber     the unique account number
     * @param accountHolderName the name of the account holder
     * @param initialDeposit    the initial deposit amount
     * @throws InvalidAmountException if the initial deposit is invalid
     */
    public BankAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.minimumBalance = 0;
        this.accountType = "Bank Account";
    }

    /**
     * Deposits a specified amount into the account.
     *
     * @param amount the amount to deposit
     * @return true if deposit is successful
     * @throws InvalidAmountException   if the amount is not positive
     * @throws AccountClosedException   if the account is closed
     */
    @Override
    public boolean deposit(double amount) throws InvalidAmountException, AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be positive");

        balance += amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "DEPOSIT", amount, "Cash deposit");
        return true;
    }

    /**
     * Withdraws a specified amount from the account.
     *
     * @param amount the amount to withdraw
     * @return true if withdrawal is successful
     * @throws InsufficientFundsException if funds are insufficient after maintaining minimum balance
     * @throws InvalidAmountException     if the amount is not positive
     * @throws AccountClosedException     if the account is closed
     */
    @Override
    public boolean withdraw(double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be positive");
        if (balance - amount < minimumBalance) {
            throw new InsufficientFundsException(balance - minimumBalance, amount);
        }

        balance -= amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "WITHDRAWAL", -amount, "Cash withdrawal");
        return true;
    }

    /**
     * Closes the account by marking it as inactive.
     */
    @Override
    public void closeAccount() {
        super.closeAccount();
    }

    /**
     * Sets the minimum balance required for the account.
     *
     * @param minimum the new minimum balance
     */
    public void setMinimumBalance(double minimum) {
        this.minimumBalance = minimum;
    }

    /**
     * Sets the account balance manually (used with caution).
     *
     * @param v the new balance value
     */
    public void setBalance(double v) {
        this.balance = v;
        updateLastModifiedDate();
    }

    /**
     * Encashes a check for a specified amount.
     *
     * @param amount the check amount
     * @return true if encashment is successful
     * @throws InsufficientFundsException if funds are insufficient
     * @throws AccountClosedException     if the account is closed
     * @throws InvalidAmountException     if the amount is invalid
     */
    public abstract boolean encashCheck(double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException;

    /**
     * Charges a specified amount to a card linked to the account.
     *
     * @param amount the amount to charge
     * @return true if charge is successful
     */
    public abstract boolean chargeToCard(double amount);

    /**
     * Pays a specified amount to the card linked to the account.
     *
     * @param amount the amount to pay
     * @return true if payment is successful
     */
    public abstract boolean payCard(double amount);

    /**
     * Computes the monthly interest for the account.
     *
     * @return the interest amount
     */
    public abstract double computeMonthlyInterest();
}
