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

import it.redturtle.mobile.apparpav.utils.PLISTParser;
import it.redturtle.mobile.apparpav.utils.Util;
import it.redturtle.mobile.apparpav.utils.XMLParser;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

/**
 * @author Nicola Senno
 */
public class MeteogramsActivity extends IndicatorActivity implements MeteogramFragment.OnPageListener {
	private ProgressDialog pd = null;
	private String DEFAULT_BULLETIN = "MV";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_circles);

		if(Util.isFirstRun(this)){
			this.pd = ProgressDialog.show(this, "Working..", "Preparing data...\nNetwork connection is recommended", true, false);
			new InitialTask().execute();

		} else {
			Intent intent = getIntent();
			boolean reload = intent.getBooleanExtra("reload", false);
			if(!reload)
				new UpdateTask().execute();
			
			this.updateDisplay();
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		Intent intent = getIntent();
		boolean reload = intent.getBooleanExtra("reload", false);
		if(reload){
			this.pd = ProgressDialog.show(this, "Working..", "Downloading Data...", true, false);
			new InitialTask().execute();
		}
	}

	public void updateDisplay() {
		// If there is no municipality saved as a favorite
		if(Util.isEmptySavedMunicipality(this)){
			// change the layout from simple_circle (setted at onCreate method)
			setContentView(R.layout.simple_circles_no_pref);
			// listener of Button_favorites
			final Button favorites = (Button) this.findViewById(R.id.button_favorites);
			favorites.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent newintent = new Intent(MeteogramsActivity.this, ConfActivity.class);
					newintent.putExtra("reload", true);
					startActivity(newintent);
				}
			});
		}
		
		// METEO active
		final Button meteo = (Button) this.findViewById(R.id.meteograms);
		// ###########
		// originally: disattiva bottone solo se non ci sono preferiti salvati
		//if(!Util.isEmptySavedMunicipality(this))
			//meteo.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer_reversed));
		// ############
		meteo.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer_reversed));
		
		// START BULLETINS
		final Button bulletins = (Button) this.findViewById(R.id.bulletins);
		bulletins.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer));
		bulletins.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				bulletins.setBackgroundDrawable(MeteogramsActivity.this.getResources().getDrawable(R.drawable.bg_footer_reversed));
				Intent newintent = new Intent();
				newintent.setClass(MeteogramsActivity.this, BulletinActivity.class);
				newintent.putExtra("state", DEFAULT_BULLETIN);
				startActivity(newintent);
			}
		});

		// START RADAR
		final Button radar = (Button) this.findViewById(R.id.radar);
		radar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer));
		radar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				radar.setBackgroundDrawable(MeteogramsActivity.this.getResources().getDrawable(R.drawable.bg_footer_reversed));
				Intent newintent = new Intent();
				newintent.setClass(MeteogramsActivity.this, RadarActivity.class);
				startActivity(newintent);
			}
		});

		mAdapter = new MeteogramFragmentAdapter(getSupportFragmentManager(), getApplicationContext());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		

	}


	
	/**
	 * This task will be executed only the first time the application runs. Retrieves info from 
	 * internet or from file-system if not connection available and load data into Globals singleton. 
	 * Morever if data is correct it will save a serialized list of InitialTaskobject in shared preference
	 */
	private class InitialTask extends AsyncTask<String, Void, Object> {

		protected Object doInBackground(String... args) {
			Context c = MeteogramsActivity.this.getApplicationContext();
			boolean isOKXML = XMLParser.getXML(c, Util.getXMLFirstRun(c));
			boolean isOKPLIST = PLISTParser.getPlist(c, Util.getPLISTFirstRun(c));
			if(isOKPLIST && isOKXML)
				Util.setLastSave(c);
			
			Util.updateIsFirstRun(c);
			return "";
		}

		protected void onPostExecute(Object result) {
			try {
				if (MeteogramsActivity.this.pd != null)
					MeteogramsActivity.this.pd.dismiss();
			} catch (Exception e) {}

			MeteogramsActivity.this.updateDisplay();
		}
	} 

	/**
	 * This task will be executed in back-ground every time the activity restart. 
	 * It retrieves info from internet only if a connetcion is available and if the stored data are out of date.
	 * If new data are correct replace old one from sharedpreference
	 */
	private class UpdateTask extends AsyncTask<String, Void, Object> {

		protected Object doInBackground(String... args) {
			Context c = MeteogramsActivity.this.getApplicationContext();

			// Upade will be skipped if not connection is available and if not the right time: after 1 AM or after 1 PM
			if(!Util.isUpdated(c) && Util.isNetworkAvailable(c)){
				boolean isOKPLIST = PLISTParser.getPlist(c, Util.getPLIST(c));
				boolean isOKXML = XMLParser.getXML(c, Util.getXML(c));

				// set time of last save
				if(isOKPLIST && isOKXML)
					Util.setLastSave(c);
			}
			return "";
		}

		protected void onPostExecute(Object result) {}
	}
	
	
	// #################################
	// implementation of interface OnPageListener declared in MeteogramsFragment.java
	@Override
	public void swypeLeft() {
		
		mPager.setCurrentItem( mPager.getCurrentItem()-1 );
	}
	
	public void swypeRight() {
		mPager.setCurrentItem( mPager.getCurrentItem()+1 );
	}
	
	// to hide the button_swype_left and button_swype_right if there are no swype left or right
	public int controlSwype(Context context){
		int numTotMunicipality = Util.getSavedMunicipalities(context).size();
		
		if(  numTotMunicipality == 1 )
			// hides both buttons
			return 0;
		
		// don't hide buttons
		return 2;
	}
	
	// #################################
	
}