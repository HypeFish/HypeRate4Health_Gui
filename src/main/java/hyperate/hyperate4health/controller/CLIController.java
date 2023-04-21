package hyperate.hyperate4health.controller;

import hyperate.hyperate4health.model.FirstMonitor;
import hyperate.hyperate4health.model.HRMonitor;
import hyperate.hyperate4health.view.HRView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class CLIController implements HRControl {
    private final HRView view;
    private final Readable in;

    public CLIController(HRView view, Readable in) {
        if (view == null || in == null)
            throw new IllegalArgumentException("HRMonitor, HRView, or Readable cannot be null");
        this.view = view;
        this.in = in;
    }

    public CLIController(HRMonitor model, HRView view) {
        this(view, new BufferedReader(new InputStreamReader(System.in)));
    }


    /**
     * This method will run the program in the command line
     *
     * @throws IllegalStateException if the program input cannot be read
     */
    @Override
    public void run() throws IllegalStateException, IOException {

        view.renderMessage("Welcome to the HypeRate CLI! ");
        view.renderMessage("Thank you to HypeRate for making this possible! ");
        Scanner scanner = new Scanner(in);
        view.renderMessage("Enter the HypeRate device ID. Example: AB12\n");
        String hyperateId = scanner.nextLine();
        view.renderMessage("Enter the path to where you want to save the file. To override default file naming, you can specify a filename ending in .csv\nExample: /user/Documents\nExample: /user/Documents/HR.csv\n(i) If the file exists already, it will be overriden.\n");
        String savePath = scanner.nextLine();
        view.renderMessage("Enter the minimum time allowed between HR signals for the connection to remain open and the program to be running.\n");
        int timeout = scanner.nextInt();
        view.renderMessage("Enter the API key to the recorder. If you do not specify, it will be automatically loaded from the \"HYPERATE_API_KEY\" environmental variable.\n");
        String apiKey = scanner.nextLine();

        view.renderMessage("Starting recording...");
        if (hyperateId == null || hyperateId.isEmpty()) {
            view.renderMessage("Invalid HypeRate ID. Please try again.");
            return;
        }
        if (savePath == null || savePath.isEmpty()) {
            view.renderMessage("Invalid save path. Please try again.");
            return;
        }
        if (timeout < 0) {
            view.renderMessage("Invalid timeout. Please try again.");
            return;
        }
        FirstMonitor monitor = new FirstMonitor(hyperateId, timeout, savePath);

        view.renderMessage("Connection to Hyperate established with ID " + hyperateId +
                "\n(i) For a better visual go to: app.hyperate.io/" + hyperateId);
        view.renderMessage("Heart rate data will be saved to: " + savePath + "/" + "HR:" + hyperateId + ".csv");
        view.renderMessage("Heart rate will be saved every " + 3 + " seconds");
    }
}