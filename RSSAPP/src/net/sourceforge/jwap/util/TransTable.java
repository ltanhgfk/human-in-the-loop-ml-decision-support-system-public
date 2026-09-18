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

import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;


/**
 * This class represent a translation table that may be used to convert
 * well-known integer values to their corresponding string representation.
 *
 * @author Michel Marti
 */
public class TransTable {
    /** RESOURCE_PKG is the name of the constant holding the value
        "/net/sourceforge/jwap/resources" */
    public static final String RESOURCE_PKG = "/net/sourceforge/jwap/resources";
    private static final Logger log = Logger.getLogger(TransTable.class);
    private static Hashtable tables = new Hashtable();
    private Hashtable mib2str;
    private Hashtable str2mib;

    private TransTable() {
        mib2str = new Hashtable();
        str2mib = new Hashtable();
    }

    /**
     * Get a specific translation table. This method uses the class-loader
     * to load the table from a properties file. If the given name is not fully
     * qualified, getTable will lookup the resource in the jWAP's default resource
     * package (net.sourceforge.jwap.resources).
     *
     * @param name the (fully qualified) resource name of the properties holding
     * the table
     * @return a TransTable object
     * @throws RuntimeException if the resource cannot be loaded
     */
    public static TransTable getTable(String table) throws RuntimeException {
        if (table == null) {
            return null;
        }

        // If the resource is not fully qualified, locate it in the jWAP resource
        // package
        if (!table.startsWith("/")) {
            table = new StringBuffer(RESOURCE_PKG).append("/").append(table)
                                                  .toString();
        }

        TransTable tbl = (TransTable) tables.get(table);

        if (tbl == null) {
            synchronized (tables) {
                tbl = (TransTable) tables.get(table);

                if (tbl == null) {
                    tbl = new TransTable();
                    tables.put(table, tbl);
                    InputStream in = TransTable.class.getResourceAsStream(table);

                    if (in == null) {
                        throw new RuntimeException(table +
                            ": Unable to load translation table");
                    }

                    Properties p = new Properties();

                    try {
                        try {
                            p.load(in);
                        } finally {
                            in.close();
                        }
                    } catch (IOException unknown) {
                        throw new RuntimeException(table +
                            ": Unable to load translation table");
                    }

                    for (Enumeration e = p.keys(); e.hasMoreElements();) {
                        String key = (String) e.nextElement();
                        String val = p.getProperty(key).trim();

                        try {
                            Integer code = Integer.decode(key);
                            tbl.mib2str.put(code, val);
                            tbl.str2mib.put(val.toLowerCase(), code);
                        } catch (NumberFormatException nfe) {
                            log.warn(new StringBuffer("TransTable '").append(
                                    table).append("': Skipping non-integer key ")
                                                                     .append(key)
                                                                     .toString());
                        }
                    }
                }
            }
        }

        return tbl;
    }

    /**
     * Lookup the string representation of a code in the translation table
     * @param mib the code to lookup
     * @return the string representation or null if unknown
     */
    public String code2str(int mib) {
        return (String) mib2str.get(new Integer(mib));
    }

    /**
     * Lookup the integer representation of a code
     *
     * @param str the code to lookup
     * @return the integer representation or null if unknown
     */
    public Integer str2code(String str) {
        return (Integer) str2mib.get(str.toLowerCase());
    }
}
