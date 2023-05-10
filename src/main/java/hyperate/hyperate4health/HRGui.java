package hyperate.hyperate4health;

import hyperate.hyperate4health.controller.CommandController;
import hyperate.hyperate4health.controller.HRControl;
import hyperate.hyperate4health.view.HRCLI;
import hyperate.hyperate4health.view.HRView;

import java.io.IOException;
import java.io.InputStreamReader;

public class HRGui {

    public static void main(String[] args) throws IOException {
        HRView view = new HRCLI();
        HRControl controller = new CommandController(view, new InputStreamReader(System.in));
        controller.run();
    }
}


