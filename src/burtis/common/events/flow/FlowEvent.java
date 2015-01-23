package burtis.common.events.flow;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

/**
 * Event directed to Sync module. No meaning of itself, provides base to inherit
 * from. Inherited classes can use convenient constructor with default recipient
 * list.
 * 
 * @author Amadeusz Sadowski
 *
 */
public abstract class FlowEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1091627095230885704L;
    /**
     * Only recipient is Sync module.
     */
    public static final String[] defaultRecipients = new String[] { NetworkConfig
            .getModuleName(NetworkConfig.SYNC_MODULE) };

    /**
     * Create default event sent to sync module.
     * 
     * @param sender
     *            - name of sender.
     */
    public FlowEvent(String sender)
    {
        super(sender, defaultRecipients);
    }

    /**
     * Create custom event to send to other recipients.
     * 
     * @param sender
     *            - name of sender.
     * @param recipients
     *            - custom recipient list.
     */
    public FlowEvent(String sender, String[] recipients)
    {
        super(sender, recipients);
    }
}
