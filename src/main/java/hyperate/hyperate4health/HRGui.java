package hyperate.hyperate4health;

import hyperate.hyperate4health.controller.CLIController;
import hyperate.hyperate4health.controller.HRControl;
import hyperate.hyperate4health.model.HRMonitor;
import hyperate.hyperate4health.view.HRCLI;
import hyperate.hyperate4health.view.HRView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;

public class HRGui {

    public static void main(String[] args) throws IOException {
    HRView view = new HRCLI();
    HRControl controller = new CLIController(view, new InputStreamReader(System.in));
    controller.run();
    }
}


