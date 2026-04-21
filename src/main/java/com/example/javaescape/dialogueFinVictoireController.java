package com.example.javaescape;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class dialogueFinVictoireController {

    @FXML private ImageView imageFin;
    @FXML private TextArea zoneTexte;

    private List<String> dialogues;
    private int index = 0;
    private Timeline timeline;
    private boolean isWriting = false;

    @FXML
    public void initialize() {

        imageFin.setImage( //img de fin
                new Image(getClass().getResource("/com/example/javaescape/bombeDesamorce.png").toExternalForm())
        );

        dialogues = List.of(
                "Chef : Tu l'as fait… Tu as réussi à désamorcer la bombe et à sauver la ville. Je savais que tu en étais capable.",
                "Chef : Grâce à toi, des vies ont été sauvées aujourd’hui. Tu as fait preuve de courage, d'intelligence et de détermination dans chaque étape de cette mission. Tu as tout donné, et ça a payé.",
                "Chef : Bien joué, vraiment. Tu as prouvé qu’il n’y a rien que tu ne puisses accomplir. Je n’oublierai jamais ce jour."
        );

        afficherDialogue();

        zoneTexte.setFocusTraversable(true);
        zoneTexte.requestFocus();

        zoneTexte.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                passerDialogue();
            }
        });
    }

    private void afficherDialogue() {
        zoneTexte.clear();
        ecrireLettreParLettre(dialogues.get(index));
    }

    private void ecrireLettreParLettre(String texte) {
        isWriting = true;
        zoneTexte.clear();

        final int[] i = {0};

        timeline = new Timeline(
                new KeyFrame(Duration.millis(30), event -> {
                    if (i[0] < texte.length()) {
                        zoneTexte.appendText(String.valueOf(texte.charAt(i[0])));
                        i[0]++;
                    } else {
                        isWriting = false;
                        timeline.stop();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void passerDialogue() {
        if (isWriting) {
            timeline.stop();
            zoneTexte.setText(dialogues.get(index));
            isWriting = false;
            return;
        }

        index++;

        if (index < dialogues.size()) {
            afficherDialogue();
        } else {
            retourMenu();
        }
    }

    @FXML
    private void retourMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            Stage stage = (Stage) zoneTexte.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
