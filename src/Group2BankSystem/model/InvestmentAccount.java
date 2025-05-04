package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

/**
 * Abstract class representing an Investment Account in a banking system.
 * This class extends the BankAccount class and provides functionality
 * specific to investment accounts, including interest calculations.
 */
public abstract class InvestmentAccount extends BankAccount {

    /** The interest rate applied to the account balance. */
    private double interestRate;

    /** The total interest earned on the account. */
    private double totalInterestEarned;

    /**
     * Constructs an InvestmentAccount with the specified account number,
     * account holder name, and initial deposit.
     *
     * @param accountNumber the unique identifier for the account
     * @param accountHolderName the name of the account holder
     * @param initialDeposit the initial amount to deposit into the account
     * @throws InvalidAmountException if the initial deposit is invalid
     */
    public InvestmentAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.interestRate = 0.05; // Default interest rate of 5%
        this.minimumBalance = 500; // Minimum balance requirement
        this.accountType = "Investment Account"; // Type of account
    }

    /**
     * Applies monthly interest to the account balance.
     *
     * @throws AccountClosedException if the account is closed
     */
    public void applyMonthlyInterest() throws AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        double interest = balance * interestRate; // Calculate interest
        balance += interest; // Update balance with interest
        totalInterestEarned += interest; // Update total interest earned
        updateLastModifiedDate(); // Update the last modified date
        TransactionManager.addTransaction(accountNumber, "INTEREST", interest, "Monthly interest");
    }

    /**
     * Returns the interest rate of the account.
     *
     * @return the interest rate
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Returns the total interest earned on the account.
     *
     * @return the total interest earned
     */
    public double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    /**
     * Sets the active status of the account.
     *
     * @param active true to activate the account, false to deactivate
     */
    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Validates whether there are sufficient funds for a transaction.
     * Ensures that the balance after the transaction does not fall below
     * the minimum balance requirement.
     *
     * @param amount the amount to validate
     * @throws InsufficientFundsException if there are not enough funds
     */
    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
        if (balance - amount < minimumBalance) {
            throw new InsufficientFundsException(balance - minimumBalance, amount);
        }
    }
}
