package com.example.javaescape;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.dat";

    public static class GameSave implements Serializable {
        private static final long serialVersionUID = 1L;

        public String currentScene;
        public int dialogueIndex;
        public boolean dialogueFinished; // Pour savoir si tous les dialogues sont terminés

        // Pour MasterMind
        public List<Character> secretCode;
        public int currentTry;
        public List<List<Character>> history;
        public List<FeedbackData> feedbacks; // pour sauvegarder les feedbacks

        public GameSave() {
            this.history = new ArrayList<>();
            this.feedbacks = new ArrayList<>();
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

    public static void saveGame(GameSave save) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(save);
            System.out.println("Partie sauvegardée - Scene: " + save.currentScene + ", Dialogue: " + save.dialogueIndex);
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