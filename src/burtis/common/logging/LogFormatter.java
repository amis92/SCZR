package burtis.common.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Console logging formatter.
 * 
 * The pattern is: <LEVEL> <Source class>: <Message>
 *  
 * @author  Mikołaj Sowiński
 *
 */
public class LogFormatter extends Formatter
{
    @Override
    public String format(LogRecord record)
    {
        return record.getLevel() + ": " + record.getMessage() + "(" + record.getSourceClassName() + ")\n";
    }
}
