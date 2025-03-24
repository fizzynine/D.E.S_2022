package fr.antoben.desapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NotifController implements Initializable {
    @FXML
    private Label close, message;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message.setText(Main.notifMessage);
        close.setOnMouseClicked(e->((Stage)close.getScene().getWindow()).close());
    }
}
