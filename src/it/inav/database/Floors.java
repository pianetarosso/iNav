package it.inav.database;

import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;
import it.inav.base_objects.Point;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Floors {

	private SQLiteDatabase mDb;

	private static final String[] allParameters = new String[] {Floor.ID, 
		Floor.IMMAGINE, Floor.BEARING, Floor.NUMERO_DI_PIANO, Floor.DESCRIZIONE};

	// Stringa creazione Tabella
	protected static final String Floors =
			"CREATE TABLE " 
					+ Building.FLOORS_TAG +" ( " 
					+ Floor.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Floor.IMMAGINE + " TEXT, "
					+ Floor.BEARING + " TEXT, " // trovare valore per numero con virgola
					+ Floor.NUMERO_DI_PIANO + " INT, "
					+ Floor.DESCRIZIONE + " TEXT, "
					+ Building.BUILDING_TAG + " INTEGER NOT NULL, "
					+ "FOREIGN KEY ( "+ Building.BUILDING_TAG +" ) "
					+ "REFERENCES " + Building.BUILDING_TAG + " ( " + Building.ID + " )"
					+ ");";


	protected Floors(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}


	// CREA Piano
	protected long createFloor(
			long edificio,
			String link,
			double bearing,
			int numero_di_piano, 
			String descrizione
			) { 

		Log.i(Building.FLOORS_TAG, "Inserting record...");

		ContentValues initialValues = new ContentValues();
		initialValues.put(Floor.IMMAGINE, link);
		initialValues.put(Floor.BEARING, "" + bearing);
		initialValues.put(Floor.NUMERO_DI_PIANO, numero_di_piano);
		initialValues.put(Floor.DESCRIZIONE, descrizione);
		initialValues.put(Building.BUILDING_TAG, edificio);

		return mDb.insert(Building.FLOORS_TAG, null, initialValues);
	}

	// CANCELLO TUTTI I PIANI DI UN EDIFICIO
	protected boolean deleteAllFloors(long id) {
		return mDb.delete(Building.FLOORS_TAG, Building.BUILDING_TAG + "=" + id, null) > 0;
	}

	// RECUPERO TUTTI I PIANI DI UN EDIFICIO (ordinati per piano)
	protected List<Floor> fetchFloors(long Id, List<Point> points) throws SQLException {
		Cursor mCursor = mDb.query(true, Building.FLOORS_TAG, allParameters, 
				Building.BUILDING_TAG + "=" + Id, null,	null, null, Floor.NUMERO_DI_PIANO+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTofloorList(mCursor, points);
	}


	// METODI PER RESTITUIRE UNA LISTA (O PIANI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Floor> cursorTofloorList(Cursor cursor, List<Point> points) {

		List <Floor> output = new ArrayList<Floor>();

		if (cursor!=null) {
			do {
				output.add(cursorToFloor(cursor, points));
			} while (cursor.moveToNext());
		}
		return output;
	}

	private Floor cursorToFloor(Cursor cursor, List<Point> points) {
		try {
			return new Floor(
					cursor.getLong(cursor.getColumnIndex(Floor.ID)),
					cursor.getString(cursor.getColumnIndex(Floor.IMMAGINE)),
					Double.parseDouble(cursor.getString(cursor.getColumnIndex(Floor.BEARING))),
					cursor.getInt(cursor.getColumnIndex(Floor.NUMERO_DI_PIANO)),
					cursor.getString(cursor.getColumnIndex(Floor.DESCRIZIONE)),
					points
					);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////	

}

