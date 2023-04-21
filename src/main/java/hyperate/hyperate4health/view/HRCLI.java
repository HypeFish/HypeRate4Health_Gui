package hyperate.hyperate4health.view;

import hyperate.hyperate4health.model.HRMonitor;

import java.io.IOException;

public class HRCLI implements HRView {
    private final Appendable out;

    public HRCLI(Appendable out) {
        if (out == null)
            throw new IllegalArgumentException("HRMonitor or Appendable cannot be null");
        this.out = out;
    }

    public HRCLI() {
        this(System.out);

    }

    /**
     * Render a specific messager to the provided data destination.
     *
     * @param message the messager to be transmitted
     * @throws IllegalStateException if transmission of the board to the provided data destination fails
     */
    @Override
    public void renderMessage(String message) {
        try {
            out.append(message);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot render messager");
        }
    }
}
