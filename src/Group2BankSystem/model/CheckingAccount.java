package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.accountType = "Checking Account";
        this.overdraftLimit = 500.00;
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double limit) {
        this.overdraftLimit = limit;
        updateLastModifiedDate();
    }

    public double getAvailableBalance() {
        return balance + overdraftLimit;
    }

    public double getCreditBalance() {
        return overdraftLimit - Math.max(0, -balance);
    }

    public boolean encashCheck(double amount) throws AccountClosedException,
            InsufficientFundsException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Check amount must be positive");
        if (amount > getAvailableBalance()) {
            throw new InsufficientFundsException(getAvailableBalance(), amount);
        }

        balance -= amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "CHECK_ENCASHMENT", -amount, "Check encashment");
        return true;
    }

    @Override
    public boolean withdraw(double amount) throws InsufficientFundsException,
            AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be positive");
        if (amount > getAvailableBalance()) {
            throw new InsufficientFundsException(getAvailableBalance(), amount);
        }

        balance -= amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "WITHDRAWAL", -amount, "Cash withdrawal");
        return true;
    }
}