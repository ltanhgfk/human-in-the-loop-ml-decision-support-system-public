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
package net.sourceforge.jwap.wsp;

import java.net.InetAddress;


/**
 * @author Niko Bender
 */
public interface IWSPUpperLayer2 extends IWSPUpperLayer {
    public void s_connect_cnf();

    public void s_suspend_ind(short reason);

    public void s_resume_cnf();

    public void s_disconnect_ind(short reason);

    public void s_disconnect_ind(InetAddress[] redirectInfo);

    public void s_disconnect_ind(CWSPSocketAddress[] redirectInfo);
    
    public void s_methodResult_ind(CWSPResult result);
}
