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
        // Désactiver le bouton "Reprendre" s'il n'y a pas de sauvegarde
        if (!SaveManager.saveExists()) {
            btnResumer.setDisable(true);
        }
    }

    @FXML
    void onDemarrer(ActionEvent event) throws Exception {
        // Supprimer la sauvegarde existante pour une nouvelle partie
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

        // Charger la scène appropriée en fonction de la sauvegarde
        switch (save.currentScene) {
            case "dialogueDebut":
                loader = new FXMLLoader(getClass().getResource("dialogueDebut.fxml"));
                stage.setScene(new Scene(loader.load()));
                dialogueDebutController controller = loader.getController();
                controller.loadFromSave(save);
                break;

            case "MasterMind":
                loader = new FXMLLoader(getClass().getResource("MasterMind.fxml"));
                stage.setScene(new Scene(loader.load()));
                MasterMindController mmController = loader.getController();
                mmController.loadFromSave(save);
                break;

            case "dialogueFinVictoire":
                loader = new FXMLLoader(getClass().getResource("dialogueFinVictoire.fxml"));
                stage.setScene(new Scene(loader.load()));
                dialogueFinVictoireController vicController = loader.getController();
                vicController.loadFromSave(save);
                break;

            case "dialogueFinDefaite":
                loader = new FXMLLoader(getClass().getResource("dialogueFinDefaite.fxml"));
                stage.setScene(new Scene(loader.load()));
                dialogueFinDefaiteController defController = loader.getController();
                defController.loadFromSave(save);
                break;

            default:
                System.err.println("Scène inconnue : " + save.currentScene);
                return;
        }

        stage.show();
    }

    @FXML
    void onQuitter(ActionEvent event) {
        System.exit(0);
    }
}