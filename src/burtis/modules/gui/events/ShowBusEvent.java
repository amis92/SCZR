package burtis.modules.gui.events;

public class ShowBusEvent extends ProgramEvent {
    private Integer i;
	private ShowBusEvent() {}
	
	public ShowBusEvent(Integer i) {
		this.i = i;
	}
	
	public Integer getId() {
		// TODO Auto-generated method stub
		return i;
	}
}
