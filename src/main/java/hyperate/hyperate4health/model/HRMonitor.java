package hyperate.hyperate4health.model;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.Session;



/**
 * @author HypeFish
 * @version 1.0
 * <p>
 * This interface will establish the contract for the backend of the Hyperate4Health application. The backend needs
 * to accomplish all the following tasks: It must be able to create, connect to, and disconnect from a web socket,
 * it must be able to retrieve the most recent heart rate, and all heart rates, it must be able to stop the application,
 * and it must be able to write the data collected into a csv file
 * <p>
 * Note: Connection to a database that is then connected to the web socket may be necessary for future
 * storage and retrieval of data.
 **/

public interface HRMonitor {


    /**
     * This method will be called when a message is received from the server
     * @param session the session that the message was received from
     * @param message the message that was received
     * @throws IOException if an error occurs while receiving the message
     */
    void onMessage(Session session, String message) throws IOException;


    /**
     * This method will retrieve the most recent heart rate
     *
     * @return the most recent heart rate
     */
    Integer getHeartRate();

    /**
     * This method will retrieve all heart rates
     *
     * @return all heart rates
     */
    HashMap<Timestamp, Integer> getAllHeartRates();

    /**
     * This method will stop the application
     */
    void stopApplication();

    /**
     * Begins the recording of the heart rate
     */
    void beginRecording();

    /**
     * Gets the set of chat endpoints
     * @return the set of chat endpoints
     */
    Set<FirstMonitor> getChatEndpoints();

}