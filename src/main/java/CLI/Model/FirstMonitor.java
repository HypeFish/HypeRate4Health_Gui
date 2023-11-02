package CLI.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;

/**
 * @author HypeFish
 * @version 1.0
 * <p>This class seeks to implement the basic functionality of the HRMonitor interface.
 * This class will be used
 * to establish a connection to the web socket, retrieve the most recent heart rate,
 * retrieve all heart rates,
 * stop the application, and write the data collected into a csv file. </p>
 *
 * <p>This class will attempt to establish a connection to the web socket.
 * If the connection is successful, the
 *  application will be able to retrieve the most recent heart rate, retrieve
 *  all heart rates, and write the data
 *  collected into a csv file. If the connection is unsuccessful, the
 *  application will be able to stop the
 *  application. This will be done without the use of a database.
 *  A database may be used in the future to store
 *  and retrieve data with the help of a proper DBMS.
 * @summary The implmentation to the model. </p>
 *
 */
@ServerEndpoint(value = "/firstMonitor",
        decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class FirstMonitor extends Endpoint implements HRMonitor {
  protected final String hyperateId;
  protected final int timeout;
  protected final String savePath;
  protected final String subjectID;
  protected final Timer responseBeat = new Timer();
  protected final HashMap<Timestamp, Integer> data = new HashMap<>();
  protected Session session;
  protected Timer closeBeat = new Timer();
  protected File file;

  /**
   * This constructor will create a new FirstMonitor object.
   */
  public FirstMonitor(String hyperateId, int timeout, String savePath, String subjectID) {

    if (hyperateId == null || hyperateId.isEmpty()) {
      throw new IllegalArgumentException("Hyperate ID cannot be null or empty");
    }
    if (timeout < 0) {
      throw new IllegalArgumentException("timeout cannot be negative");
    }
    if (savePath == null || savePath.isEmpty()) {
      throw new IllegalArgumentException("Save path cannot be null or empty");
    }
    if (subjectID == null || subjectID.isEmpty()) {
      throw new IllegalArgumentException("Subject ID cannot be null or empty");
    }

    this.hyperateId = hyperateId;
    this.timeout = timeout;
    this.savePath = savePath;
    this.subjectID = subjectID;

    try {
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      String apiKey = "9X0nFDdWrUx2m5n2pkj0NMy96Xb9f3WzNtSFRTvLzppGzFMRr7DluVY5w2PEcGWw";
      String address = "wss://app.hyperate.io/socket/websocket?token=" + apiKey;
      this.session = container.connectToServer(this, new URI(address));
    } catch (DeploymentException | IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method will create a web socket connection to the server.
   *
   * @param session the session that the connection will be made in
   * @param config  the endpoint that the connection will be made to
   */

  @OnOpen
  public void onOpen(Session session, EndpointConfig config) {
    this.session = session;
    this.file = new File(savePath + "/" + "hr_sub_ " + this.subjectID + ".csv");
    String login = new JSONObject()
            .put("topic", "hr:" + hyperateId)
            .put("event", "phx_join")
            .put("payload", "{}")
            .put("ref", 0)
            .toString();
    JSONObject loginParams = new JSONObject(login);

    //Sets up the timer to keep the connection open
    try {
      session.getBasicRemote().sendText(loginParams.toString());
      responseBeat.schedule(new TimerTask() {
        @Override
        public void run() {
          respondToKeepOpen();
        }
      }, 0, 3000);

      //Sets up the timer to close the connection if no data is received
      closeBeat.schedule(new TimerTask() {
        @Override
        public void run() {
          stopApplication();
        }
      }, timeout * 2000L);

    } catch (IOException e) {
      e.printStackTrace();
    }

    session.addMessageHandler(new MessageHandler.Whole<String>() {
      /**
       * Called when the message has been fully received.
       *
       * @param message the message data.
       */
      @Override
      @OnMessage
      public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        if (json.has("event")) {
          if (json.getString("event").equals("hr_update")) {
            closeBeat.cancel();
            closeBeat = new Timer();
            closeBeat.schedule(new TimerTask() {
              @Override
              public void run() {
                stopApplication();
              }
            }, timeout * 2000L);

            Calendar.getInstance().getTimeInMillis();
            System.out.println("Heart Rate: " + json.getJSONObject("payload").getInt("hr"));
            data.put(new Timestamp(System.currentTimeMillis()),
                    json.getJSONObject("payload").getInt("hr"));
          }
        }
      }
    });
  }


  /**
   * This method will disconnect from the web socket.
   *
   * @param session the session that the connection will be disconnected from
   */
  @OnClose
  public void onClose(Session session, CloseReason reason) {
    this.stopApplication();
    System.out.println("Application closed");
  }

  /**
   * This method will be called when an error occurs.
   *
   * @param session   the session that the error occurred in
   * @param throwable the throwable that was thrown
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    System.out.println(throwable.getMessage());
  }

  /**
   * This method will start the application.
   */
  @Override
  public void start() {
    System.out.println("Application started");
  }

  /**
   * This method will retrieve the most recent heart rate.
   *
   * @return the most recent heart rate
   */
  @Override
  public Integer getHeartRate() {
    HashMap<Timestamp, Integer> copy = new HashMap<>(this.data);
    Timestamp mostRecent = new Timestamp(0);
    for (Timestamp t : copy.keySet()) {
      if (mostRecent == null || mostRecent.before(t)) {
        mostRecent = t;
      }
    }
    return copy.get(mostRecent);
  }

  private void fileWriter() throws IOException {
    // Create file if it doesn't exist
    if (!file.exists()) {

      file.createNewFile();

    }
    // File to write data to if savePath is not null
    FileWriter fileWriter = new FileWriter(file, true);
    //write header to csv file if it is empty

    if (file.length() == 0) {
      fileWriter.write("Timestamp,Heart Rate (BPM) \n");
    }
    if (getHeartRate() == null) {
      fileWriter.write(new Timestamp(System.currentTimeMillis()) + "," + "0" + "\n");
    } else {
      fileWriter.write(new Timestamp(System.currentTimeMillis()) + "," + getHeartRate() + "\n");
    }
    fileWriter.close();
  }

  /**
   * This method will send a message to the web socket to keep the connection open.
   */
  private void respondToKeepOpen() {
    try {
      this.session.getBasicRemote().sendText(new JSONObject()
              .put("topic", "phoenix")
              .put("event", "heartbeat")
              .put("payload", "{}")
              .put("ref", 0)
              .toString());
      //write to file if savePath is not null
      if (this.savePath != null) {
        fileWriter();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method will stop the application.
   */
  public void stopApplication() {
    this.closeBeat.cancel();
    this.responseBeat.cancel();
    //stop the connection
    try {
      this.session.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
