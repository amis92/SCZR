package pl.rbraun.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.rbraun.events.ProgramEvent;
import pl.rbraun.view.View;

public class Main {
	static LinkedBlockingQueue<ProgramEvent> bQueue;
	static View view;
	
	public static void main(String[] args) {
		bQueue = new LinkedBlockingQueue<ProgramEvent>();
		view = new View(bQueue);
	}
}
