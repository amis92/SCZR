package burtis.modules.common;

/**
 * Extension of ModuleConfig for network module. Adds field critical defining if
 * failure of the module implies stopping simulation.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class ModuleConfig extends burtis.modules.network.ModuleConfig
{
    private boolean finished;
    private boolean ignore;

    public ModuleConfig(String moduleName, String serverAddress, int port,
            boolean isCritical)
    {
        super(moduleName, serverAddress, port, isCritical);
    }

    public void setFinished(boolean state)
    {
        finished = state;
    };

    public void setIgnore(boolean state)
    {
        ignore = state;
    };

    public boolean isFinished()
    {
        return finished;
    }

    public boolean isIgnored()
    {
        return ignore;
    }
}
