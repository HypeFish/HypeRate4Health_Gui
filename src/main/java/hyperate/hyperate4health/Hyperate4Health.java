package hyperate.hyperate4health;

import java.io.IOException;

public class Hyperate4Health {
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            App.main(args);
        } else if (args[0].equalsIgnoreCase("--CLI"))
            CLI.CommandMain.main(args);
    }
}

