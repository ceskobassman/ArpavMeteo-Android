package it.redturtle.mobile.apparpav;

import it.redturtle.mobile.apparpav.utils.ImageLoader;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FullScreenActivity extends Activity {

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.full_screen);

        
	    Bundle extras=getIntent().getExtras();
  	    String title=extras.getString("title");
  	    String url=extras.getString("url"); 
        
  	    Toast toast = Toast.makeText(getBaseContext(), title, Toast.LENGTH_SHORT);
  	    toast.setGravity(Gravity.TOP, 0, 0);
  	    toast.show();
  	    
        ImageView image = (ImageView) findViewById(R.id.image);
        ImageLoader imageLoader  = new ImageLoader(this);
        Bitmap bmp = imageLoader.getBitmap(url);
        image.setImageBitmap(bmp);

        
    }
    
    
    @Override
    public void onBackPressed() {
    	Intent newintent = new Intent();
    	newintent.setClass(FullScreenActivity.this, RadarActivity.class);
		startActivity(newintent);
    	
    }
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            finish();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }


    
}
