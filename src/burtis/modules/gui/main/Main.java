package burtis.modules.gui.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import burtis.common.mockups.Mockup;
import burtis.common.model.Bus;
import burtis.common.model.Schedule;
import burtis.modules.gui.controller.Controller;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.model.Model;
import burtis.modules.gui.view.View;

public class Main {
	static LinkedBlockingQueue<ProgramEvent> bQueue;
	static View view;
	static Model model;
	static Controller controller;
	
	public static void main(String[] args) {
		bQueue = new LinkedBlockingQueue<ProgramEvent>();
		view = new View(bQueue);
		model = new Model(bQueue);
		controller = new Controller(view, model, bQueue);
		
		try {		    
			ArrayList<Bus> buses = new ArrayList<Bus>(Collections.nCopies(10, new Bus(0)));
			Schedule schedule = new Schedule(20);
			long currentTime = 666;
			
			Mockup mockup = new Mockup(buses, schedule, currentTime);
			view.refresh(mockup);
			
		} catch(Exception e) {
			System.out.println("error:");
			e.printStackTrace();
			System.exit(1);
		}
		
		controller.work();
	}
}