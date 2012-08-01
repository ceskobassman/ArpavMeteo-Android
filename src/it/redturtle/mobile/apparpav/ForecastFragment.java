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

import it.redturtle.mobile.apparpav.types.Forecast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author Nicola Senno
 */
public final class ForecastFragment extends Fragment {

	private Activity activity;
	public static String currentBulletinid;
	private Forecast forecast;

	
	public static ForecastFragment newInstance(Activity activity, Forecast f, String bulletinid ) {

		
		currentBulletinid=bulletinid;
		ForecastFragment fragment = new ForecastFragment();
		fragment.forecast = f;
		fragment.activity = activity;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.showbulletin, container,false);

		// Set button in order to start the bulletin fragmnet
		final Button mv = (Button) view.findViewById(R.id.mv);
		mv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newintent = new Intent();
				newintent.setClass(activity, BulletinActivity.class);
				newintent.putExtra("bulletinid", "MV");
				startActivity(newintent);
			}
		});

		// Set button in order to start the bulletin fragmnet
		final Button dm = (Button) view.findViewById(R.id.dm);
		dm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newintent = new Intent();
				newintent.setClass(activity, BulletinActivity.class);
				newintent.putExtra("bulletinid", "DM");
				startActivity(newintent);
			}
		});

		// Set button in order to start the bulletin fragmnet
		final Button pm = (Button) view.findViewById(R.id.pm);
		pm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newintent = new Intent();
				newintent.setClass(activity, BulletinActivity.class);
				newintent.putExtra("bulletinid", "MP");
				startActivity(newintent);
			}
		});
		

		// disable the button corresponding to the current section, for better user navigation 
		if( currentBulletinid.equals("MV") ){
			mv.setEnabled(false);
		}
		else{
			if( currentBulletinid.equals("DM") ){
				dm.setEnabled(false);
			}
			else
				if( currentBulletinid.equals("MP") ){
					pm.setEnabled(false);
				}
		}
		// #####################################
		
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
	    
		//#######################

		WebView webview = (WebView) view.findViewById(R.id.datail_content);
		webview.setWebChromeClient(new WebChromeClient() {});
		webview.getSettings().setJavaScriptEnabled(true); 
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webview.setWebViewClient( new WebViewListener() );

		String html = "<!doctype html>";
		html += "<html>";
		html += "<head>";
		html += "<meta name=\"author\" content=\"RedTurtle.it\" />";
		html += "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />";
		html += "</head>";
		html += "<body style=\"font-family:Helvetica;font-size:small;\">";
		html += "<div style=\"text-align:center;\"><b>"+forecast.getName()+"</b></div>";

		
		if(forecast.getType() == 1){
			html += "<div style=\"text-align:center;\">";
			html +=	"<p>"+forecast.getCaptionAt(0)+"</p>";
			html +=	"<a href=\""+forecast.getPathAt(0)+"\"> <img src=\""+forecast.getPathAt(0)+"\" width=\"140\" style=\" border:0.1em solid LightBlue; padding:1px;\"/>  </a>";
			Log.d("path", forecast.getPathAt(0));
			html += "</div>";
		}
		if(forecast.getType() == 2){

			html += "<div style=\"text-align:center;\">";
			html += "<div style=\"display:inline-block;\">";
			html += "<div style=\"float:left;\">";
			html +=	"<p>"+forecast.getCaptionAt(0)+"</p>";
			html +=	"<a href=\""+forecast.getPathAt(0)+"\"> <img src=\""+forecast.getPathAt(0)+"\" width=\"140\" style=\" border:0.1em solid LightBlue; padding:2px;\"/> </a>";	
			html += "</div>";
			html +=	"<div style=\"float:left;\">";
			html +=	"<p>"+forecast.getCaptionAt(1)+"</p>";
			html +=	"<a href=\""+forecast.getPathAt(1)+"\"><img  src=\""+forecast.getPathAt(1)+"\" width=\"140\" style=\" border:0.1em solid LightBlue; padding:2px;\"/> </a>";
			html +=	"</div>";
			html +=	"</div>";
			html +=	"</div>";
		}

		html += forecast.getBody();
		html += "</body>"; 
		html += "</html>";

		webview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
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
	   };
	}
	//#######################################################
	
	
	
	
	
	// redefine a listener of WebView: it listen if a link is pressed and manages the event 
	private class WebViewListener extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	
	    	
//	        if (Uri.parse(url).getHost().equals("www.example.com")) {
//	            // This is my web site, so do not override; let my WebView load the page
//	            return false;
//	        }
//	        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//	        startActivity(intent);
	    	
//	  	    Toast toast = Toast.makeText( getActivity(), url, Toast.LENGTH_SHORT);
//	  	    toast.show();
//	  	    Log.d("url", url);
	  	    
	    	
	    	
//	        // in onCreate or any event where your want the user to select a file
//	        Intent intent = new Intent( getActivity(), FullScreenActivity.class );
//	        // extra "from" tells at FullScreenActivity from which activity arrives the intent
//	        intent.putExtra("from", "bulletin" );
//	        intent.putExtra("title", forecast.getName() );
//	        intent.putExtra("url", url );
//	        startActivity(intent);
	  	    
	        return true;
	    }
	}
	
	
}
