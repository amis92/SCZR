/**
 * 
 */
package burtis.modules.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.MainMockupEvent;
import burtis.common.events.SimulationEvent;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.gui.controller.Controller;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.view.View;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;

import com.sun.istack.internal.logging.Logger;

/**
 * Initializes GUI with network connection.
 * 
 * @author Rafał
 *
 */
public class GuiModule extends AbstractNetworkModule
{
    private static final Logger logger = Logger.getLogger(GuiModule.class);
    private final LinkedBlockingQueue<ProgramEvent> queue = new LinkedBlockingQueue<ProgramEvent>();
    private View view;
    private Controller controller;

    public GuiModule(ModuleConfig config)
    {
        super(config);
    }

    /**
     * Creates view, assigns event handler, creates Controller and calls
     * {@link #initializeModule()}.
     */
    @Override
    protected void init()
    {
        createView();
        eventHandler = new EventHandler();
        controller = new Controller(view, queue, this::send);
        controller.start();
        try
        {
            initializeModule();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates view which handles gui termination.
     * 
     * @param queue
     */
    private void createView()
    {
        WindowAdapter onExit = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                terminate();
                System.exit(0);
            }
        };
        view = new View(queue, onExit);
    }

    @Override
    protected void terminate()
    {
        closeModule();
        controller.stop();
    }

    public static void main(String[] args)
    {
        ModuleConfig config = NetworkConfig.defaultConfig().getModuleConfigs()
                .get(NetworkConfig.GUI_MODULE);
        GuiModule guiModule = new GuiModule(config);
        SwingUtilities.invokeLater(() -> guiModule.init());
    }

    /**
     * Handles mockup reception.
     * 
     * @author Amadeusz Sadowski
     *
     */
    private class EventHandler extends AbstractEventHandler
    {
        @Override
        public void process(MainMockupEvent event)
        {
            view.refresh(event.getMainMockup());
        }

        @Override
        public void defaultHandle(SimulationEvent event)
        {
            logger.warning("Unhandled event: " + event);
        }
    }
}
