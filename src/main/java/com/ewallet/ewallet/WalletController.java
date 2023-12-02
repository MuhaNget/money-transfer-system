package com.ewallet.ewallet;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



import java.util.ArrayList;

public class WalletController {
    private final ArrayList<User> users = new ArrayList<>();
    private User loggedInUser;
    private final Stage primaryStage;
    private Label balanceLabel;
    private double balance;

    public WalletController(Stage primaryStage){
        this.primaryStage = primaryStage;
    }
    protected Scene createMainScene() {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));

        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");

        registerButton.setOnAction(e -> primaryStage.setScene(createRegisterScene()));
        loginButton.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        mainLayout.getChildren().addAll(registerButton, loginButton);

        return new Scene(mainLayout, 400, 400);
    }

    protected Scene createRegisterScene() {
        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField telephoneField = new TextField();
        Button registerButton = new Button("Register");
        Button exitButton = new Button("Exit");


        registerButton.setOnAction(e -> {
            if(registerUser(usernameField.getText(), passwordField.getText(), telephoneField.getText())){
                usernameField.clear();
                passwordField.clear();
                primaryStage.setScene(createLoginScene());
            }
            System.out.println("Please enter all fields to proceed.");
        });
        exitButton.setOnAction(e -> primaryStage.setScene(createMainScene()));


        registerLayout.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Telephone #:"), telephoneField,
                registerButton,exitButton
        );

        return new Scene(registerLayout, 400, 400);
    }

    protected Scene createLoginScene() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit");


        loginButton.setOnAction(e -> {
            if (validateLogin(usernameField.getText(), passwordField.getText())) {
                System.out.println("Login successful!");
                primaryStage.setScene(createTransactionScene());
            } else {
                System.out.println("Login failed. User not found.");
            }
            usernameField.clear();
            passwordField.clear();
        });

        exitButton.setOnAction(e -> primaryStage.setScene(createMainScene()));

        loginLayout.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton, exitButton
        );

        return new Scene(loginLayout, 400, 400);
    }

    protected Scene createTransactionScene() {
        VBox transactionLayout = new VBox(10);
        transactionLayout.setPadding(new Insets(20));

        this.balanceLabel = new Label("Balance: D" + balance);
        TextField amountField = new TextField();
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        TextField transferUserField = new TextField();
        TextField transferAmountField = new TextField();
        Button transferButton = new Button("Transfer");
        Button logoutButton = new Button("Logout");


        depositButton.setOnAction(e -> {
            depositMoney(amountField.getText());
            amountField.clear();
        });
        withdrawButton.setOnAction(e -> {
            withdrawMoney(amountField.getText());
            amountField.clear();
        });
        transferButton.setOnAction(e -> {
            transferMoney(transferUserField.getText(), transferAmountField.getText());
            transferAmountField.clear();
            transferUserField.clear();
        });
        logoutButton.setOnAction(e -> {
            // Switch back to the main scene (logout)
            primaryStage.setScene(createMainScene());
            loggedInUser = null;
        });

        transactionLayout.getChildren().addAll(
                balanceLabel, amountField, depositButton,
                withdrawButton, new Label("Transfer to (telephone #):"), transferUserField,
                new Label("Amount:"), transferAmountField, transferButton, logoutButton
        );

        return new Scene(transactionLayout, 500, 400);
    }


    private boolean registerUser(String username, String password, String telephone) {
        if(username.equals("") || password.equals("") || telephone.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Registration failed");
            alert.setContentText("All fields are required" );
            alert.showAndWait();
            return false;
        }

        User newUser = new User(username, password, telephone);
        users.add(newUser);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Registered");
        alert.setContentText("User "+ newUser.getUsername() + " registered successfully" );
        alert.showAndWait();


        return true;
    }

    private boolean validateLogin(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedInUser = user;
                this.balance = loggedInUser.getBalance();
                System.out.println("User logged in: " + loggedInUser);
                updateLabels();
                return true; // User found
            }
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("User not found");
        alert.setContentText("User not in DB Or Invalid credentials" );
        alert.showAndWait();
        return false; // User not found
    }

    private void depositMoney(String amount) {
        if (isValidAmount(amount)) {
            double depositAmount = Double.parseDouble(amount);
            loggedInUser.deposit(depositAmount);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Deposited");
            alert.setContentText("Deposited D" + depositAmount);
            alert.showAndWait();
            updateLabels();
        }
    }

    private void withdrawMoney(String amount) {
        if (isValidAmount(amount)) {
            double withdrawAmount = Double.parseDouble(amount);
            if (loggedInUser.withdraw(withdrawAmount)) {
                System.out.println("Withdrawn D" + withdrawAmount);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Deposited");
                alert.setContentText("Withdrawn D" + withdrawAmount);
                alert.showAndWait();
                updateLabels();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Insufficient funds");
                alert.setContentText("You don't have enough funds");
                alert.showAndWait();
            }
        }
    }

    private void transferMoney(String telephone, String amount) {
        if (isValidAmount(amount)) {
            double transferAmount = Double.parseDouble(amount);
            User recipient = findUserByTelephone(telephone);
            if (recipient != null && loggedInUser.transfer(recipient, transferAmount)) {
                System.out.println("Transferred D" + transferAmount + " to " + recipient.getUsername());
                updateLabels();
            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid recipient");
                alert.setContentText("Invalid recipient or insufficient funds");
                alert.showAndWait();
            }
        }
    }

    private void updateLabels() {
        primaryStage.setTitle("Money Transfer App - " + loggedInUser.getUsername());
        balanceLabel.setText("Balance: D" + loggedInUser.getBalance());
    }


    private User findUserByTelephone(String telephone) {
        for (User user : users) {
            if (user.getTelephone().equals(telephone)) {
                return user;
            }
        }
        return null;
    }

    private boolean isValidAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount");
            return false;
        }
    }
}
