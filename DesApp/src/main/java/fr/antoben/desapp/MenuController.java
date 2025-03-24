package fr.antoben.desapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    private Label close;

    @FXML
    private ListView listView;

    private static void handle(MouseEvent e) {
        Main.stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        close.setOnMouseClicked(MenuController::handle);
        for(AlgoType algoType : AlgoType.values()){
            String s = "";
            for(int i = 0; i < 14-algoType.name().length();i++) s+=" ";
            Label l = new Label(algoType.name()+s);
            l.setTextFill(Color.WHITESMOKE);
            listView.getItems().add(l);
            l.setOnMouseClicked(e -> Main.loadAlgoWindow(algoType));
        }
    }

}