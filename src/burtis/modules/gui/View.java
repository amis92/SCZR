package burtis.modules.gui;

import burtis.common.mockups.Mockup;

public interface View
{
    /**
     * Update info with passengers from bus.
     * 
     * @param busId
     *            - id of bus to read passengers from.
     */
    public void updatePassengerInfoPanel(Integer busId);

    /**
     * Update info with passengers from stop.
     * 
     * @param stopName
     *            - name of the stop to read passengers from.
     */
    public void updatePassengerInfoPanel(String stopName);

    /**
     * Load new mockup containing status of current iteration.
     * 
     * @param mockup
     */
    public void refresh(Mockup mockup);

    /**
     * Recheck status of server connection. Call when it could've changed.
     */
    public void refreshConnectionStatus();
}
