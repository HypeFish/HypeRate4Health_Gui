package hyperate.hyperate4health;

import CLI.model.FirstMonitor;
import CLI.model.HRMonitor;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainScreenController implements Initializable {

    private final int WINDOW_SIZE = 10;
    public Text title;
    private boolean isRunning = false;
    @FXML
    private Label name;
    @FXML
    private Button quitButton;
    @FXML
    private Button startButton;
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

    private void setMonitor(String hyperateId, int timeout, String filepath) {
        this.hrMonitor = new FirstMonitor(hyperateId, timeout, filepath);
    }

    public void createMonitor(String hyperateId, int timeout, String filepath) {
        this.hyperateId = hyperateId;
        this.timeout = timeout;
        this.filepath = filepath;

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

        //Set the name

        //Set the heart rate
        heartRateLabel.textProperty().bind(Bindings.concat("Heart Rate: ", 0));

    }

    @FXML
    private void startButtonPressed(ActionEvent actionEvent) {

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

                    } catch (NullPointerException e) {
                        //Do nothing
                    }
                    if (series.getData().size() > WINDOW_SIZE)
                        series.getData().remove(0);
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);

    }

    @FXML
    private void quitButtonPressed(ActionEvent actionEvent) {
        this.isRunning = false;
        this.hrMonitor.stopApplication();
        //Stop the graph
        task.cancel();
    }
}
