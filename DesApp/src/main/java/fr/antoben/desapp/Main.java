package fr.antoben.desapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main extends Application {

    /**
     * Fichier qui sauvegarde le message crypté. Grâce aux boutons "enregistrer" sur le bouton du
     * téléphone de gauche.
     */
    public static File fileSaveCryp;
    /**
     * Fichier avec un message crypé, qu'on ouvre pour pouvoir le décrypter avec le téléphone de droite.
     */
    public static File fileOpenCryp;
    /**
     * Fenêtre du menu principal du logiciel qui propose l'algorythme de cryptage qui sera
     * utiisé par le téléphone de gauche.
     */
    public static Stage stage;
    /**
     * Fenêtre avec les 2 téléphones.
     * Le premier téléphone permet de cryper en fonction de l'algorythme choisi
     * depuis le menu principal.
     * <p>
     * Le second téléphone permet de décrypter n'importe quel message.
     */
    public static Stage algo;

    /**
     * 2 variables permettant que la fenêtre puisse être bougé à la souris
     */
    public static double xOffsetNW;
    public static double yOffsetNW;

    /**
     * instances pour appeler les fonctions de des.
     */
    public static Des crypteur;
    public static Des décrypteur;
    /**
     * Algorythme choisit
     */
    public static AlgoType algoSelect;

    /**
     * Variable qui contient le texte utilisé pour la fenêtre de notification.
     */
    public static String notifMessage;

    /**
     * Chargement de la fenêtre principal
     *
     * @param stage fenêtre
     */
    @Override
    public void start(Stage stage) {
        Main.stage = stage;
        loadMenuWindow();
        crypteur = new Des();
        décrypteur = new Des();
    }

    /**
     * Fonction auxiliaire pour charger graphiquement la fenêtre principal
     * qui permetrera de choisir l'algorythme pour crypter avec le téléphone de gauche.
     * C'est la première fenêtre qui apparaît à l'écran.
     */
    public static void loadMenuWindow() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("menu.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(Objects.requireNonNull(parent)));
        parent.setOnMousePressed(event -> {
            xOffsetNW = event.getSceneX();
            yOffsetNW = event.getSceneY();
        });
        parent.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffsetNW);
            stage.setY(event.getScreenY() - yOffsetNW);
        });
        stage.setTitle("MenuController");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    /**
     * Fonction auxiliaire pour charger graphiquement la fenêtre avec les 2 téléphones.
     * Cette fenêtre apparaît
     *
     * @param algoType algorythme
     */
    public static void loadAlgoWindow(AlgoType algoType) {
        stage.close();
        algo = new Stage();
        Parent parent = null;
        algoSelect = algoType;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("algo.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        algo.setScene(new Scene(Objects.requireNonNull(parent)));
        parent.setOnMousePressed(event -> {
            xOffsetNW = event.getSceneX();
            yOffsetNW = event.getSceneY();
        });
        parent.setOnMouseDragged(event -> {
            algo.setX(event.getScreenX() - xOffsetNW);
            algo.setY(event.getScreenY() - yOffsetNW);
        });
        algo.setTitle(algoType.name());
        algo.initStyle(StageStyle.UNDECORATED);
        algo.show();
    }

    /**
     * Fonction auxiliaire pour charger graphiquement la fenêtre de notification
     *
     * @param message l'erreur en question
     */
    public static void loadNotif(String message) {
        notifMessage = message;
        Stage notif = new Stage();
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("notif.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        notif.setScene(new Scene(Objects.requireNonNull(parent)));
        parent.setOnMousePressed(event -> {
            xOffsetNW = event.getSceneX();
            yOffsetNW = event.getSceneY();
        });
        parent.setOnMouseDragged(event -> {
            notif.setX(event.getScreenX() - xOffsetNW);
            notif.setY(event.getScreenY() - yOffsetNW);
        });
        notif.setTitle("Notification");
        notif.initStyle(StageStyle.UNDECORATED);
        notif.show();
    }

    /**
     * Fonction auxiliaire pour ouvrir le gestionnaire de fichier et ouvrir un fichier
     */
    static File selectOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un message crypté");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Des", "*.des"));
        return fileChooser.showOpenDialog(algo);
    }

    /**
     * Fonction auxiliaire pour ouvrir le gestionnaire de fichier pour sauvegarder un fichier
     */
    static File selectSaveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder un message crypté");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Des", "*.des"));
        return fileChooser.showSaveDialog(algo);
    }

    /**
     * Fonction auxiliaire pour charger les données depuis un fichier.
     */
    public static int[] loadData() {
        fileOpenCryp = selectOpenFile();
        if (fileOpenCryp == null) {
            loadNotif("Vous n'avez pas sélectionné de fichier contenant un message crypté !");
            return null;
        }
        ArrayList<String> lines = (ArrayList<String>) readAllLines(fileOpenCryp.getAbsolutePath());
        algoSelect = AlgoType.valueOf(lines.get(0));
        décrypteur.tab_cles = stringToTabofTabInt(lines.get(1));
        décrypteur.nbEspace = Integer.parseInt(lines.get(2));
        if (algoSelect.equals(AlgoType.DES_16_Rondes)) {
            décrypteur.tableauPermutationS = stringToArrayListTabInt(lines.get(3));
            return stringToTabInt(lines.get(4)); // tableau de bit
        } else if (algoSelect.equals(AlgoType.TripleDES)) {
            décrypteur.tableauPermutationS = stringToArrayListTabInt(lines.get(3)); //ArrayList<int[]>
            décrypteur.tableauTripleDes.clear();
            décrypteur.tableauTripleDes.add(stringToTabofTabInt(lines.get(4))); //int[][]
            décrypteur.tableauTripleDes.add(stringToArrayListTabInt(lines.get(5))); //ArrayList<int[]>
            décrypteur.tableauTripleDes.add(Integer.parseInt(lines.get(6))); //int
            décrypteur.tableauTripleDes.add(stringToTabofTabInt(lines.get(7))); //int[][]
            return stringToTabInt(lines.get(8)); //tableau de bit
        } else {
            décrypteur.S= new int[]{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13};
            return stringToTabInt(lines.get(3)); //tableau de bit
        }
    }

    /**
     * Fonction auxiliaire pour stocker toutes les données en fonction de l'algorythme choisi dans un fichier.
     * Le fichier en question contiendra le message crypté ainsi que les données nécessaires pour pouvoir
     * le décrypter un jour.
     *
     * @param bits le message crypté
     * @throws IOException erreur le fichier s'ouvre pas
     */
    public static void saveAllData(int[] bits) throws IOException {
        fileSaveCryp = selectSaveFile();
        if (fileSaveCryp == null) {
            loadNotif("Vous n'avez pas sélectionné de fichier\n pour sauvegarder votre message crypté.");
            return;
        }
        String data = algoSelect.name() + "\n" +
                arrayListTabIntToString(crypteur.tab_cles) + "\n" +
                crypteur.nbEspace + "\n";
        if (algoSelect.equals(AlgoType.DES_16_Rondes)) {
            data += arrayListTabIntToString(crypteur.tableauPermutationS) + "\n" +
                    Arrays.toString(bits);
        } else if (algoSelect.equals(AlgoType.TripleDES)) {
            data += arrayListTabIntToString(crypteur.tableauPermutationS) + "\n" +
                    arrayListTabIntToString((int[][]) crypteur.tableauTripleDes.get(0)) + "\n" + //int[][]
                    arrayListTabIntToString((ArrayList<int[]>) crypteur.tableauTripleDes.get(1)) + "\n" + //arraylist<int[]>
                    crypteur.tableauTripleDes.get(2) + "\n" +
                    arrayListTabIntToString((int[][]) crypteur.tableauTripleDes.get(3)) + "\n" + //int[][]
                    Arrays.toString(bits);
        } else {
            data += Arrays.toString(bits);
        }
        FileWriter fWriter;
        System.out.println(data);
        try {
            fWriter = new FileWriter(fileSaveCryp.getAbsolutePath());
            fWriter.write(data);
            fWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction auxiliaire pour récuperer les lignes de texte d'un fichier
     *
     * @param fileName nom du fichier
     * @return une liste de string
     */
    public static List<String> readAllLines(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Fonction auxiliaire pour convertir correctement en string un ArrayList<int[]>
     *
     * @param ar l'objet à convertir
     * @return la string complète
     */
    public static String arrayListTabIntToString(ArrayList<int[]> ar) {
        StringBuilder tabs = new StringBuilder("[");
        for (int[] tab : ar) tabs.append(Arrays.toString(tab)).append(";");
        tabs.setCharAt(tabs.length() - 1, ']');
        return tabs.toString();
    }

    /**
     * Fonction auxiliaire pour convertir correctement en string un int[][]
     *
     * @param tab l'objet à convertir
     * @return la string complète
     */
    public static String arrayListTabIntToString(int[][] tab) {
        return arrayListTabIntToString(new ArrayList<>() {{
            this.addAll(Arrays.asList(tab));
        }});
    }


    /**
     * Fonction auxiliaire pour convertir correctement un string en ArrayList<int[]>
     *
     * @param text texte à transformer en objet
     * @return ArrayList<int [ ]>
     */
    public static ArrayList<int[]> stringToArrayListTabInt(String text) {
        text = text.replaceAll(" ", "");
        String[] tabS = text.substring(1, text.length() - 1).split(";");
        ArrayList<int[]> ar = new ArrayList<>(); //liste à remplir de tableau
        for (String s : tabS) { //chaque tableau
            String[] ss = s.substring(1, s.length() - 1).split(",");
            int[] t = new int[ss.length]; //le tableau
            for (int i = 0; i < ss.length; i++) t[i] = Integer.parseInt(ss[i]);//on remplit le tableau
            ar.add(t);//on ajoute le tableau
        }
        return ar;
    }

    /**
     * Fonction auxiliaire pour convertir correctement un string en int[][]
     *
     * @param text texte à transformer en objet
     * @return int[][]
     */
    public static int[][] stringToTabofTabInt(String text) {
        ArrayList<int[]> ar = stringToArrayListTabInt(text);
        int[][] tab = new int[ar.size()][];
        for (int i = 0; i < ar.size(); i++) tab[i] = ar.get(i);

        return tab;
    }

    /**
     * Fonction auxiliaire pour convertir correctement un string en int[]
     *
     * @param text texte à transformer en objet
     * @return int[]
     */
    public static int[] stringToTabInt(String text) {
        text = text.replaceAll(" ", "");
        String[] s = text.substring(1, text.length() - 1).split(",");
        int[] tab = new int[s.length];
        for (int i = 0; i < s.length; i++) tab[i] = Integer.parseInt(s[i]);
        return tab;
    }

    public static void main(String[] args) {
        launch();
    }
}