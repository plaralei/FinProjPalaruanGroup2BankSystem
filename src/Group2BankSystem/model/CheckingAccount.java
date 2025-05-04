package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public abstract class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.overdraftLimit = 1000;
        this.minimumBalance = 300;
        this.accountType = "Checking Account";
    }

    @Override
    public boolean encashCheck(double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
        withdraw(amount);
        TransactionManager.addTransaction(accountNumber, "CHECK_ENCASHMENT", -amount, "Check encashment");
        return true;
    }

    public double getAvailableBalance() {
        return balance + overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public abstract boolean transfer(BankAccount target, double amountValue);

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
        if (balance + overdraftLimit < amount) {
            throw new InsufficientFundsException(balance + overdraftLimit, amount);
        }
    }
}
