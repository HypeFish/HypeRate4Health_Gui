package CLI.view;

/**
 * Represents the view for the HR GUI. Interface is supplied with a method to display the app
 * on the command line, another interface will be supplied to display the app in a GUI.
 */
public interface HRView {

    /**
     * Render a specific messager to the provided data destination.
     *
     * @param message the messager to be transmitted
     * @throws IllegalStateException if transmission of the board to the provided data destination fails
     */
    void renderMessage(String message) throws IllegalStateException;
}
