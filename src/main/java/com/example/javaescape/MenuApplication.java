package com.example.javaescape;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("menu.fxml"));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1200, 800); //IA

        stage.setTitle("Escape Game");
        stage.setScene(scene);
        stage.setResizable(false);

        scene.getStylesheets().add(//rev cloudDevs
                MenuApplication.class.getResource("/styles/menu.css").toExternalForm());

        stage.show();
    }


}
