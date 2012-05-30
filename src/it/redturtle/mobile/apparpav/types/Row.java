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
import java.util.HashMap;
import java.util.Map;

/**
 * This class stores values of a single row from XML data.
 * It uses a map in order to keep key and value
 * 
 * es: <single_row title="Temperatura quota" type="image" value="t1.png" />
 * @author Nicola Senno
 */

public class Row implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int TITLE = 0;
	private static final int IMAGE = 1;
	private static final int TEXT  = 2;
	
	//meta info about the row content: 0=title, 1=image, 2=text 
	private int contentType;
	
	//meta info about the row type: 0=single row, 1=double row
	private int type;
	private Map<String, String> row = new HashMap<String, String>();
	
	public Row(Map<String, String> row){
		this.row = row;
		this.setType(row);
	}
	
	public int getType() {
		return type;
	}
	
	public int getContentType() {
		return contentType;
	}
	
	public Row setType(int type) {
		this.type = type;
		return this;
	}
	
	public String getElementByName(String index){
		return row.get(index);
	}

	public Map<String, String> getMap(){
		return row;
	}
	
	private void setType(Map<String, String> r){
		if(r.get("type").equals("title")) 
			contentType = TITLE;
		
		else if (r.get("type").equals("image"))
			contentType = IMAGE;
		
		else 
			contentType = TEXT;
	}
}
