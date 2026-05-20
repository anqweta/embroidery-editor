package org.example.embroideryeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {



    @FXML
    private Button cancel;



    @FXML
    private void closeSettings() throws IOException {
        ((Stage) cancel.getScene().getWindow()).close();
    }
}
