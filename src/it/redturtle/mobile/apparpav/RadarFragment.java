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

import it.redturtle.mobile.apparpav.utils.ImageLoader;
import it.redturtle.mobile.apparpav.utils.Util;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Nicola Senno
 */
public final class RadarFragment extends Fragment {
	private Radar radarItem;
	
	
	public static RadarFragment newInstance(Radar r) {
		RadarFragment radar = new RadarFragment();
		radar.radarItem = r;
		return radar;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.showradar, container,false);
		
		TextView location = (TextView) view.findViewById(R.id.radar_location);
		location.setText(radarItem.getElementByName("title"));
		
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
		// ############################
		
		ImageLoader imageLoader = new ImageLoader(this.getActivity());
		ImageView image = (ImageView) view.findViewById(R.id.radar_image);
		imageLoader.DisplayImage(radarItem.getElementByName("img"), this.getActivity(), image);

		// set listener for the radar ImageView image 
	    image.setOnClickListener(new View.OnClickListener() {
	          @Override
	          public void onClick(View v) {
	        	  ImageView image = (ImageView) v.findViewById(R.id.radar_image);
	        	  String tag = null; 
	        	  tag= (String)image.getTag();
	        	  // if radar_image is not loaded, it contains the tag "not_loaded" so FullScreenActivity can't start
	        	  if( tag.equals("not_loaded") ){
	        		  if(Util.isNetworkAvailable(getActivity().getBaseContext()) == false){
		          		  Toast toast = Toast.makeText( getActivity().getBaseContext(), R.string.no_network, Toast.LENGTH_SHORT);
		          		  toast.setGravity(Gravity.BOTTOM, 0, 25);
		          		  toast.show();
		          		  return;
	        		  }
	        		  
	          		  Toast toast = Toast.makeText( getActivity().getBaseContext(), R.string.loading, Toast.LENGTH_SHORT);
	          		  toast.setGravity(Gravity.BOTTOM, 0, 25);
	          		  toast.show();
	          		  return;
	        	  }
	        	  
	        	  Intent intent = new Intent( getActivity(), FullScreenActivity.class );
	        	  // extra "from" tells at FullScreenActivity from which activity arrives the intent
	        	  intent.putExtra("from", "radar" );
	        	  intent.putExtra("title", radarItem.getElementByName("title") );
	        	  intent.putExtra("url", radarItem.getElementByName("img") );
	        	  startActivity(intent);
	          }
        });
		
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	
	//################################################
	// activity listener interface for the button_swype_left and button_swype_right,
	// the interface is implemented in RadarActivity.java so when the button is pressed
	// is automatically called the method in RadarActivity.java
	private OnPageListener pageListener;
	public interface OnPageListener {
	    public void swypeLeft();
	    public void swypeRight();
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
	      public void swypeLeft(){}
	      public void swypeRight(){};
	   };
	}
	//#######################################################
	
	
}
