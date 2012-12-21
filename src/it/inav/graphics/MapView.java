package it.inav.graphics;

import it.inav.base_objects.Floor;

import java.io.InputStream;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {
	
	private float bearing;
	
	Drawable bd;
	
	private Floor selected_floor = null;
	private Bitmap bmp;
	
	
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MapView(Context context) {
		super(context);
	}
	
	protected void init(Context context, long building_id) {
		setFocusable(true);
	}

	
	
	

	public void setFloor(Floor floor) {
		selected_floor = floor;
		if (bmp != null)
			bmp.recycle();
		bmp = Bitmap.createScaledBitmap(selected_floor.immagine, 200, 200, false);
	}
	
		
	

	@Override
	public void draw(Canvas canvas) {
		int px = getMeasuredWidth() / 2;
		int py = getMeasuredHeight() /2 ;
		
		
		if (selected_floor != null) {
			Matrix matrix = new Matrix();
			
			// i punti sono le coordinate su cui si applica la rotazione
			matrix.postRotate( (float)(bearing - selected_floor.bearing), bmp.getWidth()/2, bmp.getHeight()/2 );
			
			
			canvas.drawBitmap(bmp, matrix, null);
		
			canvas.save();
			canvas.restore();
			
			
		}
		//canvas.rotate(-bearing, px, py);

//		canvas.drawLine(px, py-20, px, py+20, markerPaint);
	//	canvas.save();
		
		// Draw the marker every 15 degrees and text every 45.
	/*	for (int i = 0; i < 24; i++) {
			// Draw a marker.
			canvas.drawLine(px, py-20, px, py-20+10, markerPaint);
			canvas.save();
		
			canvas.restore();
			canvas.rotate(15, px, py);
		}*/
		
		

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		int d = Math.min(measuredWidth, measuredHeight);
		setMeasuredDimension(d, d);
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
