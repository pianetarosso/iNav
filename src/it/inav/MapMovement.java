package it.inav;

import it.inav.graphics.MapView;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapMovement implements OnTouchListener {

	
	private MapView mapView;

	// pixel che indicano l'inizio e la fine del movimento
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;


	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	public MapMovement(MapView mapView) {
		this.mapView = mapView;
	}




	// recupero la posizione del punto sull'immagine reale invertendo la 
	// matrice di traslazione / ridimensionamento dell'immagine
	private PointF convertPoints(float x, float y) {

		float[] pts = {x, y};
		
		Matrix m = new Matrix();
		
		// elimino lo zoom e la traslazione
	//	mapView.image.invert(m);
		//m.setRotate(-mapView.getBearing());
	//	m.mapPoints(pts);

		return new PointF(pts[0], pts[1]);
	}

	private float[] calculateDistance(PointF a, PointF b) {
		
		float[] pts = {a.x, a.y, b.x, b.y};
		
		Matrix m = new Matrix();
		
		// inverto la rotazione
		//mapView.canvas.invert(m);
		//m.invert(m);
		//m.mapPoints(pts);
		
		//mapView.image.mapPoints(pts);
		//m.mapPoints(pts);
		
		float diff_x = (pts[2] - pts[0]);// * mapView.zoom;// mapView.zoom;
		float diff_y = (pts[3] - pts[1]);// * mapView.zoom;// mapView.zoom;
		
		return new float[] {diff_x, diff_y};
	}

	// ONTOUCH
	@Override
	public boolean onTouch(View v, MotionEvent event) {


		// tre casi:

		// TOUCH
		// DRAG
		// PINCH ZOOM



		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

		
			start.set(event.getX(), event.getY());
			Log.d("mode", "mode=DRAG");
			mode = DRAG;
			// blocco il movimento
			mapView.isMoving = true;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:

			oldDist = spacing(event);
			Log.d("mode", "oldDist=" + oldDist);
			if (oldDist > 10f) {

				midPoint(mid, event);
				mode = ZOOM;
				Log.d("mode", "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d("mode", "mode=NONE");
			mapView.isMoving = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {

				boolean test = mapView.setMovement(start, new PointF(event.getX(), event.getY()));
				
				 if (test)
					 start.set(event.getX(), event.getY());
			}
			else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d("mode", "newDist=" + newDist);
				if (newDist > 10f) {
					float scale = newDist / oldDist;
					mapView.setZoom(scale, mid);
				}
			}
			break;
		}
		
		// Dump touch event to log
		//dumpEvent(event);

		// NECESSARIO PER LA LETTURA DEL TOUCH!!!!!
		return true;
	}

	/** Determine the space between the first two fingers */
	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	///////////////////////////////////////////////////////////////////////////////
	// LOG DEGLI EVENTI
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
				"POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_" ).append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid " ).append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")" );
		}
		sb.append("[" );
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#" ).append(i);
			sb.append("(pid " ).append(event.getPointerId(i));
			sb.append(")=" ).append((int) event.getX(i));
			sb.append("," ).append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";" );
		}
		sb.append("]" );
		Log.d("event", sb.toString());
	}
	///////////////////////////////////////////////////////////////////////////////////
}
