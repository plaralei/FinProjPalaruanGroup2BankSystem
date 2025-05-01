package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;
import java.io.Serializable;

public abstract class BankAccount extends Account implements Serializable {
    protected double minimumBalance;

    public BankAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.minimumBalance = 0;
        this.accountType = "Bank Account";
    }

    @Override
    public boolean deposit(double amount) throws InvalidAmountException, AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be positive");

        balance += amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "DEPOSIT", amount, "Cash deposit");
        return true;
    }

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

    @Override
    public void closeAccount() {
        super.closeAccount();
    }

    public void setMinimumBalance(double minimum) {
        this.minimumBalance = minimum;
    }

    public void setBalance(double v) {
        this.balance = v;
        updateLastModifiedDate();
    }

    public abstract boolean encashCheck(double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException;

    public abstract boolean chargeToCard(double amount);

    public abstract boolean payCard(double amount);

    public abstract double computeMonthlyInterest();
}
