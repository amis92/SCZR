package burtis.modules.gui;

import burtis.common.mockups.Mockup;

public interface View
{
    public void updateBusInfoPanel(Integer id);

    public void updateBusStopInfoPanel(String name);

    public void refresh(Mockup mockup);
    
    public void refreshConnectionStatus();
}
