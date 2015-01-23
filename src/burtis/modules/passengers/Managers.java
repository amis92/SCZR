package burtis.modules.passengers;

import java.util.logging.Logger;

import burtis.modules.passengers.model.BusManager;
import burtis.modules.passengers.model.BusStopManager;
import burtis.modules.passengers.model.PassengerManager;
import burtis.modules.passengers.model.TransactionManager;

/**
 * Groups all managers for convenient access across classes. Service locator of
 * sorts. Simple getters and setters.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class Managers
{
    private BusManager busManager;
    private BusStopManager busStopManager;
    private Logger logger;
    private PassengerManager passengerManager;
    private TransactionManager transactionManager;

    public BusManager getBusManager()
    {
        return busManager;
    }

    public BusStopManager getBusStopManager()
    {
        return busStopManager;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public PassengerManager getPassengerManager()
    {
        return passengerManager;
    }

    public TransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    public void setBusManager(BusManager busManager)
    {
        this.busManager = busManager;
    }

    public void setBusStopManager(BusStopManager busStopManager)
    {
        this.busStopManager = busStopManager;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    public void setPassengerManager(PassengerManager passengerManager)
    {
        this.passengerManager = passengerManager;
    }

    public void setTransactionManager(TransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
}
