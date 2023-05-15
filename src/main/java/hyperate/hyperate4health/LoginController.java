package hyperate.hyperate4health;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private HBox button;
    @FXML
    private TextField hyperateId;
    @FXML
    private Text loginText;
    @FXML
    private TextField timeout;
    @FXML
    private TextField savePath;


    @FXML
    protected void handleSpaceBar() {

    }

    /**
     * This method is called when the user clicks the Login button.
     */
    @FXML
    protected void handleSubmitButtonAction() {
        String hyperateId = this.hyperateId.getText();
        String savePath = this.savePath.getText();
        String timeout = this.timeout.getText();
        int timeoutInt;

        try {
            timeoutInt = Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The timeout you entered is not a number.");
            alert.showAndWait();
            return;
        }
        if (savePath.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The save path you entered is empty.");
            alert.showAndWait();
        }

        if (hyperateId.equals("79C2")) {
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
                mainController.createMonitor(hyperateId, timeoutInt, savePath);


                stage = new Stage();
                stage.setTitle("Hyperate4Health");
                stage.setScene(new Scene(root, 800, 600));
                stage.show();


            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Login Failed");
                alert.setContentText("An error occurred while trying to open the main window.");
                alert.showAndWait();
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            //display an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The username you entered is incorrect.");
            alert.showAndWait();
        }

//        try {
//            Connection conn = this.getConnection();
//            loginButton.setText("Login Successful");
//        } catch (SQLException e) {
//            loginButton.setText("Login Failed");
//            e.printStackTrace();
//        }
//    }
//        /**
//         * The name of the MySQL account to use (or empty for anonymous)
//         */
//        private String userName = "";
//        /**
//         * The password for the MySQL account (or empty for anonymous)
//         */
//        private String password = "";
//        private Connection conn;
//
//
//    /**
//     * Get a new database connection
//     *
//     * @return The Connection Object
//     * @throws SQLException if getConnection fails.
//     */
//    public Connection getConnection() throws SQLException {
//        Properties connectionProps = new Properties();
//        connectionProps.put("user", this.userName);
//        connectionProps.put("password", this.password);
//
//        String dbName = "hyperate4health";
//        int portNumber = 3306;
//        String serverName = "localhost";
//
//        conn = DriverManager.getConnection("jdbc:mysql://"
//                + serverName + ":" + portNumber + "/" + dbName +
//                "?characterEncoding=UTF-8&useSSL=false", connectionProps);
//
//        return conn;
//    }
    }
}
