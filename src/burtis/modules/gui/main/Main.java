package burtis.modules.gui.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import burtis.common.events.Simulation.BusStopsListEvent.BusStop;
import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;
import burtis.modules.gui.controller.Controller;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.model.Model;
import burtis.modules.gui.view.View;

public class Main {
	private static LinkedBlockingQueue<ProgramEvent> bQueue;   // blocking queue to get all events from View and put them into Controller
	private static View view;              // view is responsible for everything user sees
	private static Model model;            // model is responsible for sending simulation events to server
	private static Controller controller;  // controller takes events from bQueue and invokes 'go' method which is different for each event
	private static Mockup mockup;          // mockup presenting situation at given moment
	private static Socket socket = null;   // socket for receiving mockup
    private static InputStream in;
    private static ObjectInputStream oin;
    private static String hostname = "localhost";       // name of server address, may be given in different notations
	private static int port = 2002;                     // port number

	public static void main(String[] args) {
		bQueue = new LinkedBlockingQueue<ProgramEvent>();
		view = new View(bQueue);
		model = new Model(bQueue);
		controller = new Controller(view, model, bQueue);
	    
		/**
		 * Start the controller
		 */
		controller.work();

	    try {
	        /**
	         * Start the connection for receiving information
	         */
	        socket = new Socket(hostname, port);
	        in = socket.getInputStream();
	        oin = new ObjectInputStream(in);
		   
	        /**
	         * The main loop refreshing Gui
	         */
	        while(true) {
	            /** get mockup from stream */
	            mockup = (Mockup)oin.readObject(); 
	            
	            /** if received mockup is not null refresh, don't do anything otherwise */
	            if(mockup != null) {   
	                view.refresh(mockup);
	            }
	            
	        }
	        
	    } catch(Exception e) {
		    System.out.println("Fatal Gui error");
		    e.printStackTrace();
		    System.exit(1);
	    } finally {
	        if(socket != null) {
	            try {
	                socket.close();
	            } catch(IOException e) {
	                // ignore
	            }
	        }
	    }
	}
}