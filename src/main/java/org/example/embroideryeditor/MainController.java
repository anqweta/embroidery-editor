package org.example.embroideryeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainController {
    @FXML
    private Canvas drawingCanvas;

    @FXML
    private ColorPicker changeColor; //змінюємо колір

    @FXML
    private Spinner<Integer> inputColumns;

    @FXML
    private Spinner<Integer> inputRow;

    @FXML
    private Button settings;

    @FXML
    private final int PIXEL_SIZE = 30; //розмір пікселів

    private Color[][] pixelData; //для збереження малюнків.

    @FXML
    private ToggleButton removePixel;

    @FXML
    private ToggleButton btnVerticalSymmetry;

    @FXML
    private ToggleButton btnHorizontalSymmetry;

    private Color selectedColor;

    @FXML
    private TilePane colorPalette;

    @FXML
    public void initialize() {
        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
        drawGrid(gc, 30, 30);
        pixelData = new Color[30][30];
        //ЩЕ РОЗІБРАТИСЬ
        SpinnerValueFactory<Integer> colFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 128, 30);
        SpinnerValueFactory<Integer> rowFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 128, 30);
        inputColumns.setValueFactory(colFactory);
        inputRow.setValueFactory(rowFactory);
        pickColorOnTailPane();

        drawingCanvas.setOnMouseClicked(event -> {
            boolean btnRemove = removePixel.isSelected();
            double mouseX = event.getX();
            double mouseY = event.getY();
            int gridX = (int) (mouseX / PIXEL_SIZE);
            int gridY = (int) (mouseY / PIXEL_SIZE);
            selectedColor =  changeColor.getValue();
            if (btnRemove) {
                drawWithSymmetry(gc, gridX, gridY, Color.valueOf("#f4f4f4"));
            } else {
                drawWithSymmetry(gc, gridX, gridY, selectedColor);
            }
        });
    }

    private void drawPixel(GraphicsContext gc, int gridX, int gridY, Color color) {
        gc.setFill(color);
        gc.fillRect(gridX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        pixelData[gridX][gridY] = color;
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

    @FXML
    private void setNewGridSize() {
       /* inputWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            int gridWidth =  getInputWidth() * PIXEL_SIZE;
            drawingCanvas.setWidth(gridWidth);
            drawGrid(drawingCanvas.getGraphicsContext2D(), getInputWidth(), getInputHeight());
        });
        inputHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            int gridHeight = getInputHeight() * PIXEL_SIZE;
            drawingCanvas.setHeight(gridHeight);
            drawGrid(drawingCanvas.getGraphicsContext2D(), getInputWidth(), getInputHeight());
        }); */
        removeOldGrid(drawingCanvas.getGraphicsContext2D());
        int gridWidth = getInputColumns() * PIXEL_SIZE;
        int gridHeight = getInputRow()  * PIXEL_SIZE;
        drawingCanvas.setWidth(gridWidth);
        drawingCanvas.setHeight(gridHeight);
        drawGrid(drawingCanvas.getGraphicsContext2D(), getInputColumns(), getInputRow() );
        copyPaint();
    }

    private void removeOldGrid(GraphicsContext gc) {
        gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
    }

    private void copyPaint() {
        Color[][] oldPixelData = pixelData.clone();
        pixelData = new Color[getInputColumns()][getInputRow()];
        for (int i = 0; i < oldPixelData.length; i++) {
            for (int j = 0; j < oldPixelData[i].length; j++) {
                if (oldPixelData[i][j] != null && i < getInputColumns() && j < getInputRow()) {
                    Color color = oldPixelData[i][j];
                    drawPixel(drawingCanvas.getGraphicsContext2D(), i, j, color);
                }
            }
        }
    }

    private void drawFullReverse(GraphicsContext gc, int gridX, int gridY, Color color) {
        int reverseX = getInputColumns() - gridX - 1;
        int reverseY = getInputRow() - gridY - 1;
        gc.setFill(color);
        gc.fillRect(gridX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        gc.fillRect(reverseX * PIXEL_SIZE, reverseY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        gc.fillRect(gridX * PIXEL_SIZE, reverseY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        gc.fillRect(reverseX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        pixelData[gridX][gridY] = color;
        pixelData[reverseX][reverseY] = color;
        pixelData[gridX][reverseY] = color;
        pixelData[reverseX][gridY] = color;
    }

    private void drawHorizontalReverse(GraphicsContext gc, int gridX, int gridY, Color color) {
        int reverseY = getInputRow() - gridY - 1;
        gc.setFill(color);
        gc.fillRect(gridX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        gc.fillRect(gridX * PIXEL_SIZE, reverseY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        pixelData[gridX][gridY] = color;
        pixelData[gridX][reverseY] = color;
    }
    private void drawVerticalReverse(GraphicsContext gc, int gridX, int gridY, Color color) {
        int reverseX = getInputColumns() - gridX - 1;
        gc.setFill(color);
        gc.fillRect(gridX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        gc.fillRect(reverseX * PIXEL_SIZE, gridY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        pixelData[gridX][gridY] = color;
        pixelData[reverseX][gridY] = color;
    }

    private void drawWithSymmetry(GraphicsContext gc, int gridX, int gridY, Color color) {
        boolean vertSym = btnVerticalSymmetry.isSelected();
        boolean horizSym = btnHorizontalSymmetry.isSelected();
        if (!vertSym && !horizSym) {
            drawPixel(gc, gridX, gridY, color);
        }
        if (vertSym && horizSym) {
            drawFullReverse(gc, gridX, gridY, color);
        }
        if (vertSym) {
            drawVerticalReverse(gc, gridX, gridY, color);
        }
        if (horizSym) {
            drawHorizontalReverse(gc, gridX, gridY, color);
        }
    }

    @FXML
    private void saveCanvas() {
        WritableImage writableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());
        drawingCanvas.snapshot(null, writableImage);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        try {
            File outputFile = new File("canvas.png");
            ImageIO.write(bufferedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadCanvas() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть малюнок для завантаження");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Зображення (PNG)", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(drawingCanvas.getScene().getWindow());

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            GraphicsContext gc = drawingCanvas.getGraphicsContext2D();

            // 1. Визначаємо нові розміри сітки на основі розміру картинки
            int newCols = (int) (image.getWidth() / PIXEL_SIZE);
            int newRows = (int) (image.getHeight() / PIXEL_SIZE);

            // Оновлюємо значення в спінерах, щоб інтерфейс відповідав малюнку
            inputColumns.getValueFactory().setValue(newCols);
            inputRow.getValueFactory().setValue(newRows);

            // Оновлюємо розміри Canvas і масиву даних
            drawingCanvas.setWidth(image.getWidth());
            drawingCanvas.setHeight(image.getHeight());
            pixelData = new Color[newCols][newRows];

            // Очищаємо полотно
            gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());

            // 2. Зчитуємо кольори з картинки
            javafx.scene.image.PixelReader pixelReader = image.getPixelReader();

            for (int i = 0; i < newCols; i++) {
                for (int j = 0; j < newRows; j++) {
                    // Беремо колір з ЦЕНТРУ кожної клітинки (наприклад 15, 15).
                    // Це потрібно, щоб не захопити чорну лінію сітки (яка знаходиться на краях)
                    int pixelX = i * PIXEL_SIZE + (PIXEL_SIZE / 2);
                    int pixelY = j * PIXEL_SIZE + (PIXEL_SIZE / 2);

                    Color color = pixelReader.getColor(pixelX, pixelY);

                    // Якщо колір не повністю прозорий і не чорний (колір самої сітки)
                    if (color.getOpacity() > 0 && !color.equals(Color.BLACK)) {
                        // Використовуємо твій існуючий метод для відмальовки та запису в масив
                        drawPixel(gc, i, j, color);
                    }
                }
            }

            // 3. Відмальовуємо сітку поверх відновлених пікселів
            drawGrid(gc, newCols, newRows);
        }
    }

    @FXML
    private int getInputColumns() {
        return  inputColumns.getValue();
    }
    @FXML
    private int getInputRow() {
        return inputRow.getValue();
    }

    private void pickColorOnTailPane() {
        colorPalette.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getTarget() instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) mouseEvent.getTarget();
                selectedColor = (Color) rectangle.getFill();
                changeColor.setValue(selectedColor);
            }
        });
    }

    @FXML
    private void newProject() {
        removeOldGrid(drawingCanvas.getGraphicsContext2D());
        drawGrid(drawingCanvas.getGraphicsContext2D(), 30, 30);
        pixelData = new Color[getInputColumns()][getInputRow()];
    }

   /* @FXML
    private void removePixel() {
        drawingCanvas.setOnMouseClicked(event -> {
               double mouseX = event.getX();
               double mouseY = event.getY();
                int gridX = (int) (mouseX / PIXEL_SIZE);
                int gridY = (int) (mouseY / PIXEL_SIZE);
               drawingCanvas.getGraphicsContext2D().clearRect(mouseX, mouseY, 30, 30);
        });
    } */


}
