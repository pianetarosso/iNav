package it.inav.graphics;

import it.inav.base_objects.Floor;
import it.inav.base_objects.Pixel;
import it.inav.base_objects.Point;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class MapView extends View {
	
	private float bearing;
	
	Drawable bd;
	
	public Floor selected_floor = null;
	private List<Floor> floors = null;
	private Bitmap bmp;
	
	private float zoom;
	private float min_zoom;
	
	private Pixel screen_center = null;
	private Pixel image_center = null;

	
	
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MapView(Context context) {
		super(context);
	}
	
	public void init(List<Floor> floors) {
		this.floors = floors;
		setFocusable(true);
	}

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
	
	private void setInitialZoom() {
		
		
		if (screen_center == null)
			return;
		
		// recupero la dimensione dell'immagine
		int image_width = bmp.getWidth();
		int image_heigth = bmp.getHeight();
		
		
		int longest_i = image_width;
		if (longest_i < image_heigth)
			longest_i = image_heigth;
		
		int longest_s = screen_center.x * 2;
		
		if (longest_s < screen_center.y * 2)
			longest_s = screen_center.y * 2;
		
		float zoom = (float)longest_s / longest_i;
			
			this.min_zoom = zoom;
			this.zoom = zoom;
			
		setImageCenter(null);
	}
	
	
	private void setZoom(float zoom) {
		
		if (zoom < min_zoom)
				this.zoom = min_zoom;
			
		else if (zoom > 1)
			this.zoom = 1;
		
		else 
			this.zoom = zoom;
			
		this.invalidate();
		
	}
	
	public void goToPoint(Point point) {
		
		 setZoom((float)(this.zoom + 0.21));
		 
		 setImageCenter(point.posizione);
		 
	}
	
	private void setImageCenter(Pixel posizione) {
		
		if (posizione != null) 
			image_center = new Pixel(posizione.x, posizione.y);
		else 
			image_center = new Pixel(bmp.getWidth(), bmp.getHeight());
	}
	
	
	private void prepareImage(Canvas canvas) {
		
		canvas.rotate((float)(bearing - selected_floor.bearing), screen_center.x, screen_center.y);
		
		
		// scalo l'immagine 
		Matrix matrix = new Matrix();
		matrix.setScale(zoom, zoom);
		
		Paint drawPaint = new Paint();
		drawPaint.setAntiAlias(true);
		drawPaint.setFilterBitmap(true);
		
		float centerScaledWidth = image_center.x * zoom / 2;
		float centerScaledHeigth = image_center.y * zoom / 2;
		
		matrix.postTranslate(screen_center.x -  centerScaledWidth, 
				screen_center.y - centerScaledHeigth);
		
		canvas.drawBitmap(bmp, matrix, drawPaint);
		
		canvas.save();
		canvas.restore();
	}

	private void drawMarkers(Canvas canvas) {
			Paint drawPaint = new Paint();
			drawPaint.setAntiAlias(true);
			drawPaint.setColor(Color.RED);
			
			canvas.drawCircle(screen_center.x, screen_center.y, 2, drawPaint);
			
			canvas.save();
			canvas.restore();
	}
	
	private void setScreenCenter() {
		
		if (screen_center != null)
			return; 
					
		// recupero la dimensione dello schermo
		int screen_height = getMeasuredHeight();
		int screen_width = getMeasuredWidth();
			
		if ((screen_height == 0) || (screen_width == 0))
				return;
						
		screen_center = new Pixel(screen_width / 2, screen_height / 2);
		setInitialZoom();
	}
	
	@Override
	public void draw(Canvas canvas) {
		
		setScreenCenter();
		
		if (selected_floor != null) {
			
			
			
			
			prepareImage(canvas);
			drawMarkers(canvas);
			
		//	canvas.rotate((float)(bearing - selected_floor.bearing),  300, 300);
			
		
		
/*
		canvas.drawLine(px, py-20, px, py+20, drawPaint);
	canvas.save();
		
		// Draw the marker every 15 degrees and text every 45.
		for (int i = 0; i < 24; i++) {
			// Draw a marker.
			canvas.drawLine(px, py-20, px, py-20+10, drawPaint);
			canvas.save();
		
			canvas.restore();
			canvas.rotate(15, px, py);
		}
		
		*/
		
		}

	}

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
		Log.i("result", result+"");
		return result;
	}

	public void setBearing(float _bearing) {
		//if (_bearing < 0) 
		//    bearing = _bearing + 360;
		//else
			bearing = _bearing;
	//	Log.i("bearing", ""+bearing);
	}
	
	public float getBearing() {
		return bearing;
	}

	
}
