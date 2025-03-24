module fr.antoben.desapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.antoben.desapp to javafx.fxml;
    exports fr.antoben.desapp;
}