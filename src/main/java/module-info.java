module hyperate.hyperate4health {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires javax.websocket.api;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.json;
    requires com.fasterxml.jackson.dataformat.csv;


    opens hyperate.hyperate4health to javafx.fxml;
    exports hyperate.hyperate4health.controller;
    opens hyperate.hyperate4health.controller to javafx.fxml;
    exports hyperate.hyperate4health;

}