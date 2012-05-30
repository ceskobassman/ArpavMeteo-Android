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
import it.redturtle.mobile.apparpav.types.Row;
import it.redturtle.mobile.apparpav.types.Slot;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;


/**
 * @author Nicola Senno
 */

public class XMLParser {

	/**
	 * Return a Map of tag attributes an values.
	 * @param parser
	 * @return Map<String, String>
	 */
	private static Map<String, String> getAttributes(XmlPullParser parser){
		Map<String, String> attrs = new HashMap<String, String>();
		int n = parser.getAttributeCount();
		if(n != -1) {
			for(int i = 0; i < n; i++){
				String name = parser.getAttributeName(i);
				String value = ((null != parser.getAttributeValue(i))? parser.getAttributeValue(i):"");
				if(null != name)
					attrs.put(name, value);
			}
		}
		return attrs;
	}


	/**
	 * Parser for the bulletin XML, at the end of documet it writes serialized datas on sharedPreferences 
	 * @param context
	 */
	public static boolean getXML(Context context, String data)  {
		
		Map<String, Object> bulletins = new HashMap<String, Object>();	
		Map<String, Object> meteograms = new HashMap<String, Object>();
		LinkedList<Radar> radars = new LinkedList<Radar>();

		try {

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(data));
			boolean meteogrammi = true;

			// Meteogram parse
			Meteogram cSlots = null;	
			LinkedList<Slot> listOfSlot = null;
			LinkedList<Row> listOfrows = null;

			// Bulletin parse
			Bulletin cBulettin = null;
			Forecast cForecast = null;

			while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
				int event = parser.next();
				String value = null != parser.getText() ? parser.getText(): "";
				String name = null != parser.getName() ? parser.getName(): "";
				boolean has_text = value.equals("") ? false : true;


				if (event == XmlPullParser.START_TAG) {
					if(meteogrammi){

						// Add double information: title|cielo|tquota|tvalli|precipitazioni|probabilita|neve|Attendibilita
						if(name.equalsIgnoreCase("double_row"))
							listOfrows.add((new Row(getAttributes(parser)).setType(1))); 

						// Add single information: title|cielo|tquota|tvalli|precipitazioni|probabilita|neve|Attendibilita
						else if(name.equalsIgnoreCase("single_row"))
							listOfrows.add((new Row(getAttributes(parser))).setType(0)); 

						// Instantiate list of informations
						else if(meteogrammi && name.equalsIgnoreCase("slots"))
							listOfSlot = new LinkedList<Slot>();

						// Instantiate information 
						else if(meteogrammi && name.equalsIgnoreCase("slot"))
							listOfrows = new LinkedList<Row>();

						else if(name.equalsIgnoreCase("meteogramma"))
							cSlots = new Meteogram(getAttributes(parser));
						
						else if(name.equalsIgnoreCase("radar"))
							radars.add(new Radar(getAttributes(parser)));

						else if(name.equalsIgnoreCase("data_emissione"))
							Util.setDataEmissione(parser, context);

					} else {
						if(name.equalsIgnoreCase("img"))
							cForecast.setImage(getAttributes(parser));

						else if(name.equalsIgnoreCase("slot"))
							cForecast = new Forecast();

						else if(name.equalsIgnoreCase("bollettino"))
							cBulettin = new Bulletin(getAttributes(parser));
					}

				} else if (event == XmlPullParser.END_TAG) {
					if(meteogrammi){
						if(name.equalsIgnoreCase("slot") )
							listOfSlot.add(new Slot(listOfrows));

						if(name.equalsIgnoreCase("slots") )
							cSlots.setlistOfSlot(listOfSlot);

						else if(name.equalsIgnoreCase("meteogramma"))
							meteograms.put(cSlots.getZoneid(), cSlots);

						else if(name.equalsIgnoreCase("meteogrammi"))
							meteogrammi = false;

					} else {
						if(name.equalsIgnoreCase("slot")){
							cForecast.setName(cBulettin.getName());
							cForecast.setTitle(cBulettin.getTitle());
							cBulettin.setForecast(cForecast);
						}

						else if(name.equalsIgnoreCase("bollettino"))
							bulletins.put(cBulettin.getId(), cBulettin);
					}

				} else if (event == XmlPullParser.TEXT && has_text) {
					if(!meteogrammi){
						String testo = parser.getText();
						cForecast.setBody(testo);
					}

				} else if (event == XmlPullParser.END_DOCUMENT) {} 
			}

			// If not empty do globals and preferences get loaded
			if(null != bulletins && bulletins.size() > 0 && null != meteograms && meteograms.size() > 0){
				Global.istance().setBulletins(context, bulletins);
				Global.istance().setMeteograms(context, meteograms);
				Global.istance().setRadars(context,radars);
				FileCache fileCache = new FileCache(context);
				fileCache.clear();
				fileCache = null;
				return true;
			}
			
			return false;

		} catch (XmlPullParserException em) {
			em.printStackTrace();
			return false;

		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}