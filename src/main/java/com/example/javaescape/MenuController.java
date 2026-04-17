package com.example.javaescape;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class MenuController {

    @FXML
    void onDemarrer(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogueDebut.fxml"));//ps oubl de mettre en camelcase
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @FXML
    void onReprendre(ActionEvent event) throws Exception {
        //+tard a faire sauvegarde
    }

    @FXML
    void onQuitter(ActionEvent event) {
        System.exit(0);//rev src baeldung
    }

}
