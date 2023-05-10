package hyperate.hyperate4health.controller;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Interface for the GUI Controller that holds methods for the view to call. Helps pass along data
 * from the view to update the controller with. Also holds methods for the controller to call to
 * update the view.
 */
public interface GuiController {

  /**
   * Starts the GUI.
   */
  void startGui();

  /**
   * Stops the GUI.
   */
  void stopGui();

  /**
   * Updates the graph with the given heart rates.
   *
   * @param heartRates the heart rates to update the graph with.
   */
  void updateGraph(HashMap<Timestamp, Integer> heartRates);

  /**
   * Updates the heart rate with the given heart rate and timestamp.
   *
   * @param heartRate the heart rate to update the heart rate with.
   * @param timestamp the timestamp to update the heart rate with.
   */
  void updateHeartRate(Integer heartRate, Timestamp timestamp);

  /**
   * Saves the graph to the given file.
   *
   * @param fileName the file to save the graph to.
   */
  void save(String fileName);

  /**
   * Loads the graph from the given file.
   *
   * @param fileName the file to load the graph from.
   */
  void load(String fileName);

}
