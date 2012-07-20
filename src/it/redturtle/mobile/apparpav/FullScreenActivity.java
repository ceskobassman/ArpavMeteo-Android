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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FullScreenActivity extends Activity implements OnTouchListener {
	
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
	private ImageView view;
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

        
	    Bundle extras=getIntent().getExtras();
  	    String title=extras.getString("title");
  	    String url=extras.getString("url"); 
        
  	    Toast toast = Toast.makeText(getBaseContext(), title, Toast.LENGTH_SHORT);
  	    toast.setGravity(Gravity.BOTTOM, 0, 0);
  	    toast.show();
  	    
        view = (ImageView) findViewById(R.id.full_screen_image);
        
        ImageLoader imageLoader  = new ImageLoader(this);
        Bitmap bmp = imageLoader.getBitmap(url);
        view.setImageBitmap(bmp);

        view.setOnTouchListener(this);
        
     // Work around a Cupcake bug
        matrix.setTranslate(1f, 1f);
        view.setImageMatrix(matrix);
        
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
    	height = view.getDrawable().getIntrinsicHeight();
    	width = view.getDrawable().getIntrinsicWidth();
    	viewRect = new RectF(0, 0, view.getWidth(), view.getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent rawEvent) {
    	WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
    	ImageView view = (ImageView) v;

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

    	view.setImageMatrix(matrix);
    	return true; // indicate event was handled
    }
    

    /** Show an event in the LogCat view, for debugging */
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
