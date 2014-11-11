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
    private static final String CONNECTION_KEY = MODULE_KEY + ".connection%2$d";
    private static final String CONNECTION_NAME_KEY = CONNECTION_KEY
            + ".moduleName";
    private static final String CONNECTIONS_COUNT_KEY = MODULE_KEY
            + ".connectionsCount";
    private static final String MODULE_NAME_KEY = MODULE_KEY + ".name";
    private static final String MODULE_PORT_KEY = MODULE_KEY + ".port";
    private static final String MODULES_COUNT_KEY = CONFIG_KEY + ".moduleCount";
    private static final String SERVER_ADDRESS_KEY = CONFIG_KEY
            + ".serverAddress";

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
            moduleConfigs.add(ReadModuleConfig(properties, i));
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
            final int moduleIndex)
    {
        String namePropertyKey = String.format(MODULE_NAME_KEY, moduleIndex);
        String portPropertyKey = String.format(MODULE_PORT_KEY, moduleIndex);
        String name = properties.getProperty(namePropertyKey);
        String portString = properties.getProperty(portPropertyKey);
        int port = Integer.parseInt(portString);
        List<String> connectedModuleNames = ReadModuleConnections(properties,
                moduleIndex);
        return new ModuleConfig(name, port, connectedModuleNames);
    }

    /**
     * Reads single module's connected module names.
     */
    private static List<String> ReadModuleConnections(
            final Properties properties, final int moduleIndex)
    {
        String countKey = String.format(CONNECTIONS_COUNT_KEY, moduleIndex);
        int count = Integer.parseInt(properties.getProperty(countKey));
        List<String> connectedModuleNames = new ArrayList<>(count);
        for (int i = 1; i <= count; ++i)
        {
            String moduleNameKey = String.format(CONNECTION_NAME_KEY,
                    moduleIndex, i);
            String moduleName = properties.getProperty(moduleNameKey);
            connectedModuleNames.add(moduleName);
        }
        return connectedModuleNames;
    }

    /**
     * Saves single module's connected module names.
     */
    private static void SaveModuleConnections(final Properties properties,
            final List<String> connectedModuleNames, final int index)
    {
        final int count = connectedModuleNames.size();
        final String countString = Integer.toString(count);
        final String countPropertyKey = String.format(CONNECTIONS_COUNT_KEY,
                index);
        properties.setProperty(countPropertyKey, countString);
        for (int i = 1; i <= count; ++i)
        {
            final String namePropertyKey = String.format(CONNECTION_NAME_KEY,
                    index, i);
            final String name = connectedModuleNames.get(i - 1);
            properties.setProperty(namePropertyKey, name);
        }
    }

    /**
     * Saves single module configuration.
     */
    private static void SaveModuleToProperties(final Properties properties,
            final ModuleConfig moduleConfig, final int index)
    {
        final String name = moduleConfig.getModuleName();
        final String namePropertyKey = String.format(MODULE_NAME_KEY, index);
        final int port = moduleConfig.getPort();
        final String portString = Integer.toString(port);
        final String portPropertyKey = String.format(MODULE_PORT_KEY, port);
        properties.setProperty(namePropertyKey, name);
        properties.setProperty(portPropertyKey, portString);
        SaveModuleConnections(properties,
                moduleConfig.getConnectedModuleNames(), index);
    }
}
