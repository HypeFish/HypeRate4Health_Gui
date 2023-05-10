package hyperate.hyperate4health.view;

import hyperate.hyperate4health.controller.GuiController;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.HashMap;


/**
 * Interface for the GUI View that holds methods for the controller to call. Helps pass along data
 * from the controller to update the view with.
 */
public interface GuiView {

  /**
   * Updated the histogram class's fields based on the color values passed to the view from the
   * controller.
   *
   * @param heartRates the heart rates to update the histogram with.
   */
  void updateHrGraph(HashMap<Timestamp, Integer> heartRates);

  /**
   * Takes in a Features object that can represent the abilities of the controller. The view will
   * use this to determine what to display on the GUI and what to do when the user clicks on the
   * GUI.
   *
   * @param controller the features of the controller.
   */
  void addFeatures(GuiController controller);

  /**
   * Displays the given image in a JPanel. The image is displayed in the current state that it is
   * in, depending upon what the user has done to it.
   *
   * @param image The image to display.
   */
  void setImage(BufferedImage image);

  /**
   * Gives an error message when the user inputs something incorrectly or attempts to edit an image
   * before loading.
   */
  void errorMessage();

}

