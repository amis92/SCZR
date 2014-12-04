package event;

import mockup.Mockup;
import view.SimulatorEvent;

/**
 * Polecenie przetworzenia makiety.
 *
 * @author Maciej Majewski
 */
public class OrderParseMockup extends SimulatorEvent {
    private final Mockup mockup;

    public OrderParseMockup(Mockup mockup) {
        this.mockup = mockup;
    }

    public Mockup getMockup() {
        return mockup;
    }
}
