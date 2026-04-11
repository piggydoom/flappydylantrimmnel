module com.piggydoom {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.piggydoom to javafx.fxml;
    exports com.piggydoom;
}
