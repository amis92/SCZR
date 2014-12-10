package burtis.modules.gui.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import burtis.modules.gui.events.ConnectEvent;
import burtis.modules.gui.events.DisconnectEvent;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusEvent;
import burtis.modules.gui.events.ShowBusStopEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;
import burtis.modules.gui.model.Model;
import burtis.modules.gui.view.View;

public class Controller {
	private final View view;	
	private final Model model;
	
	/** Kolejka dla obiektow ProgramEvent. */
	private final BlockingQueue<ProgramEvent> blockingQueue;
	
	/** odwzorowanie obiektow ProgramEvent na obiekty ProgramAction */
	private final Map<Class<? extends ProgramEvent>, ProgramAction> eventActionMap;
	
	/**
	 * Tworzy obiekt typu Controller
	 * 
	 * @param view referencja na widok
	 * @param model referencja na Model
	 * @param blockingQueue kolejka do otrzymywania komunikatow z Widoku
	 */
	public Controller(View view, Model model, BlockingQueue<ProgramEvent> blockingQueue) {
		this.view = view;
		this.model = model;
		this.blockingQueue = blockingQueue;
		eventActionMap = new HashMap<Class<? extends ProgramEvent>, ProgramAction>();
		fillEventActionMap();
	}
	
	/**
	 * zapelnia kontener eventActionMap
	 */
	private void fillEventActionMap(){
		/*
		eventActionMap.put(CloseAppEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.exit(0);
			}
		});
		
		eventActionMap.put(ShowBusStationInfoEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				
			}
		});		
		
		eventActionMap.put(ShowBusInfoEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("show bus");
			}
		});		
		*/
		eventActionMap.put(GoEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Go");
			}
		});
		
		eventActionMap.put(StepEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Step");
			}
		});	
		
		eventActionMap.put(StopEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Stop");
			}
		});	
		
		eventActionMap.put(ConnectEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Connect");
			}
		});	
		
		eventActionMap.put(DisconnectEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Disconnect");
			}
		});
		
		eventActionMap.put(ShowBusEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Show Bus");
				view.updateBusInfoPanel(((ShowBusEvent)e).getId());
			}
		});
		
		eventActionMap.put(ShowBusStopEvent.class, new ProgramAction(){
			public void go(ProgramEvent e){
				System.out.println("Show Bus Stop");
				view.updateBusStopInfoPanel(((ShowBusStopEvent)e).getName());
			}
		});
	}

	/**
	 * funkcja obslugujca komunikaty z widoku w nieskonczonej petki
	 * <br> tzn pobieajaca obiekt z kolejki(blockingQueue) i na jego podstawie uruchamiajaca 
	 * odpowiednie dzialannie z mapy zadan(eventActionMap)
	 * <br>-normalne dzialanie kontrolera :)
	 */
	public void work(){	
		while(true){
			try{
				ProgramEvent event = blockingQueue.take();
				ProgramAction programAction = eventActionMap.get(event.getClass());
				programAction.go(event);
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
}
