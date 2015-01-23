package burtis.modules.gui;

import burtis.common.mockups.Mockup;

/**
 * Describes what methods for updating should be defined by view.
 * 
 * @author Rafał Braun
 *
 */
public interface View
{
    /**
     * Load new mockup containing status of current iteration.
     * 
     * @param mockup
     *            - the new mockup to be loaded and displayed.
     */
    public void refresh(Mockup mockup);

    /**
     * Recheck status of server connection. Call when it could've changed.
     */
    public void refreshConnectionStatus();

    /**
     * Update info panel with passengers from bus.
     * 
     * @param busId
     *            - id of bus to read passengers from.
     */
    public void updatePassengerInfoPanel(Integer busId);

    /**
     * Update info panel with passengers from stop.
     * 
     * @param stopName
     *            - name of the stop to read passengers from.
     */
    public void updatePassengerInfoPanel(String stopName);
}
