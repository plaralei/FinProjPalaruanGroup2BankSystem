package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public class InvestmentAccount extends BankAccount {
    private double interestRate;
    private double totalInterestEarned;

    public InvestmentAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.accountType = "Investment Account";
        this.interestRate = 0.05;
        this.totalInterestEarned = 0;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double rate) {
        this.interestRate = rate;
        updateLastModifiedDate();
    }

    public double getTotalInterestEarned() { return totalInterestEarned; }

    public void applyMonthlyInterest() throws AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);

        double interest = balance * (interestRate / 12);
        balance += interest;
        totalInterestEarned += interest;
        updateLastModifiedDate();
        TransactionManager.addTransaction(accountNumber, "INTEREST", interest, "Monthly interest");
    }

    public double computeMonthlyInterest() throws AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        return balance * (interestRate / 12);
    }
}