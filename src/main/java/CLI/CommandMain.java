package CLI;

import CLI.controller.CommandController;
import CLI.controller.HRControl;
import CLI.view.HRCLI;
import CLI.view.HRView;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandMain {

	public static void main(String[] args) throws IOException {
		HRView view = new HRCLI();
		HRControl controller = new CommandController(view, new InputStreamReader(System.in));
		controller.run();
	}
}


