package hyperate.hyperate4health;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private TextField hyperateId;
    @FXML
    private Text loginText;
    @FXML
    private TextField timeout;
    @FXML
    private TextField savePath;
    @FXML
    private TextField age;
    @FXML
    private TextField dob;
    private boolean signedIn = false;

    private Connection connection;
    private String databaseUser;
    private String databasePassword;
    private String name;
    private int timeoutInt;
    private String savePathString;
    private String hyperateIdString;
    private int ageInt;
    private String dobString;
    private String databaseUrl;

    /**
     * This method is called when the user clicks the Login button.
     */
    @FXML
    protected void handleSubmitButtonAction() {
        name = this.username.getText();
        hyperateIdString = this.hyperateId.getText();
        savePathString = this.savePath.getText();
        String timeout = this.timeout.getText();
        String age = this.age.getText();
        dobString = this.dob.getText();

        try {
            this.timeoutInt = Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The timeout you entered is not a number.");
            alert.showAndWait();
        }
        if (savePathString.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The save path you entered is empty.");
            alert.showAndWait();
        }
        if (dobString.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The date of birth you entered is empty.");
            alert.showAndWait();
        }

        try {
            this.ageInt = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The age you entered is not a number.");
            alert.showAndWait();
        }

        if (name.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("The name you entered is empty.");
            alert.showAndWait();
        }


        this.signIn();

        //Optional sign in to database
        if (signedIn) {
            this.correctInfo(hyperateIdString, name);
        } else {
            changeScreen();
        }
    }

    private void correctInfo(String hyperateIdString, String name) {
        PreparedStatement preparedStatement = null;
        //Check if given id and name exist in database
        try {
            preparedStatement =
                    connection.prepareStatement("select * from person where hyperate_id = ? and name = ?");
            preparedStatement.setString(1, hyperateIdString);
            preparedStatement.setString(2, name);
            //check if user exists
            if (preparedStatement.executeQuery().next()) {
                changeScreen();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Login Failed");
                alert.setContentText("The name and id you entered do not match.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            closeStatement(preparedStatement);
        }
    }

    private void closeStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText("An error occurred while trying to connect to the database.");
            alert.showAndWait();
        }
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
            mainController.sendInfo(hyperateIdString, name, ageInt, dobString, databaseUser,
                    databasePassword, databaseUrl);
            mainController.createMonitor(hyperateIdString, timeoutInt, savePathString);
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
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void signIn() {
        loginDialog();
    }

    public void loginDialog() {
        //Ask if the user wants to log in to the database
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Database Login");
        alert.setHeaderText("Database Login");
        alert.setContentText("Would you like to log in to the database?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent())
            if (result.get().equals(ButtonType.OK)) {
                signedIn = true;
                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.setPadding(new Insets(20, 150, 10, 10));

                TextField databaseUser = new TextField();
                databaseUser.setPromptText("Database Username");
                gridPane.add(databaseUser, 0, 0);

                PasswordField databasePassword = new PasswordField();
                databasePassword.setPromptText("Database Password");
                gridPane.add(databasePassword, 0, 1);

                alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Database Login");
                alert.setHeaderText("Database Login");
                alert.getDialogPane().setContent(gridPane);
                alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                result = alert.showAndWait();
                this.databaseUser = databaseUser.getText();
                this.databasePassword = databasePassword.getText();
                if (result.isPresent())
                    if (result.get().equals(ButtonType.OK)) {
                        this.verifyDatabaseLogin();
                    } else {
                        this.signIn();
                    }
            } else {
                signedIn = false;

            }
    }

    private void verifyDatabaseLogin() {
        //Verify the login details
        //If correct, save the data to the database
        //If incorrect, display an error message
        try {
            this.connection = this.getConnection();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Incorrect login details").showAndWait();
            e.printStackTrace();
            this.signIn();
        }
    }

    /**
     * Get a new database connection
     *
     * @return The Connection Object
     * @throws SQLException if getConnection fails.
     */
    public Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.databaseUser);
        connectionProps.put("password", this.databasePassword);

        String dbName = "hyperate_health";
        int databasePort = 3306;
        String serverName = "localhost";
        databaseUrl = "jdbc:mysql://" + serverName + ":" + databasePort + "/" + dbName;

        connection = DriverManager.getConnection(databaseUrl, connectionProps);

        return connection;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set the text fields
        hyperateId.setText("79C2");
        username.setText("Skye");
        age.setText("19");
        dob.setText("2003-06-02");
        timeout.setText("10");
        savePath.setText("/Users/skyetoral/IdeaProjects/HypeRate4Health/src/main/CSVData/");

    }
}
