module com.piggydoom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.piggydoom to javafx.fxml;
    exports com.piggydoom;
}
