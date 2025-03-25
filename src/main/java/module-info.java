module com.restaurant1.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.restaurant1.demo to javafx.fxml;
    exports com.restaurant1.demo;
    exports com.Test;
    opens com.Test to javafx.fxml;
}