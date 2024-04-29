module com.justbelieveinmyself.portscanner {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.sun.jna;
    requires java.prefs;
    requires java.logging;

    opens com.justbelieveinmyself.portscanner to javafx.fxml;
    exports com.justbelieveinmyself.portscanner;
    exports com.justbelieveinmyself.portscanner.core.net;
}