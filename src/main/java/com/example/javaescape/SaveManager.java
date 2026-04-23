package com.example.javaescape;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.dat"; //save dans un fichier .dat

    public static class GameSave implements Serializable {
        private static final long serialVersionUID = 1L;

        public String currentScene;
        public int dialogueIndex;
        public boolean dialogueFinished;

        // Pour MasterMind
        public List<Character> secretCode;
        public int currentTry;
        public List<List<Character>> history;
        public List<FeedbackData> feedbacks;

        // Pour Quizz
        public List<QuestionData> quizzQuestions; // Toutes les questions du quiz
        public int quizzIndex; // Index de la question actuelle
        public int quizzScore; // Score actuel
        public boolean quizzAnswered; // Si l'utilisateur a déjà répondu à la question actuelle
        public String quizzCurrentFeedback; // Le feedback affiché
        public List<String> quizzCurrentAnswers; // Les 4 réponses mélangées de la question actuelle

        public GameSave() {
            this.history = new ArrayList<>();
            this.feedbacks = new ArrayList<>();
            this.quizzQuestions = new ArrayList<>();
            this.quizzCurrentAnswers = new ArrayList<>();
        }
    }

    // Classe pour sauvegarder les feedbacks (indices de placement)
    public static class FeedbackData implements Serializable {
        private static final long serialVersionUID = 1L;
        public int correctPosition;
        public int correctLetter;

        public FeedbackData(int correctPosition, int correctLetter) {
            this.correctPosition = correctPosition;
            this.correctLetter = correctLetter;
        }
    }

    // Classe pour sauvegarder les questions du quiz
    public static class QuestionData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String question;
        public String correct;
        public List<String> incorrect;

        public QuestionData(String question, String correct, List<String> incorrect) {
            this.question = question;
            this.correct = correct;
            this.incorrect = new ArrayList<>(incorrect);
        }
    }

    public static void saveGame(GameSave save) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(save);
            System.out.println("Partie sauvegardée | Scene: " + save.currentScene + ", Dialogue: " + save.dialogueIndex);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static GameSave loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            GameSave save = (GameSave) ois.readObject();
            System.out.println("Partie chargée | Scene: " + save.currentScene + ", Dialogue: " + save.dialogueIndex);
            return save;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteSave() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("Sauvegarde supprimée");
        }
    }

    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }
}