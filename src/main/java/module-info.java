module com.example.zozo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.zozo to javafx.fxml;
    exports com.example.zozo;
}