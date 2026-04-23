package com.example.javaescape;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button btnResumer;

    @FXML
    public void initialize() {
        if (!SaveManager.saveExists()) {
            btnResumer.setDisable(true);
        }
    }

    @FXML
    void onDemarrer(ActionEvent event) throws Exception {
        SaveManager.deleteSave();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogueDebut.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @FXML
    void onReprendre(ActionEvent event) throws Exception {
        SaveManager.GameSave save = SaveManager.loadGame();

        if (save == null) {
            System.err.println("Aucune sauvegarde trouvée");
            return;
        }

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader;

        // Gestion des saves a load
        switch (save.currentScene) {
            case "dialogueDebut":
                loader = new FXMLLoader(getClass().getResource("dialogueDebut.fxml"));
                // Charger d'abord
                Scene sceneDebut = new Scene(loader.load());
                // Puis récupérer le contrôleur et charger la save
                dialogueDebutController controller = loader.getController();
                controller.loadFromSave(save);
                stage.setScene(sceneDebut);
                break;

            case "quizz":
                loader = new FXMLLoader(getClass().getResource("quizz.fxml"));
                Scene sceneQuizz = new Scene(loader.load());
                quizzController quizzCtrl = loader.getController();
                quizzCtrl.loadFromSave(save);
                stage.setScene(sceneQuizz);
                break;

            case "dialogueIntermediaire":
                loader = new FXMLLoader(getClass().getResource("dialogueIntermediaire.fxml"));
                Scene sceneInter = new Scene(loader.load());
                dialogueIntermediaireController interCtrl = loader.getController();
                interCtrl.loadFromSave(save);
                stage.setScene(sceneInter);
                break;

            case "MasterMind":
                loader = new FXMLLoader(getClass().getResource("MasterMind.fxml"));
                Scene sceneMM = new Scene(loader.load());
                MasterMindController mmController = loader.getController();
                mmController.loadFromSave(save);
                stage.setScene(sceneMM);
                break;

            case "dialogueFinVictoire":
                loader = new FXMLLoader(getClass().getResource("dialogueFinVictoire.fxml"));
                Scene sceneVictoire = new Scene(loader.load());
                dialogueFinVictoireController vicController = loader.getController();
                vicController.loadFromSave(save);
                stage.setScene(sceneVictoire);
                break;

            case "dialogueFinDefaite":
                loader = new FXMLLoader(getClass().getResource("dialogueFinDefaite.fxml"));
                Scene sceneDefaite = new Scene(loader.load());
                dialogueFinDefaiteController defController = loader.getController();
                defController.loadFromSave(save);
                stage.setScene(sceneDefaite);
                break;

            default:
                System.err.println("Scène introuvable : " + save.currentScene);
                return;
        }

        stage.show();
    }

    @FXML
    void onQuitter(ActionEvent event) {
        System.exit(0);
    }
}