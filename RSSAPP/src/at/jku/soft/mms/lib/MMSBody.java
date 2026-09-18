package at.jku.soft.mms.lib;

import java.util.Vector;

/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author user
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MMSBody 
{
    public Vector parts = new Vector();
        
    public byte buf[]=null;

    public boolean isMultiPart() {
	return (buf == null);
    }

    /*
    public void addEntry(MMSPart p) {
	parts.add(p);
    }
    */
	
}
