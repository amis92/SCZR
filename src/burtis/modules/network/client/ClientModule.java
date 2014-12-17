package burtis.modules.network.client;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.ModuleConfig;

/**
 * Convenient class allowing easy management of connection with server. Supports
 * sending and receiving {@link SimulationEvent}s.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ClientModule
{
    private final ModuleConfig config;
    private final ClientConnection<SimulationEvent> connection;

    /**
     * Creates new client from provided configuration. This configuration is
     * permanently saved in this object.
     * 
     * @param config
     *            Configuration to be used.
     */
    public ClientModule(final ModuleConfig config)
    {
        this.config = config;
        this.connection = new ClientConnection<SimulationEvent>(
                config.getServerAddress(), config.getServerPort());
    }

    /**
     * Closes connection. After that, it may be reconnected again.
     */
    public void close()
    {
        connection.close();
    }

    /**
     * Blocking method. Attempts to connect to server according to initial
     * configuration. Blocks until connection is setup, or timeout is reached.
     * 
     * @throws IOException
     *             - thrown by socket.connect();
     */
    public void connect() throws IOException
    {
        connection.connect();
    }

    /**
     * Allows direct access to queue of incoming events.
     * 
     * @return queue of events received from server.
     */
    public LinkedBlockingQueue<SimulationEvent> getIncomingQueue()
    {
        return connection.incomingQueue;
    }

    /**
     * Name of module as set in configuration.
     * 
     * @return module name.
     */
    public String getModuleName()
    {
        return config.getModuleName();
    }

    /**
     * Checks whether connection is good or not. If connection is changing
     * (opening or closing) it immediately returns false.
     * 
     * @return true if connection is ok, false if it's not, or opening or
     *         closing.
     */
    public boolean isConnected()
    {
        return connection.isConnected();
    }

    /**
     * Sends object immediately. Blocking method, fails silently.
     * 
     * @param event
     *            This event is sent to server.
     */
    public void send(SimulationEvent event)
    {
        connection.send(event);
    }
}
