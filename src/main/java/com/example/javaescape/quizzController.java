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

    @FXML
    public void initialize() {

        chargerQuestionsDepuisAPI();
        afficherQuestion();

        answerBtn1.setOnAction(e -> verifierReponse(answerBtn1.getText())); //rev javafx en 5 pts src
        answerBtn2.setOnAction(e -> verifierReponse(answerBtn2.getText()));
        answerBtn3.setOnAction(e -> verifierReponse(answerBtn3.getText()));
        answerBtn4.setOnAction(e -> verifierReponse(answerBtn4.getText()));

        nextButton.setOnAction(e -> nextQuestion());
    }

    private String decodeHtml(String text) {//CODE IA caractere HTML et rev src DelftStack src
        return text
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    private void chargerQuestionsDepuisAPI() {
        try { //corps API rev javaspring.net src
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

    private void mettreAJourScore () {
        scoreLabel.setText(score + "/5"); //nbr bonne rep
        questionCountLabel.setText((index + 1) + "/20"); //nbr question sur 20
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

        Collections.shuffle(reponses); //rev baeeldung ssrc

        answerBtn1.setText(reponses.get(0));
        answerBtn2.setText(reponses.get(1));
        answerBtn3.setText(reponses.get(2));
        answerBtn4.setText(reponses.get(3));

        enableButtons(true);
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
    }

    private void nextQuestion() {
        index++;

        if (score >= 5) {
            allerDialogueInter();
            return;
        }

        if (index >= 20) {
            allerDialogueDefaite();
            return;
        }

        afficherQuestion();
    }

    private void enableButtons(boolean enable) {
        answerBtn1.setDisable(!enable); //rev codingtechroom src
        answerBtn2.setDisable(!enable);
        answerBtn3.setDisable(!enable);
        answerBtn4.setDisable(!enable);
    }

    private void allerDialogueInter() { //juste appelle dial inter
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dialogueIntermediaire.fxml"));
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void allerDialogueDefaite() { //juste renvoie a dial defaite si perds
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dialogueFinDefaite.fxml"));
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
