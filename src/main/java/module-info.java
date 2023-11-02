module hyperate.hyperate4health {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires java.sql;
    requires javax.websocket.api;
    requires org.json;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.swing; // Add this line if you're using javafx.swing


    exports hyperate.hyperate4health;
    opens hyperate.hyperate4health to javafx.controls , javafx.fxml;

    exports CLI.Model;
    opens CLI.Model to javafx.base;
    exports CLI.View;
    opens CLI.View to javafx.fxml;
    exports CLI;
    opens CLI to javafx.fxml;
    exports CLI.Controller;
    opens CLI.Controller to javafx.fxml;
}