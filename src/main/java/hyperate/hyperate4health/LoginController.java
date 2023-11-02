package hyperate.hyperate4health;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField hyperateId;
    @FXML
    private Text loginText;
    @FXML
    private TextField directory;
    @FXML
    private TextField subjectId;

    private String savePathString;
    private String hyperateIdString;
    private String subjectIdString;

    /**
     * This method is called when the user clicks the Login button.
     */
    @FXML
    protected void handleSubmitButtonAction() {
        hyperateIdString = this.hyperateId.getText();
        savePathString = this.directory.getText();
        subjectIdString = this.subjectId.getText();

        if (hyperateIdString.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The Hyperate ID you entered is empty.");
            alert.showAndWait();
            return;
        }
        if (savePathString.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The save path you entered is empty.");
            alert.showAndWait();
            return;
        }

        if (subjectIdString.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The subject ID you entered is empty.");
            alert.showAndWait();
            return;
        }

        changeScreen();
    }

    private void changeScreen() {
        loginText.setText("Login Successful");
        try {
            Stage stage = (Stage) loginText.getScene().getWindow();
            stage.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText("Login Successful");
            alert.setContentText("Welcome to Hyperate4Health");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root = loader.load();
            MainScreenController mainController = loader.getController();
            mainController.sendInfo(hyperateIdString, subjectIdString);
            mainController.createMonitor(hyperateIdString, 30, savePathString);
            stage = new Stage();
            stage.setTitle("Hyperate4Health");
            stage.setScene(new Scene(root, 1000, 800));
            stage.show();


        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("An error occurred while trying to open the main window.");
            alert.showAndWait();
            System.exit(1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void handleChooseDirectoryButton(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a Directory");

        // Show open dialog
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            //set the text field to the selected directory
            savePathString = selectedDirectory.getAbsolutePath();

            //set the text field to the selected directory
            directory.setText(savePathString);
        }
    }

}
