package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public abstract class CreditCardAccount extends BankAccount {
    private double creditLimit;

    public CreditCardAccount(String accountNumber, String accountHolderName, double creditLimit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, 0);
        if (creditLimit < 5000) throw new InvalidAmountException("Minimum credit limit: 5000");
        this.creditLimit = creditLimit;
        this.accountType = "Credit Card Account";
    }

    public void charge(double amount) throws TransactionLimitException, AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount > getAvailableCredit()) {
            throw new TransactionLimitException(getAvailableCredit());
        }

        balance += amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "CREDIT_CHARGE", amount, "Credit charge");
    }

    public void makePayment(double amount) throws AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Payment amount must be positive");

        balance -= amount;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "PAYMENT", -amount, "Credit payment");
    }

    public double getAvailableCredit() {
        return creditLimit - balance;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
    }

    @Override
    public abstract boolean transfer(BankAccount target, double amountValue);
}
