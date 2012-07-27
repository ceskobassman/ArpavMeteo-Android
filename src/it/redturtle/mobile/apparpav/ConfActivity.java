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

import it.redturtle.mobile.apparpav.types.Municipality;
import it.redturtle.mobile.apparpav.utils.Global;
import it.redturtle.mobile.apparpav.utils.Util;

import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Nicola Senno
 */
public class ConfActivity extends Activity implements OnItemClickListener {	
	final static int PREFERENCE 	= 1;
	final static int PROVINCE 		= 2;
	final static int MUNICIPALITY 	= 3;
	final static int SAVEPREFRENCE 	= 4;

	private int currentstate = PREFERENCE;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.preferences);

		Intent intent = getIntent();
		currentstate = intent.getIntExtra("state", 1);
		String key = intent.getStringExtra("key");

		// Set button in order to start the bulletin fragmnet
		final Button mv = (Button) this.findViewById(R.id.add);
		mv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newintent = new Intent(ConfActivity.this, ConfActivity.class);
				newintent.putExtra("state", PROVINCE);
				startActivity(newintent);
			}
		});

		if(currentstate == PROVINCE){
			List<Map<String, Object>> provinces = Global.istance().getProvinces();
			if(provinces.size() < 1)
				Util.reloadAll(this);

			updateDisplay(provinces);
		}

		else if(currentstate == MUNICIPALITY){
			List<Map<String, Object>> provinces = Global.istance().getProvinces();
			if(provinces.size() < 1)
				Util.reloadAll(this);

			updateDisplay(Global.istance().getMunicipalities(key));
		}

		else 
			updateDisplay(Util.getSavedMunicipalitieslist(this));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_conf, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent newintent = new Intent(new Intent(this.getApplicationContext(), MeteogramsActivity.class));
		newintent.putExtra("state", PREFERENCE);
		startActivity(newintent);
		return super.onOptionsItemSelected(item);
	}


	protected void updateDisplay(List<Map<String, Object>> listElements){
		ListView list = null;
		if(currentstate == PREFERENCE){
			list = (ListView)findViewById(R.id.list);
			list.setAdapter(new PreferencesAdapter(this, listElements, currentstate));
		} else {
			this.setContentView(R.layout.choices);
			list = (ListView)findViewById(R.id.provinceslist);
			list.setAdapter(new ChoiceAdapter(this, listElements, currentstate));
		}

		list.setOnItemClickListener(this);
	}


	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);

		if(currentstate == PREFERENCE){
			Util.deleteSavedMunicipality(v.getContext(), (Municipality) (Municipality) map.get("municipality"));
			Toast toast = Toast.makeText(this, "Località eliminata con successo!", Toast.LENGTH_SHORT);
			toast.show();
			currentstate--;
		}
		currentstate++;
		if(currentstate == SAVEPREFRENCE){
			List<Map<String, Object>> savedMunicipalities = Util.getSavedMunicipalitieslist(this);
			if(savedMunicipalities.size() < 10){
				Util.updateSavedMunicipalities(ConfActivity.this.getApplicationContext(), (Municipality) map.get("municipality"));
				Toast toast = Toast.makeText(this, "Località aggiunta con successo!", Toast.LENGTH_SHORT);
				toast.show();
				
			} else {
				Toast toast = Toast.makeText(this, "Il limite località di località memorizzabile è stato raggiunto", Toast.LENGTH_SHORT);
				toast.show();
			}
			currentstate = PREFERENCE;
		}

		Intent newintent = new Intent();
		newintent.setClass(this, ConfActivity.class);
		newintent.putExtra("state", currentstate);
		newintent.putExtra("key", (String)map.get("title"));
		startActivity(newintent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK ) {
			Intent newintent = new Intent();
			newintent.setClass(this, MeteogramsActivity.class);
			newintent.putExtra("state", 1);
			startActivity(newintent);
	        return true;
	    } 
	    return super.onKeyDown(keyCode, event);
	}
}