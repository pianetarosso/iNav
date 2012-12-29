package it.inav.graphics;

import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;
import it.inav.base_objects.Point;
import it.inav.base_objects.Room;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;

/* MANCANTE ANCORA: 
 * 		EVIDENZIAZIONE DEI MARKER !!!!!!!!
 * 		TERMINE DEL "VIAGGIO"
 * 
 */


/**
 * Java class, extends ImageView
 * 
 * <br />
 * 
 * View che presenta e gestisce la mappa di iNav.
 * 
 * @author Marco Fedele
 * @category View
 *
 */
public class MapView extends ImageView  {


	/** Orientamento della mappa */
	private float bearing;


	/** Piano attualmente mostrato dalla View 
	 * @see Floor*/
	private Floor selected_floor = null;


	/** Lista di piani derivanti dal Building
	 * @see Building
	 */
	private List<Floor> floors = null;


	/** Immagine attualmente mostrata dala View */
	private Bitmap bmp;


	/** Zoom attuale dell'immagine */
	public float zoom;

	/** Zoom minimo dell'immagine 
	 * @see MapView#setInitialZoom()
	 */
	private float min_zoom;


	/** Centro dello schermo */
	private PointF screen_center = null;

	/** Centro dell'immagine.
	 * <br />
	 * Varia a seconda dello spostamento della mappa eseguito dall'utente o dal sistema.
	 * Le sue coordinate sono indipendenti dallo zoom*/
	private PointF image_center = null;


	/** Matrice di trasformazione dell'immagine: TRASLAZIONE e ZOOM*/
	private Matrix image = new Matrix();

	/** Matrice di trasformazione della canvas: ROTAZIONE */
	private Matrix inverseRotation = new Matrix();


	/** Test per bloccare il movimento
	 * @see MapMovement#onTouch(android.view.View, android.view.MotionEvent) 
	 */
	public boolean isMoving = false;


	/** Diametro del Marker disegnato 
	 * @see MapView#drawMarkers(Canvas)*/
	private static final int MARKER_DIAMETER = 3;

	/** Massimo zoom 
	 * @see MapView#setZoom(float)
	 * @see MapView#setZoom(float, PointF)*/
	private static final int MAX_ZOOM = 1;

	/** Valore minimo di rotazione considerata
	 * @see MapView#setBearing(float)
	 */
	private static final float MIN_ROTATION = (float) 0.1;


	/** Imposta alcuni valori di dimensione e crea le path al primo onDraw */
	private boolean initial_set = true;

	/** Path della freccia, una esterna che fa da bordo e una interna*/
	private Path extPath, intPath;

	/** Colore del bordo della {@link MapView#extPath} */
	private static final int EXT_COLOR = Color.WHITE;

	/** Colore del bordo della {@link MapView#intPath} */
	private static final int INT_COLOR = Color.BLUE;

	/** Colore interno dei marker */
	private static final int MARKER_COLOR = Color.RED;

	/** Colore del bordo dei marker, normale */
	private static final int MARKER_BORDER = Color.WHITE;

	/** Colore del bordo del marker, selezionato */
	private static final int MARKER_SELECTED = Color.YELLOW;
	
	/** Colore della path */
	private static final int PATH_COLOR = Color.GREEN;
	
	/** Dimensione della path */
	private static final int PATH_WIDTH = 2;

	/** Posizione attuale utente */
	private Point user_position = null;

	/** Si sta navigando? */
	public boolean isNavigating = false;

	/** Percorso da seguire sul piano */
	private PointF[] percorso = new PointF[0];



	// COSTRUTTORI /////////////////////////////////////////////////////
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MapView(Context context) {
		super(context);
	}
	////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////
	// FUNZIONI CHIAMATE ALL'INIZIO


	/** Metodo per impostare i piani e inizializzare alcuni parametri tra cui anche il primo piano visualizzato
	 * @param floors List<Floor> lista di piani dell'edificio
	*/
	public void init(List<Floor> floors) {
		
		this.floors = floors;
		
		// inizialmente imposto come numero di piano quello più basso.
		this.setFloor(floors.get(0).numero_di_piano);
		
		// imposto il centro dello schermo
		setScreenCenter();

		// imposto lo zoom iniziale e il centro dell'immagine
		setInitialZoom();

		// preparo le due Path della freccia
		createArrows();
					
		setFocusable(true);
	}


	/** Metodo per calcolare lo zoom iniziale (e minimo) in base alla dimensione 
	 * dell'immagine e dello schermo. 
	 * 
	 * <br />
	 * 
	 * Chiama al termine {@link MapView#setImageCenter(PointF)} con valore NULL per calcolare il centro
	 * dell'immagine.
	 * 
	 * @see MapView#zoom
	 * @see MapView#min_zoom
	 */
	private void setInitialZoom() {

		// se non abbiamo ancora nessuno di questi valori, usciamo
		if ((screen_center == null) || (bmp == null))
			return;

		// recupero la dimensione dell'immagine
		int image_width = bmp.getWidth();
		int image_heigth = bmp.getHeight();

		// trovo il lato più lungo dell'immagine
		int longest_i = image_width;
		if (longest_i < image_heigth)
			longest_i = image_heigth;

		// trovo il lato più lungo dello schermo
		float longest_s = screen_center.x * 2;
		if (longest_s < screen_center.y * 2)
			longest_s = screen_center.y * 2;

		// calcolo lo zoom come rapporto tra le due lunghezze
		float zoom = (float)longest_s / longest_i;

		// imposto i valori
		this.min_zoom = zoom;
		this.zoom = zoom;

		// calcolo il centro dell'immagine
		setImageCenter(null);
	}

	/** Calcolo il centro dell'immagine.
	 * <br />
	 * Si occupa inoltre di impostare lo zoom e calcolare la traslazione della matrice {@link MapView#image} 
	 * per centrare l'immagine 
	 * 
	 * @param posizione PointF Se NULL retituisce il centro dell'immagine, altrimenti imposta le coordinate date
	 * @see MapView#image_center
	 * @see MapView#screen_center
	 * @see MapView#image
	 */
	private void setImageCenter(PointF posizione) {

		// se il punto è nullo restituisco il centro dell'immagine
		if (posizione != null) 
			image_center = new PointF(posizione.x, posizione.y);
		else 
			image_center = new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2);

		// scalo la matrice dell'immagine 
		image = new Matrix();
		image.setScale(zoom, zoom);

		// scalo le coordinate del centro dell'immagine
		float centerScaledWidth = image_center.x * zoom;
		float centerScaledHeigth = image_center.y * zoom;

		// faccio la translazione dell'immagine per centrarla nella matrice
		image.postTranslate(
				screen_center.x -  centerScaledWidth, 
				screen_center.y - centerScaledHeigth);

		this.invalidate();
	}


	/** Calcolo e imposto il centro dello schermo.
	 * <br />
	 * Al termine chiama {@link MapView#setInitialZoom()}
	 * @see MapView#screen_center
	 */
	private void setScreenCenter() {

		// se il centro dello schermo è diverso da NULL, esco
		if (screen_center != null)
			return; 

		// recupero la dimensione dello schermo
		int screen_height = getMeasuredHeight();
		int screen_width = getMeasuredWidth();

		// se i valori sono <= a zero, esco
		if ((screen_height <= 0) || (screen_width <= 0))
			return;

		// imposto il centro
		screen_center = new PointF(screen_width / 2, screen_height / 2);
	}


	/** Creo le path delle frecce del navigatore */
	private void createArrows() {

		float movement = 5f;

		// recupero il centro dello schermo
		float x = screen_center.x;
		float y = screen_center.y;

		// costruisco la path BIANCA
		extPath = new Path();

		extPath.reset();
		extPath.moveTo(x, y + (movement + 1f));
		extPath.lineTo(x + (2 * movement + 1f), y + (2 * movement + 1f));
		extPath.lineTo(x, y - (2 * movement + 1f));
		extPath.lineTo(x - (2 * movement + 1f), y + (2 * movement + 1f));
		extPath.lineTo(x, y + (movement + 1f));
		extPath.close();

		// costruisco la path BLU
		intPath = new Path();

		intPath.reset();
		intPath.moveTo(x, y + movement);
		intPath.lineTo(x + (2 * movement), y + (2 * movement));
		intPath.lineTo(x, y - (2 * movement));
		intPath.lineTo(x - (2 * movement), y + (2 * movement));
		intPath.lineTo(x, y + movement);
		intPath.close();
	}


	////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////
	// FUNZIONI DI INPUT/OUTPUT


	/** Imposto quale immagine mostrare e quale piano sia in evidenza
	 * 
	 * @param floor int numero del piano da mostrare
	 */
	public void setFloor(int floor) {

		// scansiono la lista dei piani in cerca di quello giusto
		for(Floor f : floors) 
			if (f.numero_di_piano == floor) {
				this.selected_floor = f;
				break;
			}

		// se le immagini sono diverse provvedo ad impostarle
		if (bmp != selected_floor.immagine) {
			bmp = selected_floor.immagine;
		}

		this.invalidate();
	}


	/** Imposto lo zoom, costringendolo tra il massimo e minimo.
	 * 
	 * @param zoom float nuovo valore dello zoom
	 * 
	 * @see MapView#zoom
	 * @see MapView#min_zoom
	 * @see MapView#MAX_ZOOM
	 */
	private void setZoom(float zoom) {

		if (zoom < min_zoom)
			this.zoom = min_zoom;

		else if (zoom > MAX_ZOOM)
			this.zoom = MAX_ZOOM;

		else 
			this.zoom = zoom;

		// forzo il redraw dello schermo
		this.invalidate();
	}


	/** Imposto il punto su cui muovere la mappa. Imposto anche un valore di zoom pari a 0.8
	 * <br />
	 * Manca ancora l'animazione del movimento
	 * @param point Point punto su cui posizionare l'immagine
	 * @see MapView#setZoom(float, PointF)
	 */
	public void goToPoint(Point point) {

		// aumento lo zoom e sposto il centro dell'immagine
		setZoom((float) 0.2, point.posizione);
	}

	/** Metodo per muovere la mappa sullo schermo.
	 * <br />
	 * Inverto la matrice del canvas per riposizionare i punti in input, rimuovendo così la rotazione.
	 * Calcolo poi la distanza tra i due punti e verifico che
	 * il movimento non porti il centro dello schermo ad uscire dall'immagine.
	 * Chiamata da {@link MapMovement#onTouch(android.view.View, android.view.MotionEvent)}
	 * 
	 * @param start PointF indica il punto di partenza del movimento
	 * @param stop PointF indica il punto di fine del movimento
	 * 
	 * @return TRUE il movimento è stato effettuato, FALSE indica che non è stato possibile completare l'azione
	 * @see MapView#canvas
	 * @see MapView#image
	 * */
	public boolean setMovement(PointF start, PointF stop) {

		// se sto navigando NON posso effettuare spostamenti
		if (isNavigating)
			return false;
		
		// INVERSIONE DELLA ROTAZIONE //////////////////////////////////////////
		float[] movement = {start.x, start.y, stop.x, stop.y};

		if (isNavigating) {

			// uso la matrice di rotazione inversa per rimappare i punti
			inverseRotation.mapPoints(movement);
		}

		// calcolo lo spostamento sull'asse X e Y
		float dx = movement[2] - movement[0];
		float dy = movement[3] - movement[1];

		////////////////////////////////////////////////////////////////////////

		// CALCOLO DELLA TRASLAZIONE E ZOOM


		float[] new_center = {screen_center.x, screen_center.y};

		// Copio la matrice dell'immagine e applico la traslazione calcolata sopra
		Matrix copy = new Matrix();
		copy.set(image);
		copy.postTranslate(dx, dy);

		// Inverto la matrice copiata e ottengo la posizione sull'immagine del centro dello schermo
		// (non serve eliminare la rotazione, in quanto questo è il perno della rotazione
		Matrix invert = new Matrix();
		copy.invert(invert);
		invert.mapPoints(new_center);

		// imposto la "distanza di sicurezza"
		int safe_distance = MARKER_DIAMETER;

		// verifico se il centro dello schermo è ancora all'interno dell'immagine
		if (
				(new_center[0] > safe_distance) && 
				(new_center[0] <= bmp.getWidth() - safe_distance) && 
				(new_center[1] >= safe_distance) && 
				(new_center[1] <= bmp.getHeight() - safe_distance)) {

			// aggiorno il centro dell'immagine
			image_center.x = new_center[0];
			image_center.y = new_center[1];

			// copio la matrice "copia" su quella originale dell'immagine
			image.set(copy);

			// forzo l'aggiornamento della View
			this.invalidate();

			return true;
		}
		else
			return false;
	}

	/** Imposto lo zoom e il nuovo centro
	 * 
	 * @param zoom float nuovo zoom
	 * @param point PointF nuovo centro dell'immagine
	 * 
	 * @see MapView#setImageCenter(PointF)
	 * @see MapView#setZoom(float)
	 */
	public void setZoom(float zoom, PointF point) {

		setZoom(zoom);
		
		// nel caso in cui sia in corso una navigazione, mantengo lo zoom
		// fisso sullo stesso punto
		if (!isNavigating)
			setImageCenter(point);
	}



	/** Impostazione dell'orientamento.
	 * <br />
	 * Questo metodo viene utilizzato solo se non è in corso un vento touch.
	 * Se il discostamento tra il NUOVO bearing ed il VECCHIO è inferiore a {@link MapView#MIN_ROTATION}
	 * non viene effettuato l'aggiornamento del valore.
	 * L'orientamento viene calcolato secondo questa formula:
	 * <br /> 
	 * (bearing_del_piano - bearing)
	 * 
	 * @param bearing float nuovo orientamento della mappa
	 * @see MapView#isMoving
	 * @throws NullPointerException e imposta il bearing a 0
	 */
	public void setBearing(float bearing) {

		try {
			// se non c'è movimento
			if (!isMoving)	{

				// calcolo il nuovo bearing
				float new_bearing = (float) (-bearing + selected_floor.bearing);

				// verifico se la rotazione è maggiore del valore minimo impostato
				if (Math.abs(this.bearing - new_bearing) > MIN_ROTATION ) {

					// salvo la rotazione
					this.bearing = new_bearing;

					// imposto la matrice inversa di rotazione
					inverseRotation.setRotate(-new_bearing, screen_center.x, screen_center.y);
				}
			}

		} catch (NullPointerException e) {
			this.bearing = 0;
		}
	}


	/** Imposto la posizione dell'utente (predefinito null).
	 * Nel caso si stia effettuando una navigazione, provvedo a ridimensionare l'array {@link #percorso}
	 * eliminando tutti i punti precedenti a questo nell'array.
	 * 
	 * @param position Point posizione dell'utente sulla mappa
	 */
	public void setUserPosition(Point position) {

		this.user_position = position;
		
		goToPoint(user_position);

		if (isNavigating) {

			// elimino tutti gli elementi precedenti al punto "position" sul percorso
			// nel caso si stia navigando

			int new_zero = Integer.MAX_VALUE;
			PointF[] new_percorso = null;

			for (int i=0; i < percorso.length; i++)
				if (percorso[i] == position.posizione) {
					new_zero = i;
					new_percorso = new PointF[percorso.length - i];
				}
				else if (i >= new_zero)
					new_percorso[i - new_zero] = percorso[i];

			percorso = new_percorso;
		}
	}

	/** Imposto una navigazione sul piano in cui si trova l'utente. 
	 * Abilito inoltre la variabile {@link #isNavigating}
	 * Nel caso sia necessario imposto anche il piano.
	 * 
	 * @param list PointF[] array di punti, il primo è {@link #user_position}
	 * @param floor int piano in cui si trova l'utente e il percorso
	 */
	public void setNavigation(Point[] list, int floor) {
		this.percorso = new PointF[list.length];
		
		for(int i=0; i < list.length; i++)
			this.percorso[i] = list[i].posizione;
		
		this.isNavigating = true;
		
		setFloor(floor);
	}
	
	/** Interrompo la navigazione */
	public void stopNavigation() {
		
		isNavigating = false;
		percorso = new PointF[0];
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	// DISEGNO SULL'IMMAGINE


	/** Applica le trasformazioni della matrice all'immagine del piano e la disegna sul canvas
	 * 
	 * @param canvas Canvas dato da {@link MapView#onDraw(Canvas)}
	 */
	private void drawImage(Canvas canvas, Paint paint) {

		// disegno l'immagine con la sua matrice
		canvas.drawBitmap(bmp, image, paint);

		canvas.save();
		canvas.restore();
	}


	/** Disegno dei marker.
	 * Effettuo la scansione dei marker su questo piano e provvedo a disegnarli sulla mappa
	 * 
	 * @param canvas Canvas dato da {@link MapView#onDraw(Canvas)}
	 * @param paint Paint metodo di disegno delle frecce
	 * @see MapView#selected_floor
	 * @see Floor
	 */
	private void drawMarkers(Canvas canvas, Paint paint) {

		for (Point p : selected_floor.punti) {

			Room stanza = p.stanza;

			if (stanza != null) {

				float[] point = {p.posizione.x, p.posizione.y};

				image.mapPoints(point);

				// disegno il bordo bianco
				paint.setColor(MARKER_BORDER);
				canvas.drawCircle(point[0], point[1], MARKER_DIAMETER + 1, paint);

				// disegno il marker
				paint.setColor(MARKER_COLOR);
				canvas.drawCircle(point[0], point[1], MARKER_DIAMETER, paint);
			}
		}

		// salvo il canvas
		canvas.save();
		canvas.restore();
	}



	/** Disegno le frecce del navigatore sul canvas
	 * 
	 * @param canvas Canvas dato da {@link MapView#onDraw(Canvas)}
	 * @param paint Paint metodo di disegno delle frecce
	 */
	private void drawArrows(Canvas canvas, Paint paint) {

		try {
			if (user_position.piano == selected_floor.numero_di_piano) {
				Path copyExtPath = new Path();
				Path copyIntPath = new Path();

				copyExtPath.set(extPath);
				copyIntPath.set(intPath);

				// nel caso non si stia effettuando una navigazione, provvedo a traslare e ruotare
				// le frecce nella posizione opportuna in cui si trova l'utente
				if (!isNavigating) {
					
					// applico la rotazione 
					Matrix nav = new Matrix();
					nav.setRotate(bearing, screen_center.x, screen_center.y);

					copyExtPath.transform(nav);
					copyIntPath.transform(nav);
					
					
					// applico la traslazione, ricordarsi che le frecce sono GIÀ posizionate
					// in image_center, ed è necessario dargli solo un offset
					float dx = -(image_center.x - user_position.posizione.x) * zoom;
					float dy = -(image_center.y - user_position.posizione.y) * zoom;
					
					copyExtPath.offset(dx, dy);
					copyIntPath.offset(dx, dy);

				}
				else {
					// altrimenti applico la rotazione inversa alle path
					copyExtPath.transform(inverseRotation);
					copyIntPath.transform(inverseRotation);
				}

				// le disegno dei ripettivi colori
				paint.setColor(EXT_COLOR);
				canvas.drawPath(copyExtPath, paint);

				paint.setColor(INT_COLOR);
				canvas.drawPath(copyIntPath, paint);

				// salvo il canvas
				canvas.save();
				canvas.restore();
			}
		} catch (NullPointerException e) {
			// non è ancora stata impostata una posizione per l'utente, quindi non faccio nulla
		}
	}

	/** Metodo per disegnare i percorsi sulla mappa
	 * 
	 * @param canvas Canvas dato da {@link MapView#onDraw(Canvas)}
	 * @param paint Paint metodo di disegno
	 */
	private void drawPath(Canvas canvas, Paint paint) {
		
		if (percorso.length > 1) {
			
			for (int i=1; i < percorso.length; i++) {
			
				float[] pts = {percorso[i-1].x, percorso[i-1].y, percorso[i].x, percorso[i].y};
				
				image.mapPoints(pts);
				
				paint.setStrokeWidth(PATH_WIDTH + 2);
				paint.setColor(Color.WHITE);
				canvas.drawLines(pts, paint);
				
				paint.setStrokeWidth(PATH_WIDTH);
				paint.setColor(PATH_COLOR);
				canvas.drawLines(pts, paint);
				
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////








	/** Override of draw.
	 * <br />
	 * Imposto alcuni valori e costruisco alcuni oggetti.
	 * Inoltre disegno i marker, l'immagine e la "freccia" del navigatore
	 * 
	 * @see android.view.View#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {

		if (selected_floor != null) {

			// preparo la Paint
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);

			// ruoto il canvas del valore dato (nel caso ci sia una navigazione)
			if (isNavigating)
				canvas.rotate(bearing, screen_center.x, screen_center.y);

			// disegno gli oggetti
			drawImage(canvas, paint);			
			drawPath(canvas, paint);
			drawMarkers(canvas, paint);
			drawArrows(canvas, paint);
		}

	}

	//////////////////////////////////////////////////////////////////////////////




	//////////////////////////////////////////////////////////////////////////////
	// MISURAZIONE DELLO SCHERMO

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);

		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	/** Calcolo della dimensione dello schermo
	 * 
	 * @param measureSpec int
	 * @return int lo spazio occupato dalla View
	 */
	private int measure(int measureSpec) {
		int result = 0;
		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}

		return result;
	}

	//////////////////////////////////////////////////////////////////////////////////////

}
