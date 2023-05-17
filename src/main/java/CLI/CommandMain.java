package CLI;

import CLI.Controller.CommandController;
import CLI.Controller.HRControl;
import CLI.View.HrCli;
import CLI.View.HrView;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandMain {

	public static void main(String[] args) throws IOException {
		HrView view = new HrCli();
		HRControl controller = new CommandController(view, new InputStreamReader(System.in));

		controller.run();
	}
}


