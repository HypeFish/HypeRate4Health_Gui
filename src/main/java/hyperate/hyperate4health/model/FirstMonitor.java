package hyperate.hyperate4health.model;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.*;
import javax.websocket.server.*;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.json.*;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

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
@ServerEndpoint(value = "/chat/{username}")
public class FirstMonitor implements HRMonitor {
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
    private String address = String.format("wss://app.hyperate.io/socket/websocket?token=%s", this.apiKey);
    private Timer responseBeat;
    private Timer closeBeat;
    private long lastHrTs = 0;
    private long startTime = Calendar.getInstance().getTimeInMillis();
    private HashMap<Timestamp, Integer> data = new HashMap<>();
    private String apiKey;
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
    }

    /**
     * This method will create a web socket connection to the server
     *
     * @param session  the session that the connection will be made in
     * @param username the username that will be used to connect to the web socket (maybe hyperate ID)
     * @throws IOException if an error occurs while connecting to the web socket
     */
    @Override
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);
        session.getBasicRemote().sendText(params.toString());
        System.out.println("on_open: Opened connection");
        System.out.println("on_open: timer_set");
        this.startTimer();
        System.out.println("Connection to Hyperate established with ID " + hyperateId + "\n(i) For a better visual go to: app.hyperate.io/" + hyperateId);

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

            this.responseBeat = this.startTimer();

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
     * @throws IOException if an error occurs while disconnecting from the web socket
     */
    @Override
    public void onClose(Session session) throws IOException {
        this.stopApplication();
    }

    /**
     * This method will be called when a message is received from the server
     *
     * @param session the session that the message was received from
     * @param message the message that was received
     * @throws IOException if an error occurs while receiving the message
     */
    @Override
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
        CsvSchema schema;
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        schemaBuilder.addColumn("UNIX_TIMESTAMP");
        schemaBuilder.addColumn("HR");
        schema = schemaBuilder.build().withHeader();
        CsvMapper mapper = new CsvMapper();
        mapper.writer(schema).writeValue(this.file, this.data);
    }

    private void filepath() throws IOException {
    }

    /**
     * This method will be called when an error occurs
     *
     * @param session   the session that the error occurred in
     * @param throwable the throwable that was thrown
     * @throws IOException if an error occurs while handling the error
     */
    @Override
    public void onError(Session session, Throwable throwable) throws IOException {
        throw new IOException("Error occurred in session " + session.getId());
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
    @Override @SuppressWarnings("unchecked")
    public HashMap<Timestamp, Integer> getAllHeartRates() {
        return (HashMap<Timestamp, Integer>) this.data.clone();
    }

    /**
     * This method will stop the application
     *
     * @return true if the application was stopped, false otherwise
     */
    @Override
    public boolean stopApplication() {
        try {
            this.session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.closeBeat.cancel();
        this.responseBeat.cancel();
        return true;
    }

    /**
     * This method will write the data collected into a csv file
     *
     * @return the file that was written to
     */
    private File writeData() {
        return new File("");
    }

    private static void broadcast(Message message) throws IOException, EncodeException {

        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
