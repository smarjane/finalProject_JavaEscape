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
    @FXML private javafx.scene.control.Button btnSuivant; //btn passer au jeu after finish dial

    private List<String> dialogues;//liste contenant dial..
    private int index = 0;

    private Timeline timeline;
    private boolean isWriting = false; //text entrain d'etre ecrit / pas

    @FXML
    public void initialize() {

        btnSuivant.setVisible(false); //btn cacher au deb al a fin du dial afficher

        imagePersonnage.setImage(
                new Image(getClass().getResource("/com/example/javaescape/chef.jpg").toExternalForm())
        );

        dialogues = List.of(
                "Chef : Écoute-moi bien. Une bombe a été placée quelque part en ville, et tout repose sur toi. Nous n'avons pas de temps à perdre. Chaque seconde compte.",
                "Chef : Voici la situation : tu vas devoir résoudre une série d'énigmes. Chacune te donnera des indices pour localiser la bombe. Le temps presse, mais nous avons encore une chance si tu agis rapidement et avec précision.",
                "Chef : Je sais que ce n'est pas facile, mais je crois en toi. Nous avons les outils nécessaires, et tu as l'intelligence pour déchiffrer ces énigmes. Chaque réponse correcte nous rapproche de la solution.",
                "Chef : Ne laisse pas la pression te faire trébucher. Résous les énigmes, trouve l’emplacement de la bombe, et nous pourrons la désamorcer avant qu'il ne soit trop tard. On compte sur toi. La ville compte sur toi."
        );

        afficherDialogue();

        zoneTexte.setFocusTraversable(true); //autorise lien entre zonetext et clavier
        zoneTexte.requestFocus(); //rends elm interactif en liant le la zone de text et le clavier

        zoneTexte.setOnKeyPressed(event -> { //qd user appuyer sur esp => dial suivant
            if (event.getCode() == KeyCode.SPACE) { //rev stackoverflow src
                passerDialogue();
            }
        });
    }

    private void afficherDialogue() {
        zoneTexte.clear();//nettoyage zone pr passer au dial actuelle donc au prochain
        ecrireLettreParLettre(dialogues.get(index));
    }

    private void ecrireLettreParLettre(String texte) { //effet machine a ecrire
        isWriting = true;
        zoneTexte.clear();

        final int[] i = {0}; //conteur de L pr avancer L/ L

        timeline = new Timeline( //rev open.jdk.otg src
                new KeyFrame(Duration.millis(30), event -> {
                    if (i[0] < texte.length()) { //ajoute une L a la x
                        zoneTexte.appendText(String.valueOf(texte.charAt(i[0])));
                        i[0]++;
                    } else {
                        isWriting = false;
                        timeline.stop();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);//demarre animat° et rev bigdev.de src
        timeline.play();
    }

    public void passerDialogue() {
        if (isWriting) {
            timeline.stop();
            zoneTexte.setText(dialogues.get(index));
            isWriting = false;
            return;
        }

        index++;
        if (index < dialogues.size()) { //dial suivant s'il reste
            afficherDialogue();
        } else {
            btnSuivant.setVisible(true); //sinon btn passer au jeu
        }
    }

    @FXML
    private void goToGame() throws Exception { //func appeler quand clique sur btn passer au jeu
        Parent root = FXMLLoader.load(getClass().getResource("MasterMind.fxml"));
        Stage stage = (Stage) zoneTexte.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
