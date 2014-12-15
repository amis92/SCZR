package burtis.tests.modules.gui;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;
import burtis.modules.gui.controller.ActionExecutor;
import burtis.modules.gui.controller.Controller;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.view.View;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;

/**
 * Runs GUI with some example data. No network connection.
 * 
 * @author Rafał Braun
 *
 */
public class GuiTest
{
    static LinkedBlockingQueue<ProgramEvent> bQueue;
    static View view;
    static Controller controller;

    public static void main(String[] args)
    {
        bQueue = new LinkedBlockingQueue<ProgramEvent>();
        view = new View(bQueue, null);
        NetworkConfig netConfig = NetworkConfig.defaultConfig();
        ModuleConfig config = netConfig.getModuleConfigs().get(NetworkConfig.GUI_MODULE);
        controller = new Controller(view, bQueue, new ActionExecutor(
                new ClientModule(config), netConfig));
        try
        {
            ArrayList<MockupBus> buses = new ArrayList<MockupBus>();
            ArrayList<MockupBusStop> schedule = new ArrayList<MockupBusStop>();
            ArrayList<MockupPassenger> passengerList = new ArrayList<MockupPassenger>();
            passengerList.add(new MockupPassenger(0, "wfewef1", "fwefwef1"));
            passengerList.add(new MockupPassenger(1, "wfewef2", "fwefwef2"));
            passengerList.add(new MockupPassenger(2, "wfewef3", "fwefwef3"));
            passengerList.add(new MockupPassenger(3, "wfewef4", "fwefwef4"));
            buses.add(new MockupBus(0));
            buses.add(new MockupBus(1));
            buses.add(new MockupBus(2));
            buses.add(new MockupBus(3));
            schedule.add(new MockupBusStop("Warszawa1"));
            schedule.add(new MockupBusStop("Warszawa2"));
            schedule.add(new MockupBusStop("Warszawa3"));
            schedule.add(new MockupBusStop("Warszawa4"));
            schedule.get(0).setPassengerList(passengerList);
            schedule.get(1).setPassengerList(passengerList);
            schedule.get(2).setPassengerList(passengerList);
            schedule.get(3).setPassengerList(passengerList);
            buses.get(0).setPassengerList(passengerList);
            buses.get(1).setPassengerList(passengerList);
            buses.get(2).setPassengerList(passengerList);
            buses.get(3).setPassengerList(passengerList);
            long currentTime = 666;
            Mockup mockup = new Mockup(buses, schedule, currentTime);
            view.refresh(mockup);
        }
        catch (Exception e)
        {
            System.out.println("error:");
            e.printStackTrace();
            System.exit(1);
        }
        controller.start();
    }
}
