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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * @author Nicola Senno
 */
public class Radar implements Serializable {
	private static final long serialVersionUID = 1L;

	// radar_row contains radar information ( Map<key, value> ) : 
	// key = "title" contains the name of radar (the location of radar)
	// key = "img" contains the url of image
	private Map<String, String> radar_row = new HashMap<String, String>();

	public Radar(Map<String, String> row){
		this.radar_row = row;
	}

	// if String index=="title" return the name of radar
	// if String index=="img" return the url of image
	public String getElementByName(String index){
		Log.d( "cosa", radar_row.get(index) );  /// !!!!!!!!!!!!!!!!!!!
		return radar_row.get(index);
	}
}