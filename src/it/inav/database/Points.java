package it.inav.database;

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
	
	// Tabella per la gestione dei punti
	protected static final String pointTable = "Punti";							// nome della tabella
	protected static final String colPointId = "Id";								// chiave, LONG autoincrementale
	private static final String colPointRFID = "RFID";							// valore RFID, STRING
	protected static final String colPointReferenceBuilding = "Id_edificio";	// chiave esterna, indica l'edificio a cui appartiene questo punto, LONG
	private static final String colPointPixelX = "X";							// posizione X sulla mappa, INT
	private static final String colPointPixelY = "Y";							// posizione Y sulla mappa, INT
	private static final String colPointFloor = "Piano";						// piano dove si trova il punto, INT
	private static final String colPointInOut = "Via_di_accesso";				// se il punto è un ingresso o un uscita, INT
	
	
	private static final String[] allParameters = new String[] {colPointId,
		colPointRFID, colPointPixelX, colPointPixelY, 
		colPointFloor, colPointInOut};
	
	// Stringa creazione Tabella
	protected static final String Points =
			"CREATE TABLE " +pointTable+" ( " 
					+colPointId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+colPointRFID+ " TEXT, "
					+colPointPixelX+ " INT, " 
					+colPointPixelY+ " INT, "
					+colPointFloor+ " INT, "
					+colPointInOut+ " INT, "
					+colPointReferenceBuilding+ " INTEGER NOT NULL, "
					+"FOREIGN KEY ( "+colPointReferenceBuilding+" ) "
					+"REFERENCES " +Buildings.buildingTable+ " ( " +Buildings.colBuildingId+ " )"
					+");";
		
		
	protected Points(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA PUNTO
	protected long createPoint(
			long edificio,
			String RFID,
			Pixel p, 
			int piano,
			boolean ingresso_uscita
			) { 

		Log.i(pointTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colPointRFID, RFID);
		initialValues.put(colPointPixelX, p.x);
		initialValues.put(colPointPixelY, p.y);
		initialValues.put(colPointFloor, piano);
		initialValues.put(colPointInOut, convertBoolean(ingresso_uscita));
		initialValues.put(colPointReferenceBuilding, edificio);
		return mDb.insert(pointTable, null, initialValues);
	}
		
	// CANCELLA UN PUNTO
	protected boolean deletePoint(long id) {
		return mDb.delete(pointTable, colPointId + "=" + id, null) > 0;
	}
	
	// CANCELLO TUTTI I PUNTI DI UN EDIFICIO
	protected boolean deleteAllPoints(long id) {
		return mDb.delete(pointTable, colPointReferenceBuilding + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI I PUNTI DI UN EDIFICIO (ordinati per piano)
	protected List<Point> fetchPoints(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, pointTable, allParameters, 
				colPointReferenceBuilding + "=" + id, null,	null, null, colPointFloor+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTopointList(mCursor);
	}
		
		
		
	
	// METODI PER RESTITUIRE UNA LISTA (O PUNTI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Point> cursorTopointList(Cursor cursor) {
		
		List <Point> output = new ArrayList<Point>();
		
		if (cursor!=null) {
			do {
				output.add(cursorToPoint(cursor));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	private Point cursorToPoint(Cursor cursor) {
		return new Point(
				cursor.getLong(cursor.getColumnIndex(colPointId)),
				cursor.getString(cursor.getColumnIndex(colPointRFID)),
				cursor.getInt(cursor.getColumnIndex(colPointPixelX)),
				cursor.getInt(cursor.getColumnIndex(colPointPixelY)),
				cursor.getInt(cursor.getColumnIndex(colPointFloor)),
				convertBoolean(cursor.getInt(cursor.getColumnIndex(colPointInOut)))
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
