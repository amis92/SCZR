package burtis.modules.network.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.function.Consumer;
import java.util.logging.Logger;

import main.SimulatorConstants;
import burtis.modules.network.AbstractSocketService;

/**
 * Manages connection to specified address/port pair.
 * 
 * Allows for opening, closing and reopening connection. Connection status can
 * be retrieved. Thread-safety: each action on Socket is done in Locked block.
 * 
 * @author Amadeusz Sadowski
 *
 * @param <T>
 *            Received objects are cast to this type, and then consumed by
 *            provided Consumer.
 */
class ClientSocketService extends AbstractSocketService
{
    private static final Logger logger = Logger.getLogger(ClientConnection.class
            .getName());
    private final String serverAddress;

    /**
     * Creates new unconnected client connection to server. Call
     * {@link #connect()} to attempt connection with server.
     * 
     * @param receiveAction
     *            - for every received object, accept is called on this
     *            {@link Consumer}
     */
    public ClientSocketService(final String serverAddress, final int serverPort)
    {
        super(serverPort, logger);
        this.serverAddress = serverAddress;
    }

    @Override
    protected Socket socketFactory() throws IOException
    {
        final SocketAddress address = new InetSocketAddress(serverAddress,
                getPort());
        final Socket socket = new Socket();
        socket.connect(address, SimulatorConstants.connectingTimeout);
        return socket;
    }
}
