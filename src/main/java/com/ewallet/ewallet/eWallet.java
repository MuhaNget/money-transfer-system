package com.ewallet.ewallet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class eWallet extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Money Transfer App");
        var wallet = new WalletController(primaryStage);

        // Create scenes
        Scene mainScene = wallet.createMainScene();
        Scene registerScene = wallet.createRegisterScene();
        Scene loginScene = wallet.createLoginScene();
        Scene transactionScene = wallet.createTransactionScene();

        primaryStage.setScene(mainScene);

        primaryStage.show();
    }

}


