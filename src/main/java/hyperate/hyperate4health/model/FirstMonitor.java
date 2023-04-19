package hyperate.hyperate4health.model;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.json.*;

/**
 * @author HypeFish
 * @version 1.0
 * <p>
 * This class seeks to implement the basic functionality of the HRMonitor interface. This class will be used
 * to establish a connection to the web socket, retrieve the most recent heart rate, retrieve all heart rates,
 * stop the application, and write the data collected into a csv file.
 * </p>
 * <p>
 * This class will attempt to establish a connection to the web socket. If the connection is successful, the
 * application will be able to retrieve the most recent heart rate, retrieve all heart rates, and write the data
 * collected into a csv file. If the connection is unsuccessful, the application will be able to stop the
 * application. This will be done without the use of a database. A database may be used in the future to store
 * and retrieve data with the help of a proper DBMS.
 * </p>
 */

@ServerEndpoint(value = "/firstMonitor", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class FirstMonitor extends Endpoint implements HRMonitor {
    private Session session;
    private static final Set<FirstMonitor> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> users = new HashMap<>();
    private String hyperateId;
    private int timeout;
    private String topic = "hr:" + hyperateId;
    private String event = "phx_join";
    private String payload = "{}";
    private int ref = 0;
    private String msg = String.format("{\"topic\":\"%s\",\"event\":\"%s\",\"payload\":%s,\"ref\":%d}", topic, event, payload, ref);
    private JSONObject params = new JSONObject(msg);
    private String apiKey = "9X0nFDdWrUx2m5n2pkj0NMy96Xb9f3WzNtSFRTvLzppGzFMRr7DluVY5w2PEcGWw";
    private final String address = "wss://app.hyperate.io/socket/websocket?token=" + apiKey;
    private Timer responseBeat;
    private Timer closeBeat;
    private long lastHrTs = 0;
    private long startTime = Calendar.getInstance().getTimeInMillis();
    private HashMap<Timestamp, Integer> data = new HashMap<>();
    private String savePath;
    private File file;


    /**
     * This constructor will create a new FirstMonitor object
     */
    public FirstMonitor(String hyperateId, int timeout, String apiKey, String savePath) {
        this.hyperateId = hyperateId;
        this.timeout = timeout;
        this.apiKey = apiKey;

        if (savePath.endsWith(".csv")) {
            this.savePath = savePath;
        } else {
            String fileName = "HR_" + hyperateId + "_" +
                    Calendar.getInstance().getTime() + Calendar.getInstance().getTime().getTime() + ".csv";
            this.savePath = savePath + "/" + fileName;
            this.file = new File(savePath);
        }
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, new URI(address));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will create a web socket connection to the server
     *
     * @param session the session that the connection will be made in
     * @param config  the endpoint that the connection will be made to
     */
    @Override
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        chatEndpoints.add(this);
        try {
            session.getBasicRemote().sendText(params.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Begins the recording of the heart rate
     */
    public void beginRecording() {
        this.session = this.makeWebsock();
        this.runWebsock();
    }

    private void runWebsock() {

        this.responseBeat = this.startTimer();
        this.closeBeat = this.startTimer();

    }

    private Session makeWebsock() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, new URI(address));
            return this.session;
        } catch (DeploymentException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method will send a message to the web socket to keep the connection open
     */
    private void respondToKeepOpen() {
        try {
            JSONObject json = new JSONObject();
            json.put("topic", "phoenix");
            json.put("event", "heartbeat");
            json.put("payload", new JSONObject());
            json.put("ref", 0);
            session.getBasicRemote().sendText(json.toString());
            System.out.println("respondToKeepOpen: sent HR, timer_set");

            //this.responseBeat = this.startTimer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a timer that should go off every 20 seconds
     *
     * @return the timer that was created
     */
    private Timer startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                respondToKeepOpen();
            }
        }, 20000);
        return timer;
    }


    /**
     * This method will save the CSV file to the specified filepath.
     * The view will be responsible for getting the filepath from the user through the GUI
     *
     * @param fileName The String of the filepath to save the file to
     * @return the file that was saved
     * @throws IllegalArgumentException if an error occurs while saving the file
     */
    private File saveFile(String fileName) throws IllegalArgumentException {

        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null.");
        }

        File file = new File(fileName);

        if (file.exists()) {
            throw new IllegalArgumentException("File already exists in this location.");
        }

        try {
            File newFile = new File(fileName);
            return newFile.getCanonicalFile();
        } catch (IOException ioException) {
            // Print information regarding the error if there is one.
            throw new IllegalArgumentException();
        }
    }


    /**
     * This method will disconnect from the web socket
     *
     * @param session the session that the connection will be disconnected from
     */
    @Override
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.stopApplication();
        reason.getReasonPhrase();
    }

    /**
     * This method will be called when a message is received from the server
     *
     * @param session the session that the message was received from
     * @param message the message that was received
     * @throws IOException if an error occurs while receiving the message
     */
    @Override
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        JSONObject m = new JSONObject(message);
        if (m.getString("event").equals("hr_update")) {
            this.responseBeat.cancel();
            this.responseBeat = this.startTimer();
            System.out.println("on_message: " + message);
            this.data.put(new Timestamp(System.currentTimeMillis()), m.getJSONObject("payload").getInt("hr"));
            try {
                if (this.file.exists()) {
                    FileWriter fw = new FileWriter(this.file, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);
                    pw.println(System.currentTimeMillis() + "," + m.getJSONObject("payload").getInt("hr"));
                    pw.flush();
                    pw.close();
                } else {
                    System.out.println("WARNING: Original file was moved or deleted. Re-saving entire dataset as " + this.file);
                    toCSV();
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR OCCURRED WHEN SAVING: CRASH SAVING TO " + this.file);
                toCSV();
                this.stopApplication();
            }
        }
    }

    private void toCSV() throws IOException {
        FileWriter fw = new FileWriter(this.file);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("time,hr");
        for (Map.Entry<Timestamp, Integer> entry : this.data.entrySet()) {
            pw.println(entry.getKey().getTime() + "," + entry.getValue());
        }
        pw.flush();
        pw.close();
    }

    private void filepath() throws IOException {
    }

    /**
     * This method will be called when an error occurs
     *
     * @param session   the session that the error occurred in
     * @param throwable the throwable that was thrown
     */
    @Override
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }


    /**
     * This method will retrieve the most recent heart rate
     *
     * @return the most recent heart rate
     */
    @Override
    public Integer getHeartRate() {
        return this.data.entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    /**
     * This method will retrieve all heart rates
     *
     * @return all heart rates
     */
    @Override
    @SuppressWarnings("unchecked")
    public HashMap<Timestamp, Integer> getAllHeartRates() {
        return (HashMap<Timestamp, Integer>) this.data.clone();
    }

    /**
     * This method will stop the application
     */
    @Override
    public void stopApplication() {
        try {
            this.session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.closeBeat.cancel();
        this.responseBeat.cancel();
    }

    /**
     * This method will write the data collected into a csv file
     *
     * @return the file that was written to
     */
    private File writeData() {
        return new File("");
    }

    /**
     * Gets the set of chat endpoints
     * @return the set of chat endpoints
     */
    public Set<FirstMonitor> getChatEndpoints() {
        return chatEndpoints;
    }
}

