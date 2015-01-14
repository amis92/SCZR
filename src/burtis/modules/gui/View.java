package burtis.modules.gui;

import burtis.common.mockups.Mockup;

public interface View
{
    /**
     * Update info with passengers from bus.
     * 
     * @param id
     *            - id of bus to read passengers from.
     */
    public void updatePassengerInfoPanel(Integer id);

    /**
     * Update info with passengers from stop.
     * 
     * @param name
     *            - name of the stop to read passengers from.
     */
    public void updatePassengerInfoPanel(String name);

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
