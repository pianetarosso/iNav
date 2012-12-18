package it.inav.graphics;

import it.inav.R;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MapView extends View {
	
	private float bearing;
	private Bitmap bmp;
	private InputStream is;
	private Picture p;
	Drawable bd;
	Matrix matrix;

	public MapView(Context context) {
		super(context);
		init(context);
	}
	
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public MapView(Context context, AttributeSet ats, int defaultStyle) {
		super(context, ats, defaultStyle);
		init(context);
	}

	private Paint markerPaint;
	private Paint circlePaint;
	
		
	protected void init(Context context) {
		setFocusable(true);
		
		 is = context.getResources().openRawResource(R.raw.mappa);
		// p =  Picture.createFromStream(is);
		
		bmp = BitmapFactory.decodeStream(is);
		bmp = Bitmap.createScaledBitmap(bmp, 200, 200, false);
        
		// RotateDrawable bd = RotateDrawable.createFromStream(is, "test");//BitmapDrawable.createFromStream(is, "mappa");
		//RotateAnimation ra = new RotateAnimation();
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(Color.GREEN);
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);		
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(Color.RED);
		matrix = new Matrix();

	}

	@Override
	public void draw(Canvas canvas) {
		int px = getMeasuredWidth() / 2;
		int py = getMeasuredHeight() /2 ;
		int radius = Math.min(px, py);
		// Draw the background
		canvas.drawCircle(px, py, radius, circlePaint);
		
		
		
		
		//Matrix mnew = matrix;
		//matrix.postTranslate(dx, dy);
		//Log.i("effective_rotation", ""+bearing);
		//Log.i("effective_rotation?", ""+this.bearing);
		matrix.postRotate( bearing, bmp.getWidth(), bmp.getHeight() );
		//Bitmap rotatedBitmap = Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true );
		//canvas.setBitmap(rotatedBitmap);
	    
		//p.draw(canvas);
		//canvas.drawPicture(p);
		
		
		canvas.drawBitmap(bmp, matrix, null);
	//	rotatedBitmap.recycle();
		// Rotate our perspective so that the ‘top’ is
		// facing the current bearing.
		canvas.save();
		canvas.rotate(-bearing, px, py);

		canvas.drawLine(px, py-20, px, py+20, markerPaint);
		canvas.save();
		
		// Draw the marker every 15 degrees and text every 45.
		for (int i = 0; i < 24; i++) {
			// Draw a marker.
			canvas.drawLine(px, py-20, px, py-20+10, markerPaint);
			canvas.save();
		
			canvas.restore();
			canvas.rotate(15, px, py);
		}
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
		if (_bearing < 0) 
		    bearing = _bearing + 360;
		else
			bearing = _bearing;
		Log.i("bearing", ""+bearing);
	}
	
	public float getBearing() {
		return bearing;
	}

	
}
