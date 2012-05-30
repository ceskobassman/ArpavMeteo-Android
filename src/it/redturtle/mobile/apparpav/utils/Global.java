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

package it.redturtle.mobile.apparpav.utils;

import it.redturtle.mobile.apparpav.Radar;
import it.redturtle.mobile.apparpav.types.Bulletin;
import it.redturtle.mobile.apparpav.types.Forecast;
import it.redturtle.mobile.apparpav.types.Meteogram;
import it.redturtle.mobile.apparpav.types.Municipality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

/**
 * Singleton
 * @author Nicola Senno
 */

public class Global {

	private Map<String, Object> meteograms 	= null;
	private Map<String, Object> bulletins 	= null;
	private Map<String, Object> provinces 	= null;
	private LinkedList<Radar> radars  = null; 
	private List<Municipality> prefered 	= null;

	private static Global DATA = null;

	/** 
	 * Return a static istance of this class
	 * @return Global
	 */
	public static Global istance() {
		if(null == DATA)
			DATA = new Global();

		return DATA;
	}

	/**
	 * Adds a list of preference to the singleton
	 * @param p List<Municipality> list of Municipality saved by the user
	 */
	public void updatePref(List<Municipality> p){
		prefered = p;
	}

	/**
	 * Return a list of ids used to load fragment content
	 * @return
	 */
	public String[] getPrefMunicpalityIds(){
		if(null == prefered) 
			return new String[]{};

		String[] m = new String[prefered.size()];
		for(int i = 0; i < prefered.size(); i++)
			m[i] = prefered.get(i).getId();

		return m;
	}
	
	/**
	 * Retrieves a specific Municipality object by its id
	 * @param key
	 * @return
	 */
	public Municipality getPrefMun(String key){
		if(null == prefered) 
			return null;

		for(Municipality m : prefered)
			if(m.getId().equals(key))
				return m;

		return null;
	}
	
	/**
	 * Set radars value after parser job and call a procesure to store data to file-system 
	 * @param c Application context
	 * @param r LinkedList<Radar>
	 */
	public void setRadars(Context c, LinkedList<Radar> r){
		radars  = r;
		Util.setSavedRadars(c, radars);
	}
	
	/**
	 * Update radars value fron file-system
	 * @param r LinkedList<Radar>
	 */
	public void updateRadars(LinkedList<Radar> r){
		radars  = r;
	}
	
	/**
	 * Return a list of Radar in order to populate RadarFragment
	 * @return LinkedList<Radar>
 	 */
	public LinkedList<Radar> getRadars(){
		if(null == radars) 
			return new LinkedList<Radar>();
		
		return radars;
	}
	
	/**
	 * Set bulletins on singleton after parse and write a copy on file-system
	 * @param c Application context
	 * @param b Map<String, Object> Bulletins map by area
	 */
	public void setBulletins(Context c, Map<String, Object> b) {
		bulletins = b;
		Util.setSavedBulletins(c, b);
	}

	/**
	 * Update bulletins but doesn't write data back on file-system
	 * @param b Map<String, Object> Bulletins map by area
	 */
	public void updateBulletins(Map<String, Object> b) {
		bulletins = b;
	}
	
	
	/** 
	 * Set meteograms after parse and write a copy on file-system
	 * @param c Context
	 * @param m Map<String, Object>
	 */
	public void setMeteograms(Context c, Map<String, Object> m) {
		meteograms = m;
		Util.setSavedMeteograms(c, m);
	}
	
	/** 
	 * Update meteograms but doesn't write data back on file-system
	 * @param m
	 */
	public void updateMeteograms(Map<String, Object> m) {
		meteograms = m;
	}

	/**
	 * Search for meteograms by zoneid in the currenst list 
	 * @param zoneid
	 * @return Meteogram or null
	 */
	public Meteogram getMeteogramsByZoneIDS(String zoneid){
		if(null == meteograms)
			return null;
		
		return (Meteogram) meteograms.get(zoneid);
	}
	
	/**
	 * Set provinces after parse and write a copy on file-system
	 * @param c Application context
	 * @param provinces
	 */
	public void setProcinces(Context c, Map<String, Object> p) {
		provinces = p;
		Util.setSavedProvinces(c, p);
	}
	
	/**
	 * Update provinces but doesn't write data back on file-system
	 * @param p Map<String, Object>
	 */
	public void updateProcinces(Map<String, Object> p) {
		provinces = p;
	}

	/**
	 * Returns a list of provinces from this singleton and sort them by name
	 * if Globals is empty returns null
	 * @return
	 */
	public List<Map<String, Object>> getProvinces() {
		List<Map<String, Object>> listProvince = new ArrayList<Map<String, Object>>();
		if(null == provinces)
			return listProvince;

		Iterator<Entry<String, Object>> it = provinces.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
			HashMap<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("title",pairs.getKey());
			listProvince.add(tmp);
		}

		// Sort list of Provinces
		Collections.sort(listProvince, Util.sDisplayNameComparator);
		return listProvince;
	}

	/**
	 * Returns a list of Municipalities from this singleton and sort them by name
	 * @return
	 */
	public List<Map<String, Object>> getMunicipalities(String key) {
		List<Map<String, Object>> result= new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		ArrayList<Municipality> lm = (ArrayList<Municipality> )provinces.get(key);
		Iterator<Municipality> it = lm.iterator();
		while (it.hasNext()) {
			Municipality m = (Municipality) it.next();
			HashMap<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("title",m.getName());
			tmp.put("municipality",m);
			result.add(tmp);
		}

		Collections.sort(result, Util.sDisplayNameComparator);
		return result;
	}
	
	/**
	 * Returns a list of Forecasts from this singleton 
	 * @return
	 */
	public List<Forecast> getForecastByBulletinID(String bulletinid){
		if(null == bulletins)
			return new ArrayList<Forecast>();

		Bulletin b = (Bulletin) bulletins.get(bulletinid);
		if(null == b)
			return new ArrayList<Forecast>();

		return b.getListOfForecast();
	}
}
