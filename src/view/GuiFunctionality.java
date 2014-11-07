package view;

import order.FunctionalityMockupParser;

/**
 * Created by mastah on 27/05/14.
 */
public interface GuiFunctionality extends FunctionalityMockupParser {
    public void connectionEstablished();
    public void connectionLost();
}
