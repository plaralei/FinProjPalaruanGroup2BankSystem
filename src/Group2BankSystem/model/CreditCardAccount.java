package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public class CreditCardAccount extends BankAccount {
    private double creditLimit;
    private double availableCredit;

    public CreditCardAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.accountType = "Credit Card Account";
        this.creditLimit = 1000.00;
        this.availableCredit = creditLimit;
    }

    public double getCreditLimit() { return creditLimit; }
    public double getAvailableCredit() { return availableCredit; }

    public void setCreditLimit(double limit) throws InvalidAmountException {
        if (limit <= 0) throw new InvalidAmountException("Credit limit must be positive");
        this.creditLimit = limit;
        this.availableCredit = limit - Math.max(0, -balance);
        updateLastModifiedDate();
    }

    public boolean chargeToCard(double amount) throws AccountClosedException,
            InvalidAmountException, TransactionLimitException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Charge amount must be positive");
        if (amount > availableCredit) throw new TransactionLimitException(availableCredit);

        balance -= amount;
        availableCredit -= amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "CREDIT_CHARGE", -amount, "Credit card charge");
        return true;
    }

    public boolean payCard(double amount) throws AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Payment amount must be positive");

        balance += amount;
        availableCredit = Math.min(creditLimit, availableCredit + amount);
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "CREDIT_PAYMENT", amount, "Credit card payment");
        return true;
    }
}