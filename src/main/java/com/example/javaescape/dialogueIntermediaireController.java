package com.example.javaescape;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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

public class dialogueIntermediaireController {

    @FXML private ImageView imagePersonnage;
    @FXML private TextArea zoneTexte;
    @FXML private javafx.scene.control.Button btnSuivant;

    private List<String> dialogues;
    private int index = 0;

    private Timeline timeline;
    private boolean isWriting = false;
    private boolean loadedFromSave = false; // Important !

    @FXML
    public void initialize() {
        btnSuivant.setVisible(false);

        imagePersonnage.setImage(
                new Image(getClass().getResource("/com/example/javaescape/chef.jpg").toExternalForm())
        );

        dialogues = List.of(
                "Chef : Bien joué. Tu as bien avancé jusqu'ici. Tu as résolu toutes les énigmes, et maintenant, nous avons une meilleure idée de l'endroit où la bombe pourrait être. Mais la tâche n'est pas encore terminée.",
                "Chef : Maintenant, il te faut localiser l'emplacement exact. Pour cela, tu vas interroger des suspects. Certains te diront la vérité, d'autres mentiront. Ce sera à toi de discerner qui est fiable et qui ne l'est pas.",
                "Chef : Le temps presse. Chaque erreur pourrait nous coûter cher, alors fais attention. Nous n'avons pas de marge pour les hésitations. Trouve où elle se cache, et on pourra passer à l'étape suivante.",
                "Chef : Tu es notre seul espoir, et je sais que tu peux le faire. Trouve la bombe, localise-la avec précision. C'est à toi de mener cette mission à bien.",
                "Chef : Allez, il ne reste plus beaucoup de temps. Trouve cette bombe, et sauve tout le monde."
        );

        // Ne démarrer que si on ne charge pas depuis une sauvegarde
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

    // Charger depuis une sauvegarde
    public void loadFromSave(SaveManager.GameSave save) {
        loadedFromSave = true;
        this.index = save.dialogueIndex;

        //Debug
        System.out.println("Chargement dialogue | index: " + index + ", terminé: " + save.dialogueFinished);

        if (timeline != null) {
            timeline.stop();
        }
        isWriting = false;

        Platform.runLater(() -> {
            if (save.dialogueFinished || index >= dialogues.size()) {
                btnSuivant.setVisible(true);
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
        if (timeline != null) {
            timeline.stop();
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("MasterMind.fxml"));
        Parent root = loader.load();

        MasterMindController controller = loader.getController();
        controller.initializeGame(); // nouvelle partie

        Stage stage = (Stage) btnSuivant.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    private void saveGame() {
        SaveManager.GameSave save = new SaveManager.GameSave();
        save.currentScene = "dialogueIntermediaire";
        save.dialogueIndex = this.index;
        save.dialogueFinished = (this.index >= dialogues.size());
        SaveManager.saveGame(save);
    }
}