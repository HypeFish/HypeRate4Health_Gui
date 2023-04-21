package hyperate.hyperate4health.controller;

import java.io.IOException;

/**
 * Represents the controller for the HR GUI. Interface is supplied with a method to run the program on the
 * command line, another interface will be supplied to run the program in a GUI.
 */
public interface HRControl {

    /**
     * This method will run the program in the command line
     * @throws IllegalStateException if the program input cannot be read
     */
    void run() throws IllegalStateException, IOException;

}
