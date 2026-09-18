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

package net.sourceforge.jwap.util.wbxml;


/**
 * 
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */

public class AttributeNameToken extends Token {

	private String name;

	private String prefix;

	private byte value;
	public AttributeNameToken(String name, String prefix, byte value) {
		this.name = name;
		this.value = value;
		this.prefix = prefix;

	}

	public AttributeNameToken(String name, byte value) {
		this.prefix = "";
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public byte getValue() {
		return value;
	}
	public void setValue(byte value) {
		this.value = value;
	}
}
