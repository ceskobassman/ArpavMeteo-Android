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

import it.redturtle.mobile.apparpav.types.Municipality;
import it.redturtle.mobile.apparpav.types.Zone;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

/**
 * Parser the content of a .plist file. All data are stored on Global singleton.
 * @author Nicola Senno
 */

public class PLISTParser {

	public static final String SHARED_PREFS_FILE = "settings";
	public static final int ROOT	= 0;
	public static final int KEY		= 1;
	public static final int INTEGER	= 2;
	public static final int STRING	= 3;
	
	public static boolean getPlist(Context context, String data)  {
		Map<String, Object> zones = new HashMap<String, Object>();
		Map<String, Object> provinces = new HashMap<String, Object>();

		try {
			int currentstate    = ROOT;
			int pos_dict 		= 0;
			String lastKey 		= "";
			String name 		= "";

			boolean parsing_zones = true;
			boolean has_text = false;

			List<Municipality> municOfCurrentProvince = null;
			Municipality cMuni = null;
			Zone cZone = null;

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(data));

			while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
				int event = parser.next();
				String value = null != parser.getText() ? parser.getText(): "";
				has_text = value.equals("") ? false : true;
				name = parser.getName();

				if (event == XmlPullParser.START_TAG) {
					if(name.equalsIgnoreCase("plist")||name.equalsIgnoreCase("array"))
						currentstate = ROOT;

					if(name.equalsIgnoreCase("dict")){
						currentstate = ROOT;
						pos_dict++;
					}

					if(name.equalsIgnoreCase("key"))
						currentstate = KEY;

					if(name.equalsIgnoreCase("integer"))
						currentstate = INTEGER;

					if(name.equalsIgnoreCase("string"))
						currentstate = STRING;
				}

				else if (event == XmlPullParser.END_TAG) {
					if(name.equalsIgnoreCase("dict")){
						if(pos_dict > 0) 
							pos_dict--;
					}

					if(name.equalsIgnoreCase("array"))
						parsing_zones = false;
				}

				else if (event == XmlPullParser.TEXT && has_text) {
					switch (currentstate) {
					
					case KEY:
						lastKey = value;
						break;

					case INTEGER:
						if(pos_dict == 2)
							cZone = new Zone(value);

						if(pos_dict == 3 && lastKey.equals("id"))
							cMuni = new Municipality(value);

						if(pos_dict == 3 && lastKey.equals("zoneid"))
							municOfCurrentProvince.add(cMuni.setZoneid(value));
						
						break;

					case STRING:
						if(pos_dict == 2){
							if(parsing_zones)
								zones.put(cZone.getId(), cZone.setName(value));
							else {
								municOfCurrentProvince = new ArrayList<Municipality>();
								provinces.put(value, municOfCurrentProvince);
							}
						}

						if(pos_dict == 3)
							cMuni.setName(value);
						
						break;

					default:
						break;
					}
				}

				else if (event == XmlPullParser.END_DOCUMENT) {
					currentstate = ROOT;
				}
			}
			
			// If not empty do globals and preferences get loaded
			if(null != provinces && provinces.size() > 0){
				Global.istance().setProcinces(context, provinces);
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
