package it.inav.database;

import it.inav.base_objects.Building;
import it.inav.base_objects.Path;
import it.inav.base_objects.Point;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Paths {

private SQLiteDatabase mDb;
	
	
	
	private static final String[] allParameters = new String[] {
		Path.COSTO, Path.ASCENSORE, Path.SCALA, Path.A, Path.B};
	
	// Stringa creazione Tabella
	protected static final String Paths =
			"CREATE TABLE " 
					+ Building.PATHS_TAG + " ( " 
					+ Path.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Path.COSTO + " INT, "
					+ Path.ASCENSORE + " TEXT, " 
					+ Path.SCALA + " TEXT, "
					
					+ Path.A + " INTEGER NOT NULL, "
					+ Path.B + " INTEGER NOT NULL, "
					+ Building.BUILDING_TAG + " INTEGER NOT NULL, "
					
					+ "FOREIGN KEY ( " + Path.A + " ) "
					+ "REFERENCES " + Building.POINTS_TAG + " ( " + Point.ID + " ), "
					
					+ "FOREIGN KEY ( " + Path.B + " ) "
					+ "REFERENCES " + Building.POINTS_TAG + " ( " + Point.ID + " ), "
					
					+ "FOREIGN KEY ( " + Building.BUILDING_TAG + " ) "
					+ "REFERENCES " + Building.BUILDING_TAG + " ( " + Building.ID + " )"
					
					+ ");";
		
		
	protected Paths(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA UN PERCORSO
	protected long createPath(
			long edificio,
			int costo,
			String ascensore,
			String scale,
			long A,
			long B
			) { 

		Log.i(Building.PATHS_TAG, "Inserting record...");
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(Path.COSTO, costo);
		initialValues.put(Path.ASCENSORE, ascensore);
		initialValues.put(Path.SCALA, scale);
		initialValues.put(Path.A, A);
		initialValues.put(Path.B, B);
		initialValues.put(Building.BUILDING_TAG, edificio);
		return mDb.insert(Building.PATHS_TAG, null, initialValues);
	}
	
	// CANCELLO TUTTI I PERCORSI DI UN EDIFICIO
	protected boolean deleteAllPaths(long id) {
		return mDb.delete(Building.PATHS_TAG, Building.BUILDING_TAG + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI I PERCORSI DI UN EDIFICIO (ordinati per piano)
	protected List<Path> fetchAllPaths(long id, List<Point> points) throws SQLException {
		Cursor mCursor = mDb.query(true, Building.PATHS_TAG, allParameters, 
				Building.BUILDING_TAG + "=" + id, null,	null, null, Path.A+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTopathList(mCursor, points);
	}
		
		
		
	
	// METODI PER RESTITUIRE UNA LISTA (O PIANI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Path> cursorTopathList(Cursor cursor, List<Point> points) {
		
		List <Path> output = new ArrayList<Path>();
		
		if (cursor!=null) {
			do {
				output.add(cursorTopath(cursor, points));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	private Path cursorTopath(Cursor cursor, List<Point> points) {
		return new Path(
				cursor.getLong(cursor.getColumnIndex(Path.A)),
				cursor.getLong(cursor.getColumnIndex(Path.B)),
				cursor.getString(cursor.getColumnIndex(Path.ASCENSORE)),
				cursor.getString(cursor.getColumnIndex(Path.SCALA)),
				cursor.getInt(cursor.getColumnIndex(Path.COSTO)),
				points
				);
	}
}

