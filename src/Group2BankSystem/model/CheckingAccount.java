package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

/**
 * Represents a checking account which supports overdrafts and check encashment.
 * This class extends {@link BankAccount} and provides additional behavior like overdraft limit checks.
 */
public abstract class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    /**
     * Constructs a new CheckingAccount with default overdraft limit and minimum balance.
     *
     * @param accountNumber     the unique account number
     * @param accountHolderName the name of the account holder
     * @param initialDeposit    the initial deposit amount
     * @throws InvalidAmountException if the initial deposit is invalid
     */
    public CheckingAccount(String accountNumber, String accountHolderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, initialDeposit);
        this.overdraftLimit = 1000;
        this.minimumBalance = 300;
        this.accountType = "Checking Account";
    }

    /**
     * Encashes a check by withdrawing the specified amount from the account.
     *
     * @param amount the check amount
     * @return true if encashment is successful
     * @throws InsufficientFundsException if funds are insufficient
     * @throws AccountClosedException     if the account is closed
     * @throws InvalidAmountException     if the amount is invalid
     */
    @Override
    public boolean encashCheck(double amount)
            throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
        withdraw(amount);
        TransactionManager.addTransaction(accountNumber, "CHECK_ENCASHMENT", -amount, "Check encashment");
        return true;
    }

    /**
     * Returns the current account balance.
     *
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Returns the overdraft limit allowed for the account.
     *
     * @return the overdraft limit
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Transfers a specified amount to another BankAccount.
     * Implemented by subclasses to define actual transfer logic.
     *
     * @param target      the target account to transfer to
     * @param amountValue the amount to transfer
     * @return true if transfer is successful
     */
    @Override
    public abstract boolean transfer(BankAccount target, double amountValue);

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
     * Validates whether the account has sufficient funds, considering overdraft.
     *
     * @param amount the amount to check
     * @throws InsufficientFundsException if funds plus overdraft are insufficient
     */
    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
        if (balance + overdraftLimit < amount) {
            throw new InsufficientFundsException(balance + overdraftLimit, amount);
        }
    }
}