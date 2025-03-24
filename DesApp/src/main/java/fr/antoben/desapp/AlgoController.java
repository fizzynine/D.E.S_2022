package fr.antoben.desapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import static fr.antoben.desapp.Main.*;

public class AlgoController implements Initializable {

    @FXML
    private Label close;

    @FXML
    private Text title;

    @FXML
    private TextArea c, rc, d, rd;

    @FXML
    private Button home, save, open;

    @FXML
    public AnchorPane anchorpane;
    /**
     * tableau de bit contenant le message crypté
     */
    private int[] msgACrypté;

    /**
     * Fonction auxiliaire pour afficher en temps réel le résultat du cryptage du texte contenue dans "c".
     * c est une zone de texte. C'est celle en haut à gauche
     * rc c'est la zone de texte en bas à gauche.
     */
    private void crypter() {
        if (c.getText().equals("")) return;
        switch (Main.algoSelect) {
            case DES -> {
                msgACrypté = crypteur.crypteVersion1(c.getText());
                rc.setText(crypteur.bitsToString(msgACrypté));
            }
            case DES_16_Rondes -> {
                msgACrypté = crypteur.crypteVersion2(c.getText());
                rc.setText(crypteur.bitsToString(msgACrypté));
            }
            case TripleDES -> {
                msgACrypté = crypteur.crypteVersion3(c.getText());
                rc.setText(crypteur.bitsToString(msgACrypté));
            }
        }
    }
    /**
     * Fonction auxiliaire pour afficher en temps réel le résultat du décryptage du texte contenu dans "s".
     * d est une zone de texte. C'est celle en haut à droite.
     * rd c'est la zone de texte en bas à droite.
     */
    private void décrypter() {
        d.setText("En attente d'un fichier...");
        rd.setText("");
        int[] data = loadData();
        if (data != null) {
            switch (Main.algoSelect) {
                case DES -> rd.setText(décrypteur.decrypteVersion1(data));
                case DES_16_Rondes -> rd.setText(décrypteur.decrypteVersion2(data));
                case TripleDES -> rd.setText(décrypteur.decrypteVersion3(data));
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String time = formatter.format(date);
            d.setText("Algo:\n" + algoSelect.name() + "\n\n" + "Contenu:" + "\n" + décrypteur.bitsToString(data) + "\n\nRéalisé à " + time);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //éviter le bouton click fantôme au début
        //////////////////
        home.setDisable(true);
        open.setDisable(true);
        save.setDisable(true);
        anchorpane.setOnMouseEntered(e -> {
            home.setDisable(false);
            open.setDisable(false);
            save.setDisable(false);
        });
        //////////////////

        //fermeture de la fenêtre si on clique sur la croix.
        close.setOnMouseClicked(e -> Main.algo.close());
        title.setText("Cryptage: " + Main.algoSelect.name());
        //bouton si on veut changer de cryptage
        home.setOnMouseClicked(e -> {
            Main.algo.close();
            Main.stage = new Stage();
            Main.loadMenuWindow();
        });
        //bouton pour charger un fichier
        open.setOnMouseClicked(e -> décrypter());
        //bouton pour sauvegarder un fichier
        save.setOnMouseClicked(e -> {
            try {
                Main.saveAllData(msgACrypté);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        //détection en temps réelle si le texte de c change.
        c.textProperty().addListener((observable, oldValue, newValue) -> crypter());
    }

}