package org.example.embroideryeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class MainController {
    @FXML
    private Canvas drawingCanvas;

    @FXML
    private ColorPicker changeColor; //змінюємо колір

    @FXML
    private Button settings;

    @FXML
    private final int PIXEL_SIZE = 30; //розмір пікселів

    @FXML
    public void initialize() {
        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
        drawGrid(gc, 50, 50);
        drawingCanvas.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            int gridX = (int) (mouseX / PIXEL_SIZE);
            int gridY = (int) (mouseY / PIXEL_SIZE);

            Color selectedColor =  changeColor.getValue();
            drawPixel(gc, gridX, gridY, selectedColor);
        });
    }

    private void drawPixel(GraphicsContext gc, int gridX, int gridY, Color color) {
        gc.setFill(color);
        gc.fillRect(gridX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);

    }

    private void drawGrid(GraphicsContext gc, int columns, int rows) {
        gc.setStroke(Color.color(0,0,0)); // колір ліній сітки
        gc.setLineWidth(1.0); // товщина лінії

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                gc.strokeRect(i * PIXEL_SIZE, j * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }

    @FXML
    private void openSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Parent root = loader.load();
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");
        settingsStage.initModality(Modality.APPLICATION_MODAL); //не можна вийти з вікна поки не введемо інформацію
        settingsStage.setScene(new Scene(root));
        settingsStage.showAndWait();
    }


}
