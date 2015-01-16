package burtis.modules.passengers;

import java.util.logging.Logger;

import burtis.modules.passengers.model.BusManager;
import burtis.modules.passengers.model.BusStopManager;
import burtis.modules.passengers.model.PassengerManager;
import burtis.modules.passengers.model.TransactionManager;

public class Managers
{

    /**
     * Reference to BusStopManager.
     */
    private BusStopManager busStopManager;
    
    /**
     * Reference to the transaction manager.
     */
    private TransactionManager transactionManager;
    
    /**
     * Reference to the bus manager.
     */
    private BusManager busManager;
    
    /**
     * Reference to the passenger manager.
     */
    private PassengerManager passengerManager;
    
    /**
     * Reference to the logger.
     */
    private Logger logger;
  
    public Logger getLogger()
    {
        return logger;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @return the busStopManager
     */
    public BusStopManager getBusStopManager()
    {
        return busStopManager;
    }

    /**
     * @return the transactionManager
     */
    public TransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    /**
     * @return the busManager
     */
    public BusManager getBusManager()
    {
        return busManager;
    }

    /**
     * @return the passengerManager
     */
    public PassengerManager getPassengerManager()
    {
        return passengerManager;
    }

    /**
     * @param busStopManager the busStopManager to set
     */
    public void setBusStopManager(BusStopManager busStopManager)
    {
        this.busStopManager = busStopManager;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(TransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    /**
     * @param busManager the busManager to set
     */
    public void setBusManager(BusManager busManager)
    {
        this.busManager = busManager;
    }

    /**
     * @param passengerManager the passengerManager to set
     */
    public void setPassengerManager(PassengerManager passengerManager)
    {
        this.passengerManager = passengerManager;
    }   
    
}
