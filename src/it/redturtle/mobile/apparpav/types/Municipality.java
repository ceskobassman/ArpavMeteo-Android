/*
 * Apparpav is copyright of Agenzia Regionale per la Prevenzione e
 * Protezione Ambientale del Veneto - Via Matteotti, 27 - 35137
 * Padova Italy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA.
 */
package it.redturtle.mobile.apparpav.types;

import java.io.Serializable;

/**
 * @author Nicola Senno
 */

public class Municipality implements Serializable, Comparable<Object>{
	private static final long serialVersionUID = 1L;
	
	private String id		= "";
	private String name		= "";
	private String zoneid	= "";

	public Municipality (String id){
		this.id = id;
	}
	
	public Municipality (String id, String name, String zoneid){
		this.id = id;
		this.name = name;
		this.zoneid = zoneid;
	}
	
	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getZoneid() {
		return zoneid;
	}

	public Municipality setZoneid(String zoneid) {
		this.zoneid = zoneid;
		return this;
	}

	public int compareTo(Object arg0) {
		return 0;
	}
}


