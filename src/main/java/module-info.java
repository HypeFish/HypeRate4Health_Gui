module hyperate.hyperate4health {
    //requires javafx.controls;
    //requires javafx.fxml;
    //requires javafx.web;
    requires java.sql;
    requires javax.websocket.api;
    requires org.json;
    requires com.google.gson;
  requires java.desktop;

  exports hyperate.hyperate4health.model;
    opens hyperate.hyperate4health.model to javafx.base;
    exports hyperate.hyperate4health.view;
    opens hyperate.hyperate4health.view to javafx.fxml;
    opens hyperate.hyperate4health to javafx.fxml;
    exports hyperate.hyperate4health.controller;
    opens hyperate.hyperate4health.controller to javafx.fxml;
    exports hyperate.hyperate4health;

}