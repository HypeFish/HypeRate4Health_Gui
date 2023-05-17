package CLI.View;

import java.io.IOException;

public class HrCli implements HrView {
    private final Appendable out;

    public HrCli(Appendable out) {
        if (out == null)
            throw new IllegalArgumentException("HRMonitor or Appendable cannot be null");
        this.out = out;
    }

    public HrCli() {
        this(System.out);

    }

    /**
     * Render a specific message to the provided data destination.
     *
     * @param message the message to be transmitted
     * @throws IllegalStateException if transmission of the board to the provided data destination fails
     */
    @Override
    public void renderMessage(String message) {
        try {
            out.append(message);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to render message");
        }
    }
}
