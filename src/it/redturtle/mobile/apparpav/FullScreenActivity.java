package it.redturtle.mobile.apparpav;

import it.redturtle.mobile.apparpav.utils.ImageLoader;
import it.redturtle.mobile.apparpav.utils.WrapMotionEvent;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FullScreenActivity extends Activity implements OnTouchListener {
	
	// indicates from with activity the call is arrived (ex. "bulletin", "radar" .. )
	String from;
	
	private static final String TAG = "Touch";
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	   
	   
	// Limit zoomable/pannable image
	private ImageView imageView;
	private float[] matrixValues = new float[9];
	private float maxZoom;
	private float minZoom;
	private float height;
	private float width;
	private RectF viewRect;

	public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.full_screen);
        imageView = (ImageView) findViewById(R.id.full_screen_image);

	    Bundle extras=getIntent().getExtras();
	    // the extra "from" tells from which activity comes the intent
	    from=extras.getString("from");
  	    String title=extras.getString("title");
  	    String url=extras.getString("url");
	    
	    Bitmap bitmap=null;
	    	    
	    // if the intent arrives from BulletinActivity (ForecastFragment)
	    if(from.equals("bulletin")){
	    	bitmap= ImageLoader.loadBitmapFromUrl(url);

	    }
	    // if the intent arrives from RadarFragment (RadarFragment)
	    else if(from.equals("radar")){
	        ImageLoader imageLoader  = new ImageLoader(this);
	        bitmap = imageLoader.getBitmap(url);
	    }
	    

    	if( bitmap==null  ){ // if image is not load, set a default images thak tells to user to update datas or to connect
    		Log.d("non caricataaaa", "aaa");
    		// provo a settare l'immagine sotto che comunica che non è stata aggiornata l'applicazione e l'immagine non è disponibile
    		//int stub_id= R.drawable.no_network;
    		//imageView.setImageResource(stub_id);
    		// altra prova, non va
//    		bmp = BitmapFactory.decodeResource( getResources(),
//                    R.drawable.no_network);
    		// da provare
//    		Drawable logo = context.getResources().getDrawable(R.drawable.banner);
//    	    setBackgroundDrawable(logo);

    		imageView.setImageBitmap(bitmap);
    		Toast toast = Toast.makeText(getBaseContext(), "Immagine non disponibile, aggiorna i dati o connettiti", Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.BOTTOM, 0, 0);
    		toast.show();
    	}
    	
    	else{ // resizes the image proportionally, for full screen dimensions
    		// screen dimension
    		Display display = getWindowManager().getDefaultDisplay(); 
    		int displayWidth = display.getWidth();  // deprecated
    		int displayHeight = display.getHeight();  // deprecated
    		// new bitmap resized
    		Bitmap bitmapResized=null;
    		
    		if( displayWidth < displayHeight ){ // portrait mode orientation
    			// proportion: bitmapWidth:displayWidth = bitmapHeight:newHeight -> newHeight= (displayWidth*bitmapHeight)/bitmapWidth
    			int newHeight = ( displayWidth * bitmap.getHeight() ) / bitmap.getWidth();
    			bitmapResized = Bitmap.createScaledBitmap(bitmap, displayWidth , newHeight, true);
    			
    		}
    		else{ // landscape mode orientation
    			// proportion: bitmapHeight:displayHeight = bitmapWidth:newWidth -> newWidth= (displayHeight*bitmapWidth)/bitmapHeight
    			int newWidth = ( displayHeight * bitmap.getWidth() ) / bitmap.getHeight();
    			bitmapResized = Bitmap.createScaledBitmap(bitmap, newWidth , displayHeight, true);
    		}
    		
    		imageView.setImageBitmap(bitmapResized);
    		imageView.setOnTouchListener(this);    		
    		
    		// Work around a Cupcake bug
    		matrix.setTranslate(1f, 1f);
    		imageView.setImageMatrix(matrix);
  	    
    		Toast toast = Toast.makeText(getBaseContext(), title, Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.BOTTOM, 0, 0);
    		toast.show();
    	}

    }
    
	
    @Override
    public void onBackPressed() {
    	Intent newintent = new Intent();
    	if( from.equals("bulletin"))
    		newintent.setClass(FullScreenActivity.this, BulletinActivity.class);
    	else{ if( from.equals("radar"))
    		newintent.setClass(FullScreenActivity.this, RadarActivity.class);
    	}
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

    

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	if(hasFocus){
    		init();
    	}
    }

    private void init() {
    	// set the max zoom and min zoom values
    	maxZoom = 4;
    	minZoom = 0.3f;
    	height = imageView.getDrawable().getIntrinsicHeight();
    	width = imageView.getDrawable().getIntrinsicWidth();
    	viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent rawEvent) {
    	WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
    	ImageView imageView = (ImageView) v;

    	// Dump touch event to log
    	dumpEvent(event);

    	// Handle touch events here...
    	switch (event.getAction() & MotionEvent.ACTION_MASK) {
    	case MotionEvent.ACTION_DOWN:
    		savedMatrix.set(matrix);
    		start.set(event.getX(), event.getY());
    		Log.d(TAG, "mode=DRAG");
    		mode = DRAG;
    		break;
    	case MotionEvent.ACTION_POINTER_DOWN:
    		oldDist = spacing(event);
    		Log.d(TAG, "oldDist=" + oldDist);
    		if (oldDist > 10f) {
    			savedMatrix.set(matrix);
    			midPoint(mid, event);
    			mode = ZOOM;
    			Log.d(TAG, "mode=ZOOM");
    		}
    		break;
    	case MotionEvent.ACTION_UP:
    	case MotionEvent.ACTION_POINTER_UP:
    		mode = NONE;
    		Log.d(TAG, "mode=NONE");
    		break;
    	case MotionEvent.ACTION_MOVE:
    		if (mode == DRAG) {
    			matrix.set(savedMatrix);
    			
    			// limit pan
    			matrix.getValues(matrixValues);
    			float currentY = matrixValues[Matrix.MTRANS_Y];
    			float currentX = matrixValues[Matrix.MTRANS_X];
    			float currentScale = matrixValues[Matrix.MSCALE_X];
    			float currentHeight = height * currentScale;
    			float currentWidth = width * currentScale;
    			float dx = event.getX() - start.x;
    			float dy = event.getY() - start.y;
    			float newX = currentX+dx;
    			float newY = currentY+dy;
    			
    			RectF drawingRect = new RectF(newX, newY, newX+currentWidth, newY+currentHeight);
    			float diffUp = Math.min(viewRect.bottom-drawingRect.bottom, viewRect.top-drawingRect.top);
    			float diffDown = Math.max(viewRect.bottom-drawingRect.bottom, viewRect.top-drawingRect.top);
    			float diffLeft = Math.min(viewRect.left-drawingRect.left, viewRect.right-drawingRect.right);
    			float diffRight = Math.max(viewRect.left-drawingRect.left, viewRect.right-drawingRect.right);
    			if(diffUp > 0 ){
    				dy +=diffUp;
    			}
    			if(diffDown < 0){
    				dy +=diffDown;
    			}
    			if( diffLeft> 0){
    				dx += diffLeft;
    			}
    			if(diffRight < 0){
    				dx += diffRight;
    			}
    			matrix.postTranslate(dx, dy);
    		}
    		else if (mode == ZOOM) {
    			float newDist = spacing(event);
    			Log.d(TAG, "newDist=" + newDist);
    			if (newDist > 10f) {
    				matrix.set(savedMatrix);
    				float scale = newDist / oldDist;

    				matrix.getValues(matrixValues);
    				float currentScale = matrixValues[Matrix.MSCALE_X];

    				// limit zoom
    				if (scale * currentScale > maxZoom) {
    					scale = maxZoom / currentScale;
    				} else if (scale * currentScale < minZoom) {
    					scale = minZoom / currentScale;
    				}
    				matrix.postScale(scale, scale, mid.x, mid.y);
    			}
    		}
    		break;
    	}

    	imageView.setImageMatrix(matrix);
    	return true; // indicate event was handled
    }
    

    /** Show an event in the LogCat imageView, for debugging */
    private void dumpEvent(WrapMotionEvent event) {
    	String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
    			"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
    	StringBuilder sb = new StringBuilder();
    	int action = event.getAction();
    	int actionCode = action & MotionEvent.ACTION_MASK;
    	sb.append("event ACTION_").append(names[actionCode]);
    	if (actionCode == MotionEvent.ACTION_POINTER_DOWN
    			|| actionCode == MotionEvent.ACTION_POINTER_UP) {
    		sb.append("(pid ").append(
    				action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
    		sb.append(")");
    	}
    	sb.append("[");
    	for (int i = 0; i < event.getPointerCount(); i++) {
    		sb.append("#").append(i);
    		sb.append("(pid ").append(event.getPointerId(i));
    		sb.append(")=").append((int) event.getX(i));
    		sb.append(",").append((int) event.getY(i));
    		if (i + 1 < event.getPointerCount())
    			sb.append(";");
    	}
    	sb.append("]");
    	Log.d(TAG, sb.toString());
    }

    
    /** Determine the space between the first two fingers */
    private float spacing(WrapMotionEvent event) {
    	float x = event.getX(0) - event.getX(1);
    	float y = event.getY(0) - event.getY(1);
    	return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, WrapMotionEvent event) {
    	float x = event.getX(0) + event.getX(1);
    	float y = event.getY(0) + event.getY(1);
    	point.set(x / 2, y / 2);
    }    
}
