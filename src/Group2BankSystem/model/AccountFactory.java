package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

/**
 * Factory class responsible for creating and converting different types of BankAccount instances.
 */
public class AccountFactory {
    private static int accountCounter = 202500000;

    /**
     * Creates a new BankAccount of a specific type.
     *
     * @param accountHolderName the name of the account holder
     * @param initialAmount the initial deposit amount
     * @param accountType the type of account to create (e.g., "Checking Account")
     * @return a new instance of the specified BankAccount type
     * @throws InvalidAmountException if the initial amount is invalid (e.g., negative)
     */
    public static BankAccount createAccount(String accountHolderName, double initialAmount, String accountType)
            throws InvalidAmountException {
        String accountNumber = generateAccountNumber();

        switch (accountType) {
            case "Checking Account":
                return new CheckingAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override public boolean chargeToCard(double amount) { return false; }
                    @Override public boolean payCard(double amount) { return false; }
                    @Override public double computeMonthlyInterest() { return 0; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance + getOverdraftLimit() < amount) {
                            throw new InsufficientFundsException(balance + getOverdraftLimit(), amount);
                        }
                    }
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };

            case "Investment Account":
                return new InvestmentAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override public boolean encashCheck(double amount) { return false; }
                    @Override public boolean chargeToCard(double amount) { return false; }
                    @Override public boolean payCard(double amount) { return false; }
                    @Override public double computeMonthlyInterest() { return balance * 0.05; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - amount < minimumBalance) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }
                    @Override public boolean transfer(BankAccount target, double amountValue) { return true; }
                };

            case "Credit Card Account":
                return new CreditCardAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override public boolean encashCheck(double amount) { return false; }
                    @Override public boolean chargeToCard(double amount) {
                        try { charge(amount); return true; } catch (Exception e) { return false; }
                    }
                    @Override public boolean payCard(double amount) {
                        try { makePayment(amount); return true; } catch (Exception e) { return false; }
                    }
                    @Override public double computeMonthlyInterest() { return 0; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) {}
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };

            default:
                return new BankAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override public boolean encashCheck(double amount) { return false; }
                    @Override public boolean chargeToCard(double amount) { return false; }
                    @Override public boolean payCard(double amount) { return false; }
                    @Override public double computeMonthlyInterest() { return 0; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - amount < minimumBalance) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };
        }
    }

    /**
     * Converts an existing BankAccount into another type, preserving key data like account number and balance.
     *
     * @param oldAccount the account to be converted
     * @param newType the type to convert to
     * @return a new instance of the converted account
     * @throws InvalidAmountException if the account's balance is invalid during conversion
     * @throws AccountConversionException if the conversion type is not supported
     */
    public static BankAccount convertAccount(BankAccount oldAccount, String newType)
            throws InvalidAmountException, AccountConversionException {

        if (!isValidConversion(oldAccount.getAccountType(), newType)) {
            throw new AccountConversionException("Invalid account conversion");
        }

        try {
            return switch (newType) {
                case "Checking Account" -> new CheckingAccount(oldAccount.getAccountNumber(), oldAccount.getAccountHolderName(), oldAccount.getBalance()) {
                    @Override public boolean chargeToCard(double amount) { return false; }
                    @Override public boolean payCard(double amount) { return false; }
                    @Override public double computeMonthlyInterest() { return 0; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance + getOverdraftLimit() < amount) {
                            throw new InsufficientFundsException(balance + getOverdraftLimit(), amount);
                        }
                    }
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };

                case "Investment Account" -> new InvestmentAccount(oldAccount.getAccountNumber(), oldAccount.getAccountHolderName(), oldAccount.getBalance()) {
                    @Override public boolean encashCheck(double amount) { return false; }
                    @Override public boolean chargeToCard(double amount) { return false; }
                    @Override public boolean payCard(double amount) { return false; }
                    @Override public double computeMonthlyInterest() { return balance * 0.05; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - minimumBalance < amount) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };

                case "Credit Card Account" -> new CreditCardAccount(oldAccount.getAccountNumber(), oldAccount.getAccountHolderName(), oldAccount.getBalance()) {
                    @Override public boolean encashCheck(double amount) { return false; }
                    @Override public boolean chargeToCard(double amount) {
                        try { charge(amount); return true; } catch (Exception e) { return false; }
                    }
                    @Override public boolean payCard(double amount) {
                        try { makePayment(amount); return true; } catch (Exception e) { return false; }
                    }
                    @Override public double computeMonthlyInterest() { return 0; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) {}
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };

                default -> new BankAccount(oldAccount.getAccountNumber(), oldAccount.getAccountHolderName(), oldAccount.getBalance()) {
                    @Override public boolean encashCheck(double amount) { return false; }
                    @Override public boolean chargeToCard(double amount) { return false; }
                    @Override public boolean payCard(double amount) { return false; }
                    @Override public double computeMonthlyInterest() { return 0; }
                    @Override public void setActive(boolean active) { this.isActive = active; }
                    @Override protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - minimumBalance < amount) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }
                    @Override public boolean transfer(BankAccount target, double amountValue) { return false; }
                };
            };
        } catch (InvalidAmountException e) {
            throw new AccountConversionException("Conversion failed: " + e.getMessage());
        }
    }

    /**
     * Validates if conversion from one account type to another is allowed.
     *
     * @param oldType the current account type
     * @param newType the new desired account type
     * @return true if valid, false otherwise
     */
    private static boolean isValidConversion(String oldType, String newType) {
        return !oldType.equals("Credit Card Account") || newType.equals("Bank Account");
    }

    /**
     * Generates a new unique account number.
     *
     * @return the generated account number as a string
     */
    private static String generateAccountNumber() {
        accountCounter++;
        return String.valueOf(accountCounter);
    }

    /**
     * Exception class to handle invalid account conversion attempts.
     */
    public static class AccountConversionException extends Exception {
        public AccountConversionException(String message) {
            super(message);
        }
    }
}
