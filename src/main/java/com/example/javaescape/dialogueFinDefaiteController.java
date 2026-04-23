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

public class dialogueFinDefaiteController {

    @FXML private ImageView imageFin;
    @FXML private TextArea zoneTexte;

    private List<String> dialogues;
    private int index = 0;
    private Timeline timeline;
    private boolean isWriting = false;
    private boolean loadedFromSave = false;

    @FXML
    public void initialize() {
        imageFin.setImage(
                new Image(getClass().getResource("/com/example/javaescape/bombeExplose.jpg").toExternalForm())
        );

        dialogues = List.of(
                "L'échec est total. La bombe a explosé.",
                "Le Chef, qui avait placé toute sa confiance en toi, est mort dans l'explosion.",
                "La ville a été détruite. Des vies ont été perdues. Tout est fini.",
                "On est tous très déçu !"
        );

        if (!loadedFromSave) {
            afficherDialogue();
        }

        zoneTexte.setFocusTraversable(true);
        zoneTexte.requestFocus();

        zoneTexte.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                passerDialogue();
            }
        });
    }

    public void loadFromSave(SaveManager.GameSave save) {
        loadedFromSave = true;
        this.index = save.dialogueIndex;

        // Arrêter toute animation en cours
        if (timeline != null) {
            timeline.stop();
        }
        isWriting = false;

        javafx.application.Platform.runLater(() -> {
            if (index >= dialogues.size()) {
                zoneTexte.clear();
                zoneTexte.setText(dialogues.get(dialogues.size() - 1));
            } else {
                zoneTexte.clear();
                zoneTexte.setText(dialogues.get(index));
            }
        });
    }

    private void afficherDialogue() {
        if (timeline != null) {
            timeline.stop();
        }

        if (index < dialogues.size()) {
            zoneTexte.clear();
            ecrireLettreParLettre(dialogues.get(index));
            saveGame();
        }
    }

    private void ecrireLettreParLettre(String texte) {
        if (timeline != null) {
            timeline.stop();
        }

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
            zoneTexte.clear();
            zoneTexte.setText(dialogues.get(index));
            isWriting = false;
            return;
        }

        index++;

        if (index < dialogues.size()) {
            afficherDialogue();
        } else {
            saveGame();
        }
    }

    @FXML
    private void retourMenu() {
        try {
            if (timeline != null) {
                timeline.stop();
            }

            SaveManager.deleteSave();
            Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            Stage stage = (Stage) zoneTexte.getScene().getWindow();

            Scene scene = new Scene(root, 1200, 800);

            // Recharger le CSS
            scene.getStylesheets().add(
                    getClass().getResource("/styles/menu.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        SaveManager.GameSave save = new SaveManager.GameSave();
        save.currentScene = "dialogueFinDefaite";
        save.dialogueIndex = this.index;
        SaveManager.saveGame(save);
    }
}