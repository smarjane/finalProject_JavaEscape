package com.example.javaescape;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MasterMindController {

    private enum GameState {
        // State pour le bouton reset
        PLAYING,
        VICTORY,
        DEFEAT
    }

    private GameState gameState;

    @FXML
    private GridPane historyGrid; // La première grille (liste des essais)

    @FXML
    private GridPane currentGuessGrid; // La grille du milieu (la sélection en cours)

    @FXML
    private GridPane buttonGrid; // Les boutons des lettres

    @FXML
    private Label remainingTriesLabel; // Label pour afficher les essais restants

    @FXML
    private Label correctPositionLabel; // Label pour les lettres bien placées

    @FXML
    private Label correctLetterLabel; // Label pour les lettres correctes

    @FXML
    private Button buttonA, buttonB, buttonC, buttonD;

    @FXML
    private Button submitButton, resetButton;

    private List<Character> secretCode;
    private List<Character> currentGuess;
    private int currentTry;
    private static final int maxRetry = 10;
    private static final int codeLength = 4;
    private static final char[] listLetters = {'A', 'B', 'C', 'D'};

    @FXML
    public void initialize() {
        initializeGame();
        setupButtons();
    }

    private void initializeGame() {
        // Initialiser
        secretCode = generateSecretCode();
        currentGuess = new ArrayList<>();
        currentTry = 0;

        gameState = GameState.PLAYING;

        // Initialise l'affichage
        updateRemainingTries();
        clearCurrentGuess();
        clearFeedback();
        clearHistory();
        resetButton.setText("Reset");
        enableButtons();

        System.out.println("Code : " + secretCode); // affiche la solution en terminal, juste pour le debug ou si on est bloqué
    }

    private List<Character> generateSecretCode() {
        // Génère le code aléatoirement
        Random random = new Random();
        List<Character> code = new ArrayList<>();
        for (int i = 0; i < codeLength; i++) {
            code.add(listLetters[random.nextInt(listLetters.length)]);
        }
        return code;
    }

    private void setupButtons() {
        // Initialise les boutons du jeux avec leur fonction
        buttonA.setOnAction(e -> addLetterToGuess('A'));
        buttonB.setOnAction(e -> addLetterToGuess('B'));
        buttonC.setOnAction(e -> addLetterToGuess('C'));
        buttonD.setOnAction(e -> addLetterToGuess('D'));
        resetButton.setOnAction(e -> handleResetButton());
        submitButton.setOnAction(e -> submitGuess());
    }

    private void addLetterToGuess(char letter) {
        // On ajoute à la selection actuelle la lettre cliquée
        if (currentGuess.size() < codeLength && currentTry < maxRetry) {
            currentGuess.add(letter);
            updateCurrentGuessDisplay();
        }
    }

    private void updateCurrentGuessDisplay() {
        // On affiche la sélection actuelle dans la grid du milieu
        for (int i = 0; i < codeLength; i++) {
            Label label = (Label) getNodeFromGridPane(currentGuessGrid, i, 0);
            if (label != null) {
                if (i < currentGuess.size()) {
                    label.setText(String.valueOf(currentGuess.get(i)));
                    label.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                } else {
                    label.setText("");
                    label.setStyle("-fx-border-color: black;");
                }
            }
        }
    }


    private void resetCurrentSelection() {
        // Juste supprime la séléction actuelle
        currentGuess.clear();
        updateCurrentGuessDisplay();
    }

    private void handleResetButton() {
        // On check l'état du bouton delete pour lui assigner une action en fonction
        switch (gameState) {
            case PLAYING:
                // reset la sélection en cours
                resetCurrentSelection();
                break;

            case DEFEAT:
                // Reset complet du jeu quand on a perdu
                initializeGame();
                break;

            case VICTORY:
                System.out.println("Suivant"); //Temporaire, après on mettera la suite des dialogues
                break;
        }
    }

    private void submitGuess() {
        // On envoie la séléction actuelle

        // Ici on vérif que la sélection est complète sinon on ne fait rien
        if (currentGuess.size() != codeLength) {
            return;
        }

        if (currentTry >= maxRetry) {
            return;
        }

        // Initialise les feedback (indices de placement)
        int correctPosition = 0;
        int correctLetter = 0;

        List<Character> secretCopy = new ArrayList<>(secretCode);
        List<Character> guessCopy = new ArrayList<>(currentGuess);

        // On compte les lettres bien placées
        for (int i = 0; i < codeLength; i++) {
            if (currentGuess.get(i).equals(secretCode.get(i))) {
                correctPosition++;
                secretCopy.set(i, null);
                guessCopy.set(i, null);
            }
        }

        // On compte les lettres correctes mais mal placées
        for (int i = 0; i < codeLength; i++) {
            if (guessCopy.get(i) != null) {
                for (int j = 0; j < codeLength; j++) {
                    if (secretCopy.get(j) != null && guessCopy.get(i).equals(secretCopy.get(j))) {
                        correctLetter++;
                        secretCopy.set(j, null);
                        break;
                    }
                }
            }
        }

        // Puis on fait le  total de lettres bien ou mal placées
        int totalCorrect = correctPosition + correctLetter;

        // On affiche dans l'historique la combi envoyée + son num d'essai
        displayInHistory(currentTry, currentGuess);

        // On affiche le feedback avec son texte
        correctPositionLabel.setText("Bien placées :\n" + correctPosition);
        correctLetterLabel.setText("Lettres correcte :\n" + totalCorrect);

        currentTry++;

        // Verif victoire
        if (correctPosition == codeLength) {
            gameState = GameState.VICTORY;

            // On set son texte a afficher + rend jolie
            remainingTriesLabel.setText("VICTOIRE !");
            remainingTriesLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            // Et on transfo le bouton reset en suivant pour afficher la suite
            resetButton.setText("Suivant");
            disableButtons(); // On desac aussi les autres bouton pour éviter les problèmes
            return;
        }

        // Vérif défaite
        if (currentTry >= maxRetry) {
            gameState = GameState.DEFEAT;

            // Pareil : texte + jolie
            remainingTriesLabel.setText("DÉFAITE");
            remainingTriesLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            // Ici on desac juste les boutons, on le change pas (du moins pas le visuel)
            disableButtons();
            return;
        }

        // Enfin on reset la sélection actuelle
        currentGuess.clear();
        updateCurrentGuessDisplay();
        updateRemainingTries();
    }

    private void displayInHistory(int tryNumber, List<Character> guess) {
        // On affiche le num d'essai (col 0 de la grid 1)
        Label tryLabel = (Label) getNodeFromGridPane(historyGrid, 0, tryNumber);
        if (tryLabel != null) {
            tryLabel.setText(String.valueOf(tryNumber + 1));
            tryLabel.setStyle("-fx-font-weight: bold;");
        }

        // + affiche la combinaison (sur les 4 autres cols)
        for (int i = 0; i < codeLength; i++) {
            Label label = (Label) getNodeFromGridPane(historyGrid, i + 1, tryNumber);
            if (label != null) {
                label.setText(String.valueOf(guess.get(i)));
                label.setStyle("-fx-background-color: lightgray;");
            }
        }
    }

    private void updateRemainingTries() {
        // On met a jour le nombre d'essaie restant
        int remaining = maxRetry - currentTry;
        remainingTriesLabel.setText("Essais restants: " + remaining);
        remainingTriesLabel.setStyle("-fx-font-weight: bold;");
    }

    private void clearCurrentGuess() {
        // On supprime la sélection actuelle
        for (int i = 0; i < codeLength; i++) {
            Label label = (Label) getNodeFromGridPane(currentGuessGrid, i, 0);
            if (label != null) {
                label.setText("");
                label.setStyle("-fx-border-color: black;");
            }
        }
    }

    private void clearFeedback() {
        // On met clear les feedback (set en valeur par défaut pour le lancement ou reset du jeu)
        correctPositionLabel.setText("Bien placées :\n");
        correctLetterLabel.setText("Lettres correcte :\n");
    }

    private void clearHistory() {
        // On clear l'history des sélection (set en valeur par défaut pour le lancement ou reset du jeu)
        for (int row = 0; row < maxRetry; row++) {
            for (int col = 0; col < 5; col++) {
                Label label = (Label) getNodeFromGridPane(historyGrid, col, row);
                if (label != null) {
                    label.setText("");
                    label.setStyle("");
                }
            }
        }
    }

    private void disableButtons() {
        // On désac les boutons pour la fin de game
        buttonA.setDisable(true);
        buttonB.setDisable(true);
        buttonC.setDisable(true);
        buttonD.setDisable(true);
        submitButton.setDisable(true);
    }

    private void enableButtons() {
        // On les re activent en cas de restart
        buttonA.setDisable(false);
        buttonB.setDisable(false);
        buttonC.setDisable(false);
        buttonD.setDisable(false);
        submitButton.setDisable(false);
    }

    // Méthode utilitaire pour récupérer une coordonnée d'une case d'un GridPane pour faire des sélections ciblée
    private javafx.scene.Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == null) nodeRow = 0;

            if (nodeCol == col && nodeRow == row) {
                return node;
            }
        }
        return null;
    }
}