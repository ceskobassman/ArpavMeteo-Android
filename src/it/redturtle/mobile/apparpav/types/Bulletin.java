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
import java.util.List;
import java.util.Map;

/**
 * This class is used to store data about bulletins, in particular save a list of forecasts.
 * @author Nicola Senno
 */

public class Bulletin implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String title;
	
	LinkedList<Forecast> listOfForecast;
	
	public Bulletin(Map<String, String> data){
		listOfForecast = new LinkedList<Forecast>();
		this.id = data.get("bollettinoid");
		this.name = data.get("name");
		this.title = data.get("title");
	}
	
	public String getId(){
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public List<Forecast> getListOfForecast(){
		return listOfForecast;
	}
	
	public void setForecast(Forecast forecast){
		listOfForecast.add(forecast);
	}
}
