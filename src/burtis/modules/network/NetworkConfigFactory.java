package burtis.modules.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Saves and reads {@link NetworkConfig} into and from {@link Properties}.
 * 
 * Only static methods.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class NetworkConfigFactory
{
    private static final String CONFIG_KEY = "burtis-network-config";
    private static final String MODULE_KEY = CONFIG_KEY + ".module%1$d";
    private static final String MODULE_CRITICAL_KEY = MODULE_KEY
            + ".isCritical";
    private static final String MODULE_NAME_KEY = MODULE_KEY + ".name";
    private static final String MODULE_PORT_KEY = MODULE_KEY + ".port";
    private static final String MODULES_COUNT_KEY = CONFIG_KEY + ".moduleCount";
    private static final String SERVER_ADDRESS_KEY = CONFIG_KEY
            + ".serverAddress";
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";

    /**
     * Reads and returns full configuration.
     */
    public static NetworkConfig CreateFromProperties(final Properties properties)
    {
        String serverAddress = properties.getProperty(SERVER_ADDRESS_KEY);
        int moduleCount = Integer.parseInt(properties
                .getProperty(MODULES_COUNT_KEY));
        List<ModuleConfig> moduleConfigs = new ArrayList<>(moduleCount);
        for (int i = 1; i <= moduleCount; ++i)
        {
            moduleConfigs.add(ReadModuleConfig(properties, serverAddress, i));
        }
        return new NetworkConfig(serverAddress, moduleConfigs);
    }

    /**
     * Saves full configuration.
     */
    public static void SaveToProperties(final NetworkConfig config,
            final Properties properties)
    {
        properties.setProperty(SERVER_ADDRESS_KEY, config.getServerAddress());
        final List<ModuleConfig> moduleConfigs = config.getModuleConfigs();
        final int count = moduleConfigs.size();
        final String countString = Integer.toString(count);
        properties.setProperty(MODULES_COUNT_KEY, countString);
        for (int index = 1; index <= count; ++index)
        {
            SaveModuleToProperties(properties, moduleConfigs.get(index - 1),
                    index);
        }
    }

    /**
     * Reads full configuration of single module.
     */
    private static ModuleConfig ReadModuleConfig(final Properties properties,
            final String serverAddress, final int moduleIndex)
    {
        final String criticalPropertyKey = String.format(MODULE_CRITICAL_KEY,
                moduleIndex);
        final String namePropertyKey = String.format(MODULE_NAME_KEY,
                moduleIndex);
        final String portPropertyKey = String.format(MODULE_PORT_KEY,
                moduleIndex);
        final String criticalString = properties
                .getProperty(criticalPropertyKey);
        final String name = properties.getProperty(namePropertyKey);
        final String portString = properties.getProperty(portPropertyKey);
        final boolean isCritical = criticalString == TRUE_VALUE ? true : false;
        final int port = Integer.parseInt(portString);
        return new ModuleConfig(name, serverAddress, port, isCritical, null);
    }

    /**
     * Saves single module configuration.
     */
    private static void SaveModuleToProperties(final Properties properties,
            final ModuleConfig moduleConfig, final int index)
    {
        final String isCritical = moduleConfig.isCritical() ? TRUE_VALUE
                : FALSE_VALUE;
        final String criticalPropertyKey = String.format(MODULE_CRITICAL_KEY,
                index);
        final String name = moduleConfig.getModuleName();
        final String namePropertyKey = String.format(MODULE_NAME_KEY, index);
        final int port = moduleConfig.getServerPort();
        final String portString = Integer.toString(port);
        final String portPropertyKey = String.format(MODULE_PORT_KEY, index);
        properties.setProperty(criticalPropertyKey, isCritical);
        properties.setProperty(namePropertyKey, name);
        properties.setProperty(portPropertyKey, portString);
    }
}
