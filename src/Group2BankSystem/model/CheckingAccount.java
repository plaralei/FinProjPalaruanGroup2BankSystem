package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

/**
 * Abstract class representing a checking account in the bank system.
 * A checking account supports overdraft and check encashment functionality.
 */
public abstract class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    /**
     * Constructs a CheckingAccount with the specified account number,
     * account holder name, and initial deposit.
     *
     * @param accountNumber       The unique identifier for the account.
     * @param accountHolderName   The name of the account holder.
     * @param initialDeposit      The initial amount deposited.
     * @throws InvalidAmountException if the initial deposit is invalid.
     */
    public CheckingAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.overdraftLimit = 1000;
        this.minimumBalance = 300;
        this.accountType = "Checking Account";
    }

    /**
     * Encashes a check by withdrawing the specified amount.
     * Records the transaction upon success.
     *
     * @param amount The amount to withdraw via check encashment.
     * @return true if the check is successfully encashed.
     * @throws InsufficientFundsException if the balance and overdraft limit are insufficient.
     * @throws AccountClosedException if the account is closed.
     * @throws InvalidAmountException if the amount is invalid.
     */
    @Override
    public boolean encashCheck(double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
        withdraw(amount);
        TransactionManager.addTransaction(accountNumber, "CHECK_ENCASHMENT", -amount, "Check encashment");
        return true;
    }

    /**
     * Returns the available balance including the overdraft limit.
     *
     * @return The total funds available (balance + overdraft limit).
     */
    public double getAvailableBalance() {
        return balance + overdraftLimit;
    }

    /**
     * Returns the overdraft limit of the checking account.
     *
     * @return The overdraft limit.
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Abstract method to handle transferring money to another account.
     *
     * @param target      The target bank account to transfer to.
     * @param amountValue The amount to transfer.
     * @return true if the transfer is successful.
     */
    @Override
    public abstract boolean transfer(BankAccount target, double amountValue);

    /**
     * Sets the active status of the account.
     *
     * @param active true to activate the account, false to deactivate.
     */
    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Validates that there are sufficient funds (including overdraft)
     * to perform a withdrawal or transaction.
     *
     * @param amount The amount to validate.
     * @throws InsufficientFundsException if funds including overdraft are insufficient.
     */
    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
        if (balance + overdraftLimit < amount) {
            throw new InsufficientFundsException(balance + overdraftLimit, amount);
        }
    }
}
