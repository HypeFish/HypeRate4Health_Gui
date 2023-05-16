package hyperate.hyperate4health;

import CLI.model.FirstMonitor;
import CLI.model.HRMonitor;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class MainScreenController implements Initializable {

    private final int WINDOW_SIZE = 10;
    @FXML
    private Label ageLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label heartRateLabel;
    @FXML
    private CategoryAxis xAxis = new CategoryAxis();
    @FXML
    private NumberAxis yAxis = new NumberAxis();
    @FXML
    private LineChart<String, Integer> lineChart;
    private HRMonitor hrMonitor;
    private XYChart.Series<String, Integer> series;
    private String hyperateId;
    private int timeout;
    private String filepath;
    private TimerTask task;
    private Connection connection;
    private String databaseUser;
    private String databasePassword;
    private String databaseUrl;
    private boolean isRunning = false;
    private String name;
    private int age;
    private String dob;

    private void setMonitor(String hyperateId, int timeout, String filepath) {
        this.hrMonitor = new FirstMonitor(hyperateId, timeout, filepath);

    }

    public void createMonitor(String hyperateId, int timeout, String filepath) {
        this.hyperateId = hyperateId;
        this.timeout = timeout;
        this.filepath = filepath;

        nameLabel.textProperty().bind(Bindings.concat("Name: ", name));
        ageLabel.textProperty().bind(Bindings.concat("Age: ", age));
        //put dob like yy/mm/dd
        dobLabel.textProperty().bind(Bindings.concat("DOB: ", dob));

        if (!isRunning) {
            return;
        }
        setMonitor(hyperateId, timeout, filepath);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //defining the axes
        lineChart.getParent();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Value");
        yAxis.setAnimated(false); // axis animations are removed
        lineChart.setTitle("Heart Rate Monitor");
        lineChart.setAnimated(false); // disable animations
        //defining a series to display data
        series = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart.getData().add(series);

        this.heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", "0"));
    }

    public void sendInfo(String hyperateId, String name, int age, String dob, String databaseUser, String databasePassword, String databaseUrl) {
        this.hyperateId = hyperateId;
        this.name = name;
        this.age = age;
        this.dob = dob;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.databaseUrl = databaseUrl;

    }

    @FXML
    private void startButtonPressed() {

        //Create the monitor
        if (isRunning) {
            return;
        }
        isRunning = true;
        createMonitor(hyperateId, timeout, filepath);
        hrMonitor.start();
        beginTimer();
    }

    private void beginTimer() {
        task = new TimerTask() {
            @Override
            public void run() {

                Platform.runLater(() -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String time = sdf.format(new Date());
                    int hr;
                    try {
                        hr = hrMonitor.getHeartRate();
                        series.getData().add(new XYChart.Data<>(time, hr));
                        heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", hr));
                        //Add to database
                        try {
                            connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
                            PreparedStatement preparedStatement =
                                    connection.prepareStatement("INSERT INTO " +
                                            "heartrate (hyperate_id, name, value, time) VALUES (?,?,?,?)");
                            preparedStatement.setString(1, hyperateId);
                            preparedStatement.setString(2, name);
                            preparedStatement.setInt(3, hr);
                            preparedStatement.setString(4,
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            preparedStatement.executeUpdate();

                        } catch (SQLException e) {
                            new Alert(Alert.AlertType.ERROR, "Could not connect to database").showAndWait();
                        }
                    } catch (NullPointerException e) {
                        //Do nothing
                    }
                    if (series.getData().size() > WINDOW_SIZE) series.getData().remove(0);
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);

    }

    @FXML
    private void quitButtonPressed() {

        if (hrMonitor == null) {
            return;
        }

        this.isRunning = false;
        this.hrMonitor.stopApplication();
        //Stop the graph
        task.cancel();
        series.getData().clear();
        heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", "0"));

    }

    @FXML
    private void save() {
        signIn();
    }

    private void saveData() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("Call insert_heartrate(?,?,?,?,?,?)");
            preparedStatement.setString(1, this.hyperateId);
            preparedStatement.setString(2, this.name);
            if (this.hrMonitor == null) {
                preparedStatement.setInt(3, 0);
            } else {
                preparedStatement.setInt(3, this.hrMonitor.getHeartRate());
            }
            preparedStatement.setString(
                    4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            preparedStatement.setInt(5, this.age);
            preparedStatement.setString(6, this.dob);
            preparedStatement.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Data saved successfully").showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Data could not be saved").showAndWait();
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
                        this.nameLabel.getParent().setDisable(false);
                    }
            } else {
                this.nameLabel.getParent().setDisable(false);
            }
    }

    private void verifyDatabaseLogin() {
        //Verify the login details
        //If correct, save the data to the database
        //If incorrect, display an error message
        try {
            this.connection = this.getConnection();
            this.saveData();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Incorrect login details").showAndWait();
            e.printStackTrace();
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
        connection = DriverManager.getConnection(databaseUrl, connectionProps);

        return connection;
    }
}
