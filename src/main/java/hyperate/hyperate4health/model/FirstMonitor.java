package hyperate.hyperate4health.model;

import java.net.ConnectException;

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
public class FirstMonitor implements HRMonitor {

    /**
     * This constructor will create a new FirstMonitor object
     */
    public FirstMonitor() {

    }

    /**
     * This method will create a web socket connection to the server
     */
    @Override
    public void connect() throws ConnectException {

    }

    /**
     * This method will disconnect from the web socket
     */
    @Override
    public void disconnect() throws ConnectException {

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
    public int[] getAllHeartRates() {
        return new int[0];
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
     * @return true if the data was written to the csv file, false otherwise
     */
    @Override
    public boolean writeData() {
        return false;
    }
}
