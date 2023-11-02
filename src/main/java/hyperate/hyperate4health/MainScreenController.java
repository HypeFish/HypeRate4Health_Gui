package hyperate.hyperate4health;

import CLI.Model.FirstMonitor;
import CLI.Model.HRMonitor;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainScreenController implements Initializable {

    private final int WINDOW_SIZE = 10;
    @FXML
    private Label heartRateLabel;
    @FXML
    private Label startedLabel;
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
    private String subjectId;
    private TimerTask task;
    private boolean isRunning = false;


    private void setMonitor(String hyperateId, int timeout, String filepath, String subjectID) {
        this.hrMonitor = new FirstMonitor(hyperateId, timeout, filepath, subjectID);

    }

    public void createMonitor(String hyperateId, int timeout, String filepath) {
        this.hyperateId = hyperateId;
        this.timeout = timeout;
        this.filepath = filepath;
        this.subjectId = subjectId;

        if (!isRunning) {
            return;
        }
        setMonitor(hyperateId, timeout, filepath, subjectId);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lineChart.getParent();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false);
        yAxis.setLabel("Value");
        yAxis.setAnimated(false);
        lineChart.setTitle("Heart Rate Monitor");
        lineChart.setAnimated(false);
        series = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart.getData().add(series);

        heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", "0"));
        startedLabel.textProperty().bind(Bindings.concat(""));

        //Set font size larger for labels
        heartRateLabel.setStyle("-fx-font-size: 20px;");
        startedLabel.setStyle("-fx-font-size: 20px;");
    }

    public void sendInfo(String hyperateId, String subjectID) {
        this.hyperateId = hyperateId;
        this.subjectId = subjectID;
    }

    @FXML
    private void startButtonPressed() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        createMonitor(hyperateId, timeout, filepath);
        hrMonitor.start();
        beginTimer();
        startedLabel.textProperty().bind(Bindings.concat("Started!"));
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
                        Set<Node> nodes = lineChart.lookupAll(".series" + 0);
                        for (Node n : nodes) {
                            n.setStyle("""
                                    -fx-background-color: #000000, white;
                                        -fx-background-insets: 0, 2;
                                        -fx-background-radius: 5px;
                                        -fx-padding: 5px;""");
                        }
                        series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;");
                        heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", hr));
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
        task.cancel();
        series.getData().clear();
        heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", "0"));
        startedLabel.textProperty().bind(Bindings.concat("Stopped!"));

    }

    @FXML
    private void handleClose() {
        if (hrMonitor != null) {
            hrMonitor.stopApplication();
        }
        Platform.exit();
    }
}
