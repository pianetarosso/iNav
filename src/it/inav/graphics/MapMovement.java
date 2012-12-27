package it.inav.graphics;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**  
 * Java class, implements OnTouchListener
 * 
 * <br />
 * 
 * Specifico per MapView, comprende la gestione del MOVE, del PINCH ZOOM e del TOUCH.
 * 
 * @author Marco Fedele
 * @category OnTouchListener 
 *  */
public class MapMovement implements OnTouchListener {

	
	/**@see MapView */
	private MapView mapView;

	// POINTF che indicano l'inizio e la fine del movimento /////////////////
	
	/** Indica il punto d'inizio del touch*/
	private PointF start = new PointF();
	
	/** Indica il punto mediano su cui applicare lo zoom */
	private PointF mid = new PointF();
	
	/** "Vecchia" distanza tra le due dita per il calcolo del fattore di zoom */ 
	private float oldDist = 1f;


	///////////////////////////////////////////////////////////////////////////
	
	
	// TIPI DI MOVIMENTO /////////////////////////////////////////////////////
	
	/** Nessun touch */
	/**
	 * 
	 */
	private static final int NONE = 0;
	
	/** È in corso un DRAG*/
	private static final int DRAG = 1;
	
	/** È in corso uno ZOOM */
	private static final int ZOOM = 2;
	
	/** Indica il tipo di azione touch in corso*/
	private int mode = NONE;

	
	
	/** Costruttore: necessita di una MapView 
	 * @see MapMovement*/
	public MapMovement(MapView mapView) {
		this.mapView = mapView;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	// ONTOUCH
	
	/** Intercetto gli eventi onTouch e li gestisco nel modo migliore per la MapView
	 * @see MapView
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		// TOUCH
		// DRAG
		// PINCH ZOOM

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
			// inizio del movimento lo schermo viene "premuto"
			case MotionEvent.ACTION_DOWN:
	
				// imposto il punto di partenza
				start.set(event.getX(), event.getY());
				mode = DRAG;
				
				// blocco il movimento
				mapView.isMoving = true;
				
				break;

			// in questo caso è stato aggiunto un dito che ha effettuato una pressione
			case MotionEvent.ACTION_POINTER_DOWN:
	
				// recupero la distanza
				oldDist = spacing(event);
				
				// verifico che la distanza si maggiore di 10f
				if (oldDist > 10f) {
					
					// calcolo il "punto di mezzo su cui effettuare lo zoom 
					midPoint(mid, event);
					mode = ZOOM;
				}
				
				break;
				
			// entrambi questi casi terminano il movimento
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				
				mode = NONE;
				
				// abilito di nuovo la rotazione della mappa
				mapView.isMoving = false;
				break;
				
			// Movimento!!! qui si distinguono due casi, DRAG e ZOOM
			case MotionEvent.ACTION_MOVE:
				
				// sto "spostando" la mappa
				if (mode == DRAG) {
	
					// "tento" di spostare la MapView
					boolean test = mapView.setMovement(start, new PointF(event.getX(), event.getY()));
					
					// se il test è andato "bene", aggiorno la posizione di start
					if (test)
						start.set(event.getX(), event.getY());
				}
				// sono in modalità ZOOM
				else if (mode == ZOOM) {
					
					// calcolo la nuova distanza
					float newDist = spacing(event);
					
					// se la nuova distanza è maggiore del valore minimo calcolo il fattore di incremento
					// e lo applico allo zoom della MAPVIEW
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

	////////////////////////////////////////////////////////////////////////////////////
	// FUNZIONI ACCESSORIE DI CALCOLO
	
	
	/** Determine the space between the first two fingers 
	 * @param event MotionEvent movimento dal touchListener
	 * @return float la distanza calcolata con Pitagora*/
	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers 
	 * @param point PointF punto che conterrà la metà tra le "due dita"
	 * @param event MotionEvent movimento dal touchListener
	 * @return Void*/
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	///////////////////////////////////////////////////////////////////////////////
	
	
	///////////////////////////////////////////////////////////////////////////////
	// LOGGING
	
	
	/** Log degli eventi touch 
	 * @param event MotionEvent movimento dal touchListener
	 * @return Void*/
	@SuppressWarnings("unused")
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
