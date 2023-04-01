package hyperate.hyperate4health.model;

import java.io.File;
import java.net.ConnectException;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.Session;
import javax.websocket.server.*;


/**
 * @author HypeFish
 * @version 1.0
 * <p>
 *     This class seeks to implement the basic functionality of the HRMonitor interface. This class will be used
 *     to establish a connection to the web socket, retrieve the most recent heart rate, retrieve all heart rates,
 *     stop the application, and write the data collected into a csv file.
 * </p>
 * <p>
 *     This class will attempt to establish a connection to the web socket. If the connection is successful, the
 *     application will be able to retrieve the most recent heart rate, retrieve all heart rates, and write the data
 *     collected into a csv file. If the connection is unsuccessful, the application will be able to stop the
 *     application. This will be done without the use of a database. A database may be used in the future to store
 *     and retrieve data with the help of a proper DBMS.
 * </p>
 */
@ServerEndpoint(value = "/chat/{username}")
public class FirstMonitor implements HRMonitor {
    private Session session;
    private static final Set<FirstMonitor> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> users = new HashMap<>();

    /**
     * This constructor will create a new FirstMonitor object
     */
    public FirstMonitor() {

    }

    /**
     * This method will create a web socket connection to the server
     *
     * @param session the session that the connection will be made in
     */
    @Override
    public void onOpen(Session session) {

    }

    /**
     * This method will disconnect from the web socket
     *
     * @param session the session that the connection will be disconnected from
     */
    @Override
    public void onClose(Session session) {

    }

    /**
     * This method will be called when a message is received from the server
     *
     * @param session the session that the message was received from
     * @param message the message that was received
     */
    @Override
    public void onMessage(Session session, String message) {

    }

    /**
     * This method will be called when an error occurs
     *
     * @param session   the session that the error occurred in
     * @param throwable the throwable that was thrown
     */
    @Override
    public void onError(Session session, Throwable throwable) {

    }

    /**
     * This method will retrieve the most recent heart rate
     *
     * @return the most recent heart rate
     */
    @Override
    public int getHeartRate() {
        return 0;
    }

    /**
     * This method will retrieve all heart rates
     *
     * @return all heart rates
     */
    @Override
    public HashMap<Timestamp, Integer> getAllHeartRates() {
        return new HashMap<>();
    }

    /**
     * This method will stop the application
     *
     * @return true if the application was stopped, false otherwise
     */
    @Override
    public boolean stopApplication() {
        return false;
    }

    /**
     * This method will write the data collected into a csv file
     *
     * @return the file that was written to
     */
    @Override
    public File writeData() {
        return new File("");
    }
}
