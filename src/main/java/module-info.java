module org.example.embroideryeditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.graphics;

    opens org.example.embroideryeditor to javafx.fxml;
    exports org.example.embroideryeditor;
}