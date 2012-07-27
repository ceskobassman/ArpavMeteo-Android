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
import it.redturtle.mobile.apparpav.types.Municipality;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities
 * @author Nicola Senno
 */

public class Util {

	private static final int TAB = 9;
	private static final int NL = 10;
	private static final int CR = 13;

	private static final String KEY_RADARS 	   		= "radars";
	private static final String KEY_BULLETINS  		= "bulletins";
	private static final String KEY_PROVINCES  		= "provinces";
	private static final String KEY_METEOGRAMS 		= "meteograms";
	private static final String KEY_MUNICIPALITIES  = "savedMunicipalities";
	private static final String KEY_LAST_SAVE		= "lastsave";
	
	private static final String SHARED_PREFS_FILE   = "settings";
	private static final String FILE_XML 	 		= "bollettino_app.xml";
	private static final String FILE_PLIST	 		= "comuni_app.plist";
	private static final String URL_XML  	 		= "http://www.arpa.veneto.it/apparpav/bollettino_app.xml";
	private static final String URL_PLIST  	 		= "http://www.arpa.veneto.it/apparpav/comuni_app.xml";

	
	public static void CopyStream(InputStream is, OutputStream os){
		final int buffer_size=1024;
		try {
			byte[] bytes=new byte[buffer_size];
			for(;;){
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}

	/**
	 * This method fill Global data structure.
	 * @param context
	 */
	public static void reloadAll(Context context){
		Global.istance().updateBulletins(Util.getSavedBulletins(context));
		Global.istance().updateMeteograms(Util.getSavedMeteograms(context));
		Global.istance().updateProcinces(Util.getSavedProvinces(context));
		Global.istance().updateRadars(Util.getSavedRadars(context));
		Global.istance().updatePref(Util.getSavedMunicipalities(context));
	}

	/**
	 * Provides a list of municipalitis saved by the user.
	 * @param context
	 * @return
	 */
	public static List<Map<String, Object>> getSavedMunicipalitieslist(Context context){
		List<Municipality> savedMunicipalities = (ArrayList<Municipality>) getSavedMunicipalities(context);
		List<Map<String, Object>> result= new ArrayList<Map<String, Object>>();
		Iterator<Municipality> it = savedMunicipalities.iterator();
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

	/**	--- METEOGRAMS ---
	 * Return a Map of deserialized meteograms objects, saved to allow resume of application after stop()
	 * @param context
	 * @return
	 */
	public static Map<String, Object>  getSavedMeteograms(Context context){
		try {
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			@SuppressWarnings("unchecked")
			Map<String, Object> savedMeteograms = (Map<String, Object>) ObjectSerializer.deserialize(prefs.getString(KEY_METEOGRAMS, ObjectSerializer.serialize(new HashMap<String, Object>())));
			return savedMeteograms;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Serialized and write Meteograms on SharedPreferences
	 * @param context
	 * @param Map<String, Object>, Map of meteograms
	 */
	public static void setSavedMeteograms(Context context, Map<String, Object> m){
		try {
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(KEY_METEOGRAMS, ObjectSerializer.serialize((Serializable) m));
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** --- BULLETINS --- 
	 * Return a Map of deserialized bulletins objects, saved to allow resume of application after stop()
	 * @param context
	 * @return
	 */
	public static Map<String, Object>  getSavedBulletins(Context context){
		try {	
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			@SuppressWarnings("unchecked")
			Map<String, Object> savedBulletins = (Map<String, Object>) ObjectSerializer.deserialize(prefs.getString(KEY_BULLETINS, ObjectSerializer.serialize(new HashMap<String, Object>())));
			return savedBulletins;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Serialized and write Bulletins on SharedPreferences
	 * @param context
	 * @param Map<String, Object>, Map of Bulletins
	 */
	public static void setSavedBulletins(Context context, Map<String, Object> b){
		try {
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(KEY_BULLETINS, ObjectSerializer.serialize((Serializable) b));
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** --- RETRIEVES RADARS --- 
	 * Return a Map of deserialized bulletins objects, saved to allow resume of application after stop()
	 * @param context
	 * @return
	 */
	public static LinkedList<Radar> getSavedRadars(Context context){
		try {	
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			@SuppressWarnings("unchecked")
			LinkedList<Radar>  savedRadars = (LinkedList<Radar>) ObjectSerializer.deserialize(prefs.getString(KEY_RADARS, ObjectSerializer.serialize(new  LinkedList<Radar>())));
			return savedRadars;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** --- WRITE RADARS ---
	 * Serialize and write Radar objects on sharedPreference in order to allow resume whitout connection or not updated 
	 * @param context
	 * @param r
	 */
	public static void setSavedRadars(Context context, LinkedList<Radar> r){
		try {
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(KEY_RADARS, ObjectSerializer.serialize((Serializable) r));
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** --- RETRIEVES PROVINCES --- 
	 * Return a Map of deserialized province objects, saved to allow resume of application after stop()
	 * @param context
	 * @return
	 */
	public static Map<String, Object>  getSavedProvinces(Context context){
		try {
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			@SuppressWarnings("unchecked")
			Map<String, Object> savedProvinces = (Map<String, Object>) ObjectSerializer.deserialize(prefs.getString(KEY_PROVINCES, ObjectSerializer.serialize(new HashMap<String, Object>())));
			return savedProvinces;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** --- WRITE PROVINCES ---
	 * Serialize and write provinces on sharedPreference in order to allow resume whitout connection or not updated 
	 * @param context
	 */
	public static void setSavedProvinces(Context context, Map<String, Object> p){
		try {
			SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(KEY_PROVINCES, ObjectSerializer.serialize((Serializable) p));
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a list of municipalitis saved by the user.
	 * @param context
	 * @return List<Municipality>
	 */
	public static List<Municipality> getSavedMunicipalities(Context context){
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		try {
			@SuppressWarnings("unchecked")
			List<Municipality> savedMunicipalities = (ArrayList<Municipality>) ObjectSerializer.deserialize(prefs.getString(KEY_MUNICIPALITIES, ObjectSerializer.serialize(new ArrayList<Municipality>())));

			Collections.sort(savedMunicipalities,new Comparator<Municipality>() {
				public int compare(Municipality lhs, Municipality rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			return savedMunicipalities;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the list of municipalities saved by the user.
	 * @param context
	 * @return
	 */
	public static void updateSavedMunicipalities(Context context, Municipality m){
		ArrayList<Municipality> mlist = (ArrayList<Municipality>) getSavedMunicipalities(context);
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);

		Editor editor = prefs.edit();
		if(null == mlist)
			mlist = new ArrayList<Municipality>();

		if(notInList(mlist, m))
			mlist.add(m);

		// I have to update the globals too
		Global.istance().updatePref(mlist);

		try {
			editor.putString(KEY_MUNICIPALITIES, ObjectSerializer.serialize((Serializable) mlist));
			editor.putBoolean("isEmptySavedMunicipality", false);
			editor.putBoolean(getVersion(context), false);
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete municipality saved on SharedPreferences by the user
	 * @param context
	 */
	public static void deleteSavedMunicipality(Context context, Municipality m){
		ArrayList<Municipality> mlist = (ArrayList<Municipality>) getSavedMunicipalities(context);
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		if(!isEmptySavedMunicipality(context)){
			ArrayList<Municipality> newlist = new ArrayList<Municipality>();
			for(Municipality mu : mlist)
				if(!mu.getId().equals(m.getId()))
					newlist.add(mu);

			// I have to update the globals too
			Global.istance().updatePref(newlist);

			try {
				Editor editor = prefs.edit();
				editor.putString(KEY_MUNICIPALITIES, ObjectSerializer.serialize((Serializable) newlist));
				if(newlist.size() < 1)
					editor.putBoolean("isEmptySavedMunicipality", true);

				editor.commit();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return false if the element is already saved a preference
	 * @param mlist
	 * @param boolean
	 */
	private static boolean notInList(ArrayList<Municipality> mlist,Municipality m ){
		if(null == mlist || null == m)
			return false;

		for(Municipality mu : mlist)
			if(mu.getId().equals(m.getId()))
				return false;

		return true;
	}

	/**
	 * The method read XML from a file, skips irrelevant chars and serves back the content as string. 
	 * @param inputStream
	 * @return String : content of a file 
	 * @throws IOException
	 */
	public static String getFileToString(Context context, String xml_file) throws IOException{
		InputStream inputStream = context.getAssets().open(xml_file);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			int i = inputStream.read();
			while (i != -1){
				if(i != TAB && i != NL && i != CR)
					byteArrayOutputStream.write(i);		
				i = inputStream.read();
			}
			return byteArrayOutputStream.toString("UTF-8");

		} catch (IOException e) {
			e.printStackTrace();
			return "";

		} finally {
			inputStream.close();
		}
	}

	/**
	 * The method read a XML from URL, skips irrelevant chars and serves back the content as string. 
	 * @param inputStream
	 * @return String : content of a file 
	 * @throws IOException
	 */
	public static String getURLToString(URL url) throws IOException{
		InputStreamReader inputStream =  new InputStreamReader(url.openStream(), "ISO-8859-1");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			int i = inputStream.read();
			while (i != -1){
				if(i != TAB && i != NL && i != CR)
					byteArrayOutputStream.write(i);		
				i = inputStream.read();
			}

			String x  = byteArrayOutputStream.toString("UTF-8");
			return x;

		} catch (IOException e) {
			e.printStackTrace();
			return "";

		} finally {
			inputStream.close();
		}
	}

	/** 
	 * Test if the app hasn't be installed before
	 * @param context
	 * @return
	 */
	public static boolean isFirstRun(Context context){
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getBoolean(getVersion(context), true);
	}

	/**
	 * Test if user hasn't save a preference yet.
	 * @param context
	 * @return
	 */
	public static boolean isEmptySavedMunicipality(Context context){
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getBoolean("isEmptySavedMunicipality", true);
	}


	/**
	 *  Simple hashmap sorter (by key-title)
	 */
	public final static Comparator<Map<String, Object>> sDisplayNameComparator = new Comparator<Map<String, Object>>() {
		private final Collator   collator = Collator.getInstance();
		public int compare(Map<String, Object> map1, Map<String, Object> map2) {
			return collator.compare(map1.get("title"), map2.get("title"));
		}
	};

	/**
	 * Get bulletin emission date and write it on sharedPreferences
	 * @param parser
	 * @param context
	 * @return currentstate DATA_EMISSIONE
	 */
	public static int setDataEmissione(XmlPullParser parser, Context context){
		if(parser.getAttributeCount() == 1)			
			try {
				SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
				Editor editor = prefs.edit();
				editor.putString("bulletinEmissionDate", ObjectSerializer.serialize(""));
				editor.commit();

			} catch (IOException e) {
				e.printStackTrace();
			}
		return 1;
	}


	/**
	 * Return the XML file in the case of first run. It means that if there isn't an available connection data will grabbed from the bundle file
	 * @param context
	 * @return
	 */
	public static String getXMLFirstRun(Context context){
		try {
			return Util.isNetworkAvailable(context)? Util.getURLToString(new URL(URL_XML)): Util.getFileToString(context, FILE_XML);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Retrieve new XML data from internet
	 * @param context
	 * @return
	 */
	public static String getXML(Context context){
		try {
			return Util.getURLToString(new URL(URL_XML));

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}


	/**
	 * Return the PLIST file in the case of first run. It means that if there isn't available connection data will grabbed from the bundle file
	 * @param context
	 * @return
	 */
	public static String getPLISTFirstRun(Context context){
		try {
			return Util.isNetworkAvailable(context)? Util.getURLToString(new URL(URL_PLIST)): Util.getFileToString(context, FILE_PLIST);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Retrieve new PLIST data from internet
	 * @param context
	 * @return
	 */
	public static String getPLIST(Context context){
		try {
			return Util.getURLToString(new URL(URL_PLIST));

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Check for internet connection
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}


	/**
	 * Check if data are updated
	 * @param context
	 * @return boolean
	 */
	public static boolean isUpdated(Context context){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int current_hours = calendar.get(Calendar.HOUR_OF_DAY);
		String last_save = Util.getLastSave(context);
		if(current_hours > 1 && current_hours <= 13 && last_save.equals("PM"))
			return false;
		else if(current_hours > 13 && current_hours <= 24 && last_save.equals("AM"))
			return false;

		return true;
	}

	/**
	 * Returns when the last save has been done: AM or PM
	 * @param context
	 * @return
	 */
	public static String getLastSave(Context context){
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getString(KEY_LAST_SAVE, "PM");
	}

	/**
	 * Set when the last update has been performed
	 * @param context
	 */
	public static void setLastSave(Context context){
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int current_hours = calendar.get(Calendar.HOUR_OF_DAY);
		// if current hours is > 13 save PM else save AM
		editor.putString(KEY_LAST_SAVE, current_hours > 13? "PM":"AM");
		editor.commit();
	}


	private static String getVersion(Context context){

		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return "version"+info.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "default";
		}
	}

	// method called every time the app is updating
	public static void updateIsFirstRun(Context context){
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(getVersion(context), false);
		editor.commit();

	}

}