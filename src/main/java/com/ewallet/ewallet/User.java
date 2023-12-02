package com.ewallet.ewallet;

public class User {
    private final String username;
    private final String password;
    private final String telephone;
    private double balance;

    public User(String username, String password, String telephone) {
        this.username = username;
        this.password = password;
        this. telephone = telephone;
        this.balance = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    protected String getTelephone() {
        return telephone;
    }
    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public boolean transfer(User recipient, double amount) {
        if (amount > balance) {
            return false;
        }
        this.withdraw(amount);
        recipient.deposit(amount);
        return true;
    }


    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + telephone + '\'' +
                '}';
    }
}

