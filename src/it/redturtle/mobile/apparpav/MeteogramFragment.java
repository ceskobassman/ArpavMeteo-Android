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

import it.redturtle.mobile.apparpav.types.Meteogram;
import it.redturtle.mobile.apparpav.types.Municipality;
import it.redturtle.mobile.apparpav.utils.Global;
import it.redturtle.mobile.apparpav.utils.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Nicola Senno
 */
public final class MeteogramFragment extends Fragment {

	private String municipalityid;

	
	public static MeteogramFragment newInstance(String mid) {
		MeteogramFragment fragment = new MeteogramFragment();
		fragment.municipalityid = mid; 
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final Context context = this.getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.meteograms, container,false);

		// Built current fragment
		Municipality m = Global.istance().getPrefMun(municipalityid);
		if(null == m){
			Intent newintent = new Intent();
			newintent.setClass(context, MeteogramsActivity.class);
			newintent.putExtra("state", 1);
			this.getActivity().finish();
			startActivity(newintent);
		}
		
		Meteogram meteogram = Global.istance().getMeteogramsByZoneIDS(m.getZoneid());
		if(null == meteogram)
			return view;

		ListView list=(ListView) view.findViewById(R.id.meteogram_items);
		TextView tv = (TextView) view.findViewById(R.id.municipality);
		tv.setTypeface(null, Typeface.BOLD);
		// set the Municipality name
		tv.setText(m.getName());
		

		
		//###########################
		// set listener for the button_swype_left and button_swype_right
	    Button bsl = (Button) view.findViewById(R.id.button_swype_left);
	    bsl.setOnClickListener(new View.OnClickListener() {
	       @Override
	       public void onClick(View v) {
	          pageListener.swypeLeft();
	       }
	    });

		Button bsr = (Button) view.findViewById(R.id.button_swype_right);
	    bsr.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
		    	   pageListener.swypeRight();
		       }
		    });
	    
	    
	    // controll if the button_swype_left and button_swype_right must hide or not
		int control = pageListener.controlSwype(context);
		switch (control) {
		case 0:
			// in this case there is only a municipality saved, so both buttons are hide
			bsl.setVisibility(View.INVISIBLE);
			bsr.setVisibility(View.INVISIBLE);
			break;

		default:
			break;
		}
		//#######################
		
		ListAdapter adapter = new MeteogramAdapter(context, meteogram.getListOfSlot());
		list.setAdapter(adapter);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	//################################################
	// activity listener interface for the button_swype_left and button_swype_right,
	// the interface is implemented in MeteogramsActivity.java so when the button is pressed
	// is automatically called the method in MeteogramsActivity.java
	private OnPageListener pageListener;
	public interface OnPageListener {
	    public void swypeLeft();
	    public void swypeRight();
	    public int controlSwype(Context context);
	}
	
	
	// onAttach : set activity listener
	@Override
	public void onAttach(Activity activity) {
	   super.onAttach(activity);
	   // if implemented by activity, set listener
	   if(activity instanceof OnPageListener) {
	      pageListener = (OnPageListener) activity;
	   }
	   // else create local listener (code never executed in this example)
	   else pageListener = new OnPageListener() {
	      @Override
	      public void swypeLeft() {}
	      public void swypeRight(){};
	      public int controlSwype(Context context){return 0;};
	   };
	}
	//#######################################################
}
