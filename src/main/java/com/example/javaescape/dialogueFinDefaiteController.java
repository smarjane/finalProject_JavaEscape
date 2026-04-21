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

public class dialogueFinDefaiteController { //rev meme corps de dialogeuDebut

    @FXML private ImageView imageFin;
    @FXML private TextArea zoneTexte;

    private List<String> dialogues;
    private int index = 0;
    private Timeline timeline;
    private boolean isWriting = false;

    @FXML
    public void initialize() {

        imageFin.setImage( //img defaite
                new Image(getClass().getResource("/com/example/javaescape/bombeExplose.jpg").toExternalForm())
        );

        dialogues = List.of(
                "L'échec est total. La bombe a explosé.",
                "Le Chef, qui avait placé toute sa confiance en toi, est mort dans l'explosion.",
                "La ville a été détruite. Des vies ont été perdues. Tout est fini.",
                "On est tous très déçu !"
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
            return; // avant c'etait retourMenu() mais mtn on retourne au menu que via le bouton
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
