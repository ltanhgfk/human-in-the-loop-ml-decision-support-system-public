/**
 * JWAP - A Java Implementation of the WAP Protocols
 * Copyright (C) 2001-2004 Niko Bender
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.jwap.util;

import java.util.Enumeration;



/**
 * This class is a wrapper for the Log4J Logger. If Log4J is available on
 * the classpath, it will forward calls to Log4Js Logger. 
 * 
 * @author Michel Marti
 */
public class Logger 
{
    private static boolean log4jAvailable;
    private static Logger voidLogger;
    
    private Logger(){}
    
    static
    {
        // Check if log4j is available...
        try {
            Class.forName("org.apache.log4j.Logger");
            log4jAvailable=true;
        } catch(Exception unknown) {
            voidLogger = new Logger();
        }
    }
    
    public static Logger getLogger(Class category)
    {
        return getLogger(category==null?null:category.getName());    
    }
    
    public static Logger getLogger(String category)
    {
        if( log4jAvailable ) {
            return new Log4jLogger(category);
        } else {
            return voidLogger;
        }        
    }
    
    public void debug(Object msg) {}
    public void debug(Object msg, Throwable throwable) {}
    public void error(Object msg) {}
    public void error(Object msg, Throwable throwable) {}
    public void fatal(Object msg) {}
    public void fatal(Object msg, Throwable throwable) {}
    public void info(Object msg) {}
    public void info(Object msg, Throwable throwable) {}
    public boolean isDebugEnabled() {
        return false;
    }
    public boolean isInfoEnabled() {
        return false;
    }
    public void warn(Object msg) {}
    public void warn(Object msg, Throwable throwable) {}

    /**
     * Initialize the logging system if it is not yet initalized.
     */
    public static void initLogSystem(boolean verbose) {
        if(log4jAvailable) {
            Log4jLogger.initLogSystem(verbose);   
        }
    }

    private static final class Log4jLogger extends Logger
    {
        private org.apache.log4j.Logger logger;
        Log4jLogger(String category) {
            logger = org.apache.log4j.Logger.getLogger(category);
        }
        
        public void debug(Object msg, Throwable throwable) {
            logger.debug(msg,throwable);
        }

        public void debug(Object msg) {
            logger.debug(msg);
        }

        public void error(Object msg, Throwable throwable) {
            logger.error(msg,throwable);
        }

        public void error(Object msg) {
            logger.error(msg);
        }

        public void fatal(Object msg, Throwable throwable) {
            logger.fatal(msg,throwable);
        }

        public void fatal(Object msg) {
            logger.fatal(msg);
        }

        public void info(Object msg, Throwable throwable) {
            logger.info(msg, throwable);
        }

        public void info(Object msg) {
            logger.info(msg);
        }

        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        public boolean isInfoEnabled() {
            return logger.isInfoEnabled();   
        }
        
        public void warn(Object msg, Throwable throwable) {
            logger.warn(msg, throwable);
        }

        public void warn(Object msg) {
            logger.warn(msg);
        }
        
        public static final void initLogSystem(boolean verbose)
        {
            if (!isLogSystemConfigured()) {
                org.apache.log4j.BasicConfigurator.configure();
                org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
                org.apache.log4j.Logger.getRootLogger().addAppender(new org.apache.log4j.ConsoleAppender(
                        new org.apache.log4j.PatternLayout("jWAP:\t%m%n"), org.apache.log4j.ConsoleAppender.SYSTEM_OUT));
                org.apache.log4j.Logger.getRootLogger().setLevel(verbose ? org.apache.log4j.Level.INFO: org.apache.log4j.Level.WARN);
            }
        }        

        private static final boolean isLogSystemConfigured() {
            Enumeration enum1 = org.apache.log4j.Logger.getRoot().getAllAppenders();

            if ((enum1 != null) &&
                    !(enum1 instanceof org.apache.log4j.helpers.NullEnumeration)) {
                return true;
            } else {
                Enumeration loggers = org.apache.log4j.LogManager.getCurrentLoggers();

                while (loggers.hasMoreElements()) {
                    org.apache.log4j.Logger c = (org.apache.log4j.Logger) loggers.nextElement();

                    if (!(c.getAllAppenders() instanceof org.apache.log4j.helpers.NullEnumeration)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
