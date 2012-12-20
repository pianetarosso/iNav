package it.inav.database;

import it.inav.base_objects.Building;
import it.inav.base_objects.Pixel;
import it.inav.base_objects.Point;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Points {

	
	private SQLiteDatabase mDb;
	
	
	private static final String[] allParameters = new String[] {Point.ID, 
		Point.RFID_, Point.X, Point.Y, Point.PIANO, Point.INGRESSO};
	
	// Stringa creazione Tabella
	protected static final String Points =
			"CREATE TABLE " 
					+ Building.POINTS_TAG +" ( " 
					+ Point.ID + " LONG PRIMARY KEY UNIQUE, "
					+ Point.RFID_+ " TEXT, "
					+ Point.X + " INT, " 
					+ Point.Y + " INT, "
					+ Point.PIANO + " INT, "
					+ Point.INGRESSO + " INT, "
					+ Building.BUILDING_TAG + " INTEGER NOT NULL, "
					+ "FOREIGN KEY ( " + Building.BUILDING_TAG + " ) "
					+ "REFERENCES " + Building.BUILDING_TAG + " ( " + Building.ID + " )"
					+ ");";
		
		
	protected Points(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA PUNTO
	protected long createPoint(
			long id,
			long edificio,
			String RFID,
			Pixel p, 
			int piano,
			boolean ingresso
			) { 

		Log.i(Building.POINTS_TAG, "Inserting record...");
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(Point.ID, id);
		initialValues.put(Point.RFID_, RFID);
		initialValues.put(Point.X, p.x);
		initialValues.put(Point.Y, p.y);
		initialValues.put(Point.PIANO, piano);
		initialValues.put(Point.INGRESSO, convertBoolean(ingresso));
		initialValues.put(Building.BUILDING_TAG, edificio);
		
		return mDb.insert(Building.POINTS_TAG, null, initialValues);
	}
	
	// CANCELLO TUTTI I PUNTI DI UN EDIFICIO
	protected boolean deleteAllPoints(long id) {
		return mDb.delete(Building.POINTS_TAG, Building.BUILDING_TAG + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI I PUNTI DI UN EDIFICIO (ordinati per piano)
	protected List<Point> fetchPoints(long id) throws SQLException {
		Log.i("id_b", ""+id);
		Cursor mCursor = mDb.query(false, Building.POINTS_TAG, allParameters, 
				Building.BUILDING_TAG + "=" + id, null,	null, null, Point.PIANO+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTopointList(mCursor);
	}
		
		
		
	
	// METODI PER RESTITUIRE UNA LISTA (O PUNTI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Point> cursorTopointList(Cursor cursor) {
		
		List <Point> output = new ArrayList<Point>();
		
		if (cursor!=null) {
			cursor.moveToFirst();
			do {
				output.add(cursorToPoint(cursor));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	private Point cursorToPoint(Cursor cursor) {
		Log.i("ll", ""+cursor.getColumnCount()+" "+cursor.getCount());
		for(String y :cursor.getColumnNames())
			Log.i("jjkh", y);
		return new Point(
				cursor.getLong(cursor.getColumnIndex(Point.ID)),
				cursor.getString(cursor.getColumnIndex(Point.RFID_)),
				cursor.getInt(cursor.getColumnIndex(Point.X)),
				cursor.getInt(cursor.getColumnIndex(Point.Y)),
				cursor.getInt(cursor.getColumnIndex(Point.PIANO)),
				convertBoolean(cursor.getInt(cursor.getColumnIndex(Point.INGRESSO)))
				);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////	

	
	// METODI VARI PER RENDERE I VALORI ACCETTABILI DA DATABASE E IN USCITA //////////////////////////////
	private int convertBoolean(boolean b) {
		if (b)
			return 1;
		return 0;
	}
		
	private boolean convertBoolean(int i) {
		return i == 1;
	}
		
	
	/////////////////////////////////////////////////////////////////////////////////////////////		
		
}
