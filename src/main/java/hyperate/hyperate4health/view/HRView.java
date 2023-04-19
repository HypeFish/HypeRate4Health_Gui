package hyperate.hyperate4health.view;
import hyperate.hyperate4health.model.HRMonitor;

import java.io.IOException;

/**
 * Represents the view for the HR GUI. Interface is supplied with a method to display the app
 * on the command line, another interface will be supplied to display the app in a GUI.
 */
public interface HRView {

    /**
     * Render a specific message to the provided data destination.
     *
     * @param message the message to be transmitted
     * @throws IOException if transmission of the board to the provided data destination fails
     */
    void renderMessage(String message) throws IOException;
}
