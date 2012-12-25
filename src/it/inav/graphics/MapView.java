package it.inav.graphics;

import it.inav.base_objects.Floor;
import it.inav.base_objects.Point;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MapView extends ImageView  {

	// orientamento della mappa
	private float bearing;


	// gestore del Building e dei piani
	public Floor selected_floor = null;
	private List<Floor> floors = null;
	private Bitmap bmp;

	// variabili che tengono traccia dello zoom massimo e minimo
	private float zoom;
	private float min_zoom;

	// punti che identificano il centro dello schermo e dell'immagine
	private PointF screen_center = null;
	private PointF image_center = null;

	// matrice delle immagini
	public Matrix image = new Matrix();

	// test del touch per bloaccare il movimento
	public boolean isMoving = false;


	// COSTANTI
	private static final int MARKER_DIAMETER = 3;
	private static final int MAX_ZOOM = 1;


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
	// FUNZIONI DI IMPOSTAZIONI ALL'AVVIO

	// init per impostare i piani all'avvio
	public void init(List<Floor> floors) {
		this.floors = floors;
		setFocusable(true);
	}


	// calcolo lo zoom iniziale in base alla dimensione dell'immagine e dello schermo
	private void setInitialZoom() {

		if ((screen_center == null) || (bmp == null))
			return;

		// recupero la dimensione dell'immagine
		int image_width = bmp.getWidth();
		int image_heigth = bmp.getHeight();


		int longest_i = image_width;
		if (longest_i < image_heigth)
			longest_i = image_heigth;

		float longest_s = screen_center.x * 2;

		if (longest_s < screen_center.y * 2)
			longest_s = screen_center.y * 2;

		float zoom = (float)longest_s / longest_i;

		this.min_zoom = zoom;
		this.zoom = zoom;

		setImageCenter(null);
	}

	// imposto il centro dell'immagine
	private void setImageCenter(PointF posizione) {

		if (posizione != null) 
			image_center = new PointF(posizione.x, posizione.y);
		else 
			image_center = new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2);
	}

	// calcolo e imposto il centro dello schermo
	private void setScreenCenter() {

		if (screen_center != null)
			return; 

		// recupero la dimensione dello schermo
		int screen_height = getMeasuredHeight();
		int screen_width = getMeasuredWidth();

		if ((screen_height == 0) || (screen_width == 0))
			return;

		screen_center = new PointF(screen_width / 2, screen_height / 2);
		setInitialZoom();
	}


	////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////
	// FUNZIONI DI INPUT/OUTPUT


	// imposto il piano in base al numero
	public void setFloor(int floor) {

		for(Floor f : floors) 
			if (f.numero_di_piano == floor) {
				this.selected_floor = f;
				break;
			}

		if (bmp != selected_floor.immagine) {

			bmp = selected_floor.immagine;

			setInitialZoom();
		}
	}




	// imposto lo zoom, tenendomi tra il massimo e minimo
	private void setZoom(float zoom) {

		if (zoom < min_zoom)
			this.zoom = min_zoom;

		else if (zoom > MAX_ZOOM)
			this.zoom = MAX_ZOOM;

		else 
			this.zoom = zoom;

		this.invalidate();

	}

	// imposto il punto su cui effettuare lo zoom 
	public void goToPoint(Point point) {

		// aumento lo zoom e sposto il centro dell'immagine
		setZoom((float) 0.8, point.posizione);
	}

	public void setMovement(float f, float g) {

		image_center.x += f;
		image_center.y += g;

		this.invalidate();
	}

	public void setZoom(float zoom, PointF point) {

		setImageCenter(point);
		setZoom(zoom);
	}

	// ORIENTAMENTO

	public void setBearing(float bearing) {

		try {
			if (!isMoving)	
				this.bearing = (float) (-bearing + selected_floor.bearing);
		} catch (NullPointerException e) {this.bearing = 0;}
	}

	public float getBearing() {
		return bearing;
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	// DISEGNO SULL'IMMAGINE


	private void prepareImage(Canvas canvas) {

		// scalo l'immagine 
		image = new Matrix();
		image.setScale(zoom, zoom);

		Paint drawPaint = new Paint();
		drawPaint.setAntiAlias(true);
		drawPaint.setFilterBitmap(true);

		float centerScaledWidth = image_center.x * zoom;
		float centerScaledHeigth = image_center.y * zoom;

		// faccio la translazione dell'immagine per centrarla
		image.postTranslate(
				screen_center.x -  centerScaledWidth, 
				screen_center.y - centerScaledHeigth);

		canvas.drawBitmap(bmp, image, drawPaint);

		canvas.save();
		canvas.restore();
	}


	// disegno il marker bordato di bianco
	private void drawMarkers(Canvas canvas) {
		Paint drawPaint = new Paint();
		drawPaint.setAntiAlias(true);

		drawPaint.setColor(Color.WHITE);
		canvas.drawCircle(screen_center.x, screen_center.y, MARKER_DIAMETER + 1, drawPaint);

		drawPaint.setColor(Color.RED);
		canvas.drawCircle(screen_center.x, screen_center.y, MARKER_DIAMETER, drawPaint);

		canvas.save();
		canvas.restore();
	}



	////////////////////////////////////////////////////////////////////////////////////////////////








	// DRAW!!!!!
	@Override
	public void draw(Canvas canvas) {


		// imposto il centro dello schermo, funzione chiamata una sola volta
		setScreenCenter();

		if (selected_floor != null) {

			// se si sta effettuando qualche operazione di touch, blocco la rotazione
			canvas.rotate(bearing, screen_center.x, screen_center.y);


			prepareImage(canvas);
			drawMarkers(canvas);

		}

	}

	//////////////////////////////////////////////////////////////////////////////




	//////////////////////////////////////////////////////////////////////////////
	// MISURAZIONE DELLO SCHERMO

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		//	int d = Math.min(measuredWidth, measuredHeight);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

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
