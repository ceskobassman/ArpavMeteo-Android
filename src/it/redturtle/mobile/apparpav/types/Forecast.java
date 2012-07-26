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
import java.util.LinkedList;
import java.util.Map;

/**
 * This class stores a single forecast. Each bulletin contain several forecast, usually four.
 * @author Nicola Senno
 */

public class Forecast implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LinkedList<Map<String, String>> listOfImages;
	private String body;
	private String name;
	private String title;
	
	public Forecast(){
		listOfImages = new  LinkedList<Map<String, String>>();
	}

	public void setImage(Map<String, String> img){
		listOfImages.add(img);
	}
	
	public void setBody(String text){
		body = text;
	}
	
	public String getBody(){
		return body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	// return the number of forecast's images 
	public int getType() {
		if(listOfImages.size() == 0)
			return 0;

		return (listOfImages.size() > 1)? 2 : 1;
	}
	
	public String getCaptionAt(int pos){
		return (String) listOfImages.get(pos).get("caption");
	}
	
	public String getPathAt(int pos){
		return (String) listOfImages.get(pos).get("src");
	}
}
