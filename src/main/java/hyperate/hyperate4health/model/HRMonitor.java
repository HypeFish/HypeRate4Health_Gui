package hyperate.hyperate4health.model;

import java.net.ConnectException;

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
     * This method will create a web socket connection to the server
     */
    void connect() throws ConnectException;

    /**
     * This method will disconnect from the web socket
     */
    void disconnect() throws ConnectException;

    /**
     * This method will retrieve the most recent heart rate
     *
     * @return the most recent heart rate
     */
    int getHeartRate();

    /**
     * This method will retrieve all heart rates
     *
     * @return all heart rates
     */
    int[] getAllHeartRates();

    /**
     * This method will stop the application
     *
     * @return true if the application was stopped, false otherwise
     */
    boolean stopApplication();

    /**
     * This method will write the data collected into a csv file
     *
     * @return true if the data was written to the csv file, false otherwise
     */
    boolean writeData();

}