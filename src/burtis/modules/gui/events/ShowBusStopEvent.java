package burtis.modules.gui.events;

public class ShowBusStopEvent extends ProgramEvent {
    private String name;
    
	private ShowBusStopEvent() {}
	
	public ShowBusStopEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
