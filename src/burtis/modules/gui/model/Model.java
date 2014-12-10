package burtis.modules.gui.model;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import burtis.modules.gui.events.ProgramEvent;

public class Model {
	private LinkedBlockingQueue<ProgramEvent> bQueue = null;

	public Model(LinkedBlockingQueue<ProgramEvent> bQueue) {
		this.bQueue = bQueue;

	}

	
}
