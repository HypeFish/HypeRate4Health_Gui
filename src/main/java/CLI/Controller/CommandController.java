package CLI.Controller;

import CLI.Model.FirstMonitor;
import CLI.View.HrView;

import java.util.Scanner;

/**
 * This class represents the CLI.controller.controller for the command line interface. It implements the HRControl
 * interface.
 */
public class CommandController implements HRControl {

    private final HrView view;
    private final Readable in;

    /**
     * This constructor creates a new CommandController object.
     *
     * @param view The view to be used (GUI, CLI, etc.)
     * @param in   The input to be used (Standard input, file, etc.)
     * @throws IllegalArgumentException if the view or model is null
     */
    public CommandController(HrView view, Readable in) {
        if (view == null || in == null) {
            throw new IllegalArgumentException("HRMonitor, HRView, or Readable cannot be null");
        }
        this.view = view;
        this.in = in;
    }

    /**
     * This method will run the program in the command line.
     *
     * @throws IllegalStateException if the program input cannot be read
     */
    @Override
    public void run() throws IllegalStateException {
        view.renderMessage("Welcome to the HypeRate CLI! ");
        view.renderMessage("Thank you to HypeRate for making this possible! ");
        Scanner scanner = new Scanner(in);
        view.renderMessage("Enter the HypeRate device ID. Example: AB12\n");
        final String hyperateId = scanner.nextLine();
        view.renderMessage("""
                Enter the path to where you want to save the file. To override default file naming, you can specify a
                 filename ending in .csv
                Example: /user/Documents
                Example: /user/Documents/HR.csv
                (i) If the file exists already, it will be replaced.
                """);
        final String savePath = scanner.nextLine();
        view.renderMessage("Enter the minimum time allowed between HR signals for the connection to remain open and " + "the program to be running.\n");
        int timeout;
        try {
            timeout = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            view.renderMessage("Invalid timeout. Please try again.\n");
            return;
        }
        view.renderMessage("Enter the subject ID.\n");
        final String subjectID = scanner.nextLine();

        view.renderMessage("Starting recording...\n");
        if (hyperateId == null || hyperateId.length() != 4) {
            view.renderMessage("Invalid HypeRate ID. Please try again.\n");
            return;
        }
        if (savePath == null || savePath.isEmpty()) {
            view.renderMessage("Invalid save path. Please try again\n");
            return;
        }
        if (timeout < 0) {
            view.renderMessage("Invalid timeout. Please try again.\n");
            return;
        }


        if (hyperateId.equalsIgnoreCase("quit") || hyperateId.equalsIgnoreCase("exit") || savePath.equalsIgnoreCase("quit") || savePath.equalsIgnoreCase("exit") || timeout == 0) {
            view.renderMessage("Program exited.\n");
            return;

        }


        FirstMonitor monitor = new FirstMonitor(hyperateId, timeout, savePath, subjectID);
        monitor.start();

        view.renderMessage("Connection to Hyperate established with ID " + hyperateId + "\n(i) For a better visual " + "go to: app.hyperate.io/" + hyperateId + "\n");
        view.renderMessage("Heart rate data will be saved to: " + savePath + "/" + "HR:" + hyperateId + ".csv" + "\n");
        view.renderMessage("Heart rate will be saved every " + 3 + " seconds" + "\n");

        view.renderMessage("Type quit or exit to stop recording\n");

        while (true) {
            String stop = scanner.nextLine();
            if (stop.equalsIgnoreCase("quit") || stop.equals("exit")) {
                monitor.stopApplication();
                break;
            }
        }
    }
}