package it.inav.graphics;

import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {
	
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}



	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private float bearing;
	private Bitmap bmp;
	private InputStream is;
	private Picture p;
	Drawable bd;
	
	private Building b;
	private Floor selected_floor;

	public MapView(Context context, long building_id) {
		super(context);
		init(context, building_id);
	}
	
	

	public void setFloor(int floor) {
		selected_floor = b.getPiani().get(floor);
		bmp = Bitmap.createScaledBitmap(selected_floor.immagine, 200, 200, false);
	}
	
	private Paint markerPaint;
	private Paint circlePaint;
	
		
	protected void init(Context context, long building_id) {
		setFocusable(true);
		
		try {
			b = Building.getBuilding(context, building_id);
			setFloor(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
        
		// RotateDrawable bd = RotateDrawable.createFromStream(is, "test");//BitmapDrawable.createFromStream(is, "mappa");
		//RotateAnimation ra = new RotateAnimation();
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(Color.GREEN);
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);		
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(Color.RED);
		
		*/
	}

	@Override
	public void draw(Canvas canvas) {
		int px = getMeasuredWidth() / 2;
		int py = getMeasuredHeight() /2 ;
		int radius = Math.min(px, py);
		// Draw the background
		//canvas.drawCircle(px, py, radius, circlePaint);
		
		
		Matrix matrix = new Matrix();
		
		// i punti sono le coordinate su cui si applica la rotazione
		matrix.postRotate( (float)(bearing - selected_floor.bearing), bmp.getWidth()/2, bmp.getHeight()/2 );
		
		
		canvas.drawBitmap(bmp, matrix, null);
	
		canvas.save();
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
		canvas.restore();
		

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
