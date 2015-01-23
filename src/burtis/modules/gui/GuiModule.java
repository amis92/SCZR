/**
 * 
 */
package burtis.modules.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.MainMockupEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.TickEvent;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.gui.controller.ActionExecutor;
import burtis.modules.gui.controller.Controller;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.simpleview.SimpleView;
import burtis.modules.network.NetworkConfig;

/**
 * Initializes GUI with network connection.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class GuiModule extends AbstractNetworkModule
{
    private static final Logger logger = Logger.getLogger(GuiModule.class
            .getName());

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            // we can live without system L&F
            logger.info("Failed to set System L&F");
        }
        NetworkConfig config = NetworkConfig.defaultConfig();
        GuiModule guiModule = new GuiModule(config);
        SwingUtilities.invokeLater(guiModule::init);
    }

    private final ActionExecutor actionExecutor;
    private Controller controller;
    private final LinkedBlockingQueue<ProgramEvent> queue = new LinkedBlockingQueue<ProgramEvent>();
    private View view;

    public GuiModule(NetworkConfig config)
    {
        super(config.getModuleConfigs().get(NetworkConfig.GUI_MODULE));
        actionExecutor = new ActionExecutor(this.client, config);
    }

    /**
     * Creates view which handles gui termination.
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
        view = new SimpleView(queue, onExit, this.client::isConnected);
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
        controller = new Controller(view, queue, actionExecutor);
        controller.start();
        try
        {
            initializeModule(false);
        }
        catch (IOException e)
        {
            // can't happen, we don't try to connect
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void terminate()
    {
        controller.stop();
        closeModule();
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
        public void defaultHandle(SimulationEvent event)
        {
            logger.warning("Unhandled event: " + event);
        }

        @Override
        public void process(CycleCompletedEvent event)
        {
            // ignoring silently, that's just ok
        }

        @Override
        public void process(MainMockupEvent event)
        {
            logger.info("Received MainMockup.");
            view.refresh(event.getMainMockup());
            send(new ModuleReadyEvent(moduleConfig.getModuleName(), event
                    .getMainMockup().getCurrentTime()));
        }

        @Override
        public void process(TickEvent event)
        {
            // ignoring silently, waiting for mockup
        }
    }
}
