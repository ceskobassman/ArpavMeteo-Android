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

package it.redturtle.mobile.apparpav;

import it.redturtle.mobile.apparpav.types.Row;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Nicola Senno
 */
public class Meteogram implements Serializable {
	private static final long serialVersionUID = 1L;
	private String zoneid; 
	private String name;
	LinkedList<Row> listOfrows;
	
	/**
	 * Costructor, set name and zoneid
	 * @param data
	 */
	public Meteogram(Map<String, String> data){
		this.init(data);
	}

	public LinkedList<Row>  getListOfRows(){
		return listOfrows;
	}
	
	public String getZoneid() {
		return zoneid;
	}

	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setlistOfrows(LinkedList<Row> list){
		listOfrows = list;
	}

	public void init(Map<String, String> mp) {
		@SuppressWarnings("rawtypes")
		Iterator it = mp.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
			if(pairs.getKey().equals("zoneid"))
				setZoneid((String)pairs.getValue());
			else
				setName((String)pairs.getValue());
		}
	}
}
