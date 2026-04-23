package com.example.javaescape;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.util.*;

public class quizzController {

    @FXML private Label questionLabel;
    @FXML private Label feedbackLabel;

    @FXML private Button answerBtn1;
    @FXML private Button answerBtn2;
    @FXML private Button answerBtn3;
    @FXML private Button answerBtn4;
    @FXML private Button nextButton;

    @FXML private Label scoreLabel;
    @FXML private Label questionCountLabel;

    private List<Question> questions = new ArrayList<>();
    private int index = 0;
    private int score = 0;
    private boolean aRepondu = false;
    private boolean loadedFromSave = false;
    private List<String> currentShuffledAnswers = new ArrayList<>();

    @FXML
    public void initialize() {
        // Configurer les boutons
        answerBtn1.setOnAction(e -> verifierReponse(answerBtn1.getText()));
        answerBtn2.setOnAction(e -> verifierReponse(answerBtn2.getText()));
        answerBtn3.setOnAction(e -> verifierReponse(answerBtn3.getText()));
        answerBtn4.setOnAction(e -> verifierReponse(answerBtn4.getText()));
        nextButton.setOnAction(e -> nextQuestion());

        // Ne charger les questions que si on ne vient pas d'une sauvegarde
        if (!loadedFromSave) {
            chargerQuestionsDepuisAPI();
            afficherQuestion();
        }
    }

    // Charger depuis une sauvegarde
    public void loadFromSave(SaveManager.GameSave save) {
        loadedFromSave = true;

        System.out.println("Chargement quiz - index: " + save.quizzIndex + ", score: " + save.quizzScore);

        // Restaurer les questions
        questions.clear();
        for (SaveManager.QuestionData qData : save.quizzQuestions) {
            questions.add(new Question(qData.question, qData.correct, qData.incorrect));
        }

        this.index = save.quizzIndex;
        this.score = save.quizzScore;
        this.aRepondu = save.quizzAnswered;
        this.currentShuffledAnswers = new ArrayList<>(save.quizzCurrentAnswers);

        javafx.application.Platform.runLater(() -> {
            afficherQuestionSauvegardee(save);
        });
    }

    private void afficherQuestionSauvegardee(SaveManager.GameSave save) {
        Question q = questions.get(index);

        questionLabel.setText(q.question);
        mettreAJourScore();

        // Restaurer les réponses dans le même ordre
        answerBtn1.setText(currentShuffledAnswers.get(0));
        answerBtn2.setText(currentShuffledAnswers.get(1));
        answerBtn3.setText(currentShuffledAnswers.get(2));
        answerBtn4.setText(currentShuffledAnswers.get(3));

        if (aRepondu) {
            // Si déjà répondu, afficher le feedback et désactiver les boutons
            feedbackLabel.setText(save.quizzCurrentFeedback);
            feedbackLabel.setVisible(true);
            nextButton.setVisible(true);
            enableButtons(false);

            // Restaurer la couleur du feedback
            if (save.quizzCurrentFeedback != null && save.quizzCurrentFeedback.startsWith("Correct")) {
                feedbackLabel.setStyle("-fx-text-fill: green;");
            } else {
                feedbackLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            // Question pas encore répondue
            feedbackLabel.setVisible(false);
            nextButton.setVisible(false);
            enableButtons(true);
        }
    }

    private String decodeHtml(String text) {
        return text
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    private void chargerQuestionsDepuisAPI() {
        try {
            URL url = new URL("https://opentdb.com/api.php?amount=20&type=multiple");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONObject obj = new JSONObject(json.toString());
            JSONArray results = obj.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject q = results.getJSONObject(i);

                String question = decodeHtml(q.getString("question"));
                String correct = decodeHtml(q.getString("correct_answer"));

                JSONArray incorrect = q.getJSONArray("incorrect_answers");
                List<String> incorrectList = new ArrayList<>();
                for (int j = 0; j < incorrect.length(); j++) {
                    incorrectList.add(decodeHtml(incorrect.getString(j)));
                }

                questions.add(new Question(question, correct, incorrectList));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mettreAJourScore() {
        scoreLabel.setText(score + "/5");
        questionCountLabel.setText((index + 1) + "/20");
    }

    private void afficherQuestion() {
        aRepondu = false;
        feedbackLabel.setVisible(false);
        nextButton.setVisible(false);

        Question q = questions.get(index);

        questionLabel.setText(q.question);

        mettreAJourScore();

        List<String> reponses = new ArrayList<>();
        reponses.add(q.correct);
        reponses.addAll(q.incorrect);

        Collections.shuffle(reponses);

        // Sauvegarder l'ordre mélangé
        currentShuffledAnswers.clear();
        currentShuffledAnswers.addAll(reponses);

        answerBtn1.setText(reponses.get(0));
        answerBtn2.setText(reponses.get(1));
        answerBtn3.setText(reponses.get(2));
        answerBtn4.setText(reponses.get(3));

        enableButtons(true);

        saveGame();
    }

    private void verifierReponse(String reponseChoisie) {
        if (aRepondu) return;
        aRepondu = true;

        Question q = questions.get(index);

        if (reponseChoisie.equals(q.correct)) {
            score++;
            feedbackLabel.setText("Correct!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
        } else {
            feedbackLabel.setText("Incorrect! Correct answer: " + q.correct);
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }

        feedbackLabel.setVisible(true);
        nextButton.setVisible(true);
        enableButtons(false);

        saveGame();
    }

    private void nextQuestion() {
        index++;

        if (score >= 5) {
            saveGame();
            allerDialogueInter();
            return;
        }

        if (index >= 20) {
            saveGame();
            allerDialogueDefaite();
            return;
        }

        afficherQuestion();
    }

    private void enableButtons(boolean enable) {
        answerBtn1.setDisable(!enable);
        answerBtn2.setDisable(!enable);
        answerBtn3.setDisable(!enable);
        answerBtn4.setDisable(!enable);
    }

    private void allerDialogueInter() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dialogueIntermediaire.fxml"));
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void allerDialogueDefaite() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dialogueFinDefaite.fxml"));
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        SaveManager.GameSave save = new SaveManager.GameSave();
        save.currentScene = "quizz";
        save.dialogueIndex = 0;

        // Sauvegarder les questions
        save.quizzQuestions = new ArrayList<>();
        for (Question q : questions) {
            save.quizzQuestions.add(new SaveManager.QuestionData(q.question, q.correct, q.incorrect));
        }

        save.quizzIndex = this.index;
        save.quizzScore = this.score;
        save.quizzAnswered = this.aRepondu;
        save.quizzCurrentFeedback = feedbackLabel.getText();
        save.quizzCurrentAnswers = new ArrayList<>(this.currentShuffledAnswers);

        SaveManager.saveGame(save);
    }

    private static class Question {
        String question;
        String correct;
        List<String> incorrect;

        Question(String question, String correct, List<String> incorrect) {
            this.question = question;
            this.correct = correct;
            this.incorrect = incorrect;
        }
    }
}