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

public class dialogueDebutController {

    @FXML private ImageView imagePersonnage;
    @FXML private TextArea zoneTexte;
    @FXML private javafx.scene.control.Button btnSuivant;

    private List<String> dialogues;
    private int index = 0;

    private Timeline timeline;
    private boolean isWriting = false;
    private boolean loadedFromSave = false;

    @FXML
    public void initialize() {
        btnSuivant.setVisible(false);

        imagePersonnage.setImage(
                new Image(getClass().getResource("/com/example/javaescape/chef.jpg").toExternalForm())
        );

        dialogues = List.of(
                "Chef : Écoute-moi bien. Une bombe a été placée quelque part en ville, et tout repose sur toi. Nous n'avons pas de temps à perdre. Chaque seconde compte.",
                "Chef : Voici la situation : tu vas devoir résoudre une série d'énigmes. Chacune te donnera des indices pour localiser la bombe. Le temps presse, mais nous avons encore une chance si tu agis rapidement et avec précision.",
                "Chef : Je sais que ce n'est pas facile, mais je crois en toi. Nous avons les outils nécessaires, et tu as l'intelligence pour déchiffrer ces énigmes. Chaque réponse correcte nous rapproche de la solution.",
                "Chef : Ne laisse pas la pression te faire trébucher. Résous les énigmes, trouve l'emplacement de la bombe, et nous pourrons la désamorcer avant qu'il ne soit trop tard. On compte sur toi. La ville compte sur toi."
        );

        // Si pas chargé depuis une sauvegarde, commencer normalement
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

        System.out.println("Chargement dialogue - index: " + index + ", finished: " + save.dialogueFinished);

        // Arrêter toute animation en cours
        if (timeline != null) {
            timeline.stop();
        }
        isWriting = false;

        // Initialiser après que initialize() soit appelé
        javafx.application.Platform.runLater(() -> {
            if (save.dialogueFinished || index >= dialogues.size()) {
                // Tous les dialogues sont terminés
                btnSuivant.setVisible(true);
                // Afficher le dernier dialogue complet
                zoneTexte.clear();
                zoneTexte.setText(dialogues.get(dialogues.size() - 1));
            } else {
                // Afficher le dialogue en cours COMPLET (pas l'effet machine à écrire)
                zoneTexte.clear();
                zoneTexte.setText(dialogues.get(index));
            }
        });
    }

    private void afficherDialogue() {
        // Arrêter toute animation en cours
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
        // Arrêter toute animation en cours
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

    public void passerDialogue() {
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
            btnSuivant.setVisible(true);
            saveGame();
        }
    }

    @FXML
    private void goToGame() throws Exception {
        // Arrêter la timeline avant de changer de scène
        if (timeline != null) {
            timeline.stop();
        }

        Parent root = FXMLLoader.load(getClass().getResource("MasterMind.fxml"));
        Stage stage = (Stage) zoneTexte.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // s
    private void saveGame() {
        SaveManager.GameSave save = new SaveManager.GameSave();
        save.currentScene = "dialogueDebut";
        save.dialogueIndex = this.index;
        save.dialogueFinished = (this.index >= dialogues.size());
        SaveManager.saveGame(save);
    }
}