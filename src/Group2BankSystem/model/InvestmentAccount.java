package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public abstract class InvestmentAccount extends BankAccount {
    private double interestRate;
    private double totalInterestEarned;

    public InvestmentAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.interestRate = 0.05;
        this.minimumBalance = 500;
        this.accountType = "Investment Account";
    }

    public void applyMonthlyInterest() throws AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        double interest = balance * interestRate;
        balance += interest;
        totalInterestEarned += interest;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "INTEREST", interest, "Monthly interest");
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
        if (balance - amount < minimumBalance) {
            throw new InsufficientFundsException(balance - minimumBalance, amount);
        }
    }
}
