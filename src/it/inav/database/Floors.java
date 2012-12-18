package it.inav.database;

import it.inav.base_objects.Floor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Floors {

	private SQLiteDatabase mDb;
	
	// Tabella per la gestione dei piani
	protected static final String floorTable = "Piani";							// nome della tabella
	private static final String colFloorId = "Id";								// chiave, LONG autoincrementale
	private static final String colFloorLink = "Link";							// link alla posizione fisica dell'immagine del piano, STRING
	private static final String colFloorBearing = "Orientamento";				// orientamento di ogni piano rispetto al nord, STRING
	private static final String colFloorNumber = "Numero_di_piano";				// numero del piano
	protected static final String colFloorReferenceBuilding = "Id_edificio";		// chiave esterna, indica l'edificio a cui appartiene questo piano, LONG
	
	private static final String[] allParameters = new String[] {colFloorId,
		colFloorLink, colFloorBearing, colFloorNumber};
	
	// Stringa creazione Tabella
	protected static final String Floors =
			"CREATE TABLE " +floorTable+" ( " 
					+colFloorId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+colFloorLink+ " TEXT, "
					+colFloorBearing+ " TEXT, " // trovare valore per numero con virgola
					+colFloorNumber+ " INT, "
					+colFloorReferenceBuilding+ " INTEGER NOT NULL, "
					+"FOREIGN KEY ( "+colFloorReferenceBuilding+" ) "
					+"REFERENCES " +Buildings.buildingTable+ " ( " +Buildings.colBuildingId+ " )"
					+");";
		
		
	protected Floors(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA Piano
	protected long createFloor(
			long edificio,
			String link,
			double bearing,
			int floor_number
			) { 

		Log.i(floorTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colFloorLink, link);
		initialValues.put(colFloorBearing, ""+bearing);
		initialValues.put(colFloorNumber, floor_number);
		initialValues.put(colFloorReferenceBuilding, edificio);
		return mDb.insert(floorTable, null, initialValues);
	}
		
	// CANCELLA UN PIANO
	protected boolean deletefloor(long id) {
		return mDb.delete(floorTable, colFloorId + "=" + id, null) > 0;
	}
	
	// CANCELLO TUTTI I PIANI DI UN EDIFICIO
	protected boolean deleteAllFloors(long id) {
		return mDb.delete(floorTable, colFloorReferenceBuilding + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI I PIANI DI UN EDIFICIO (ordinati per piano)
	protected List<Floor> fetchfloors(long Id) throws SQLException {
		Cursor mCursor = mDb.query(true, floorTable, allParameters, 
				colFloorReferenceBuilding + "=" + Id, null,	null, null, colFloorNumber+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTofloorList(mCursor);
	}
		
		
		
	
	// METODI PER RESTITUIRE UNA LISTA (O PIANI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Floor> cursorTofloorList(Cursor cursor) {
		
		List <Floor> output = new ArrayList<Floor>();
		
		if (cursor!=null) {
			do {
				output.add(cursorTofloor(cursor));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	private Floor cursorTofloor(Cursor cursor) {
		/*return new Floor(
				cursor.getLong(cursor.getColumnIndex(colFloorId)),
				cursor.getString(cursor.getColumnIndex(colFloorLink)),
				Double.parseDouble(cursor.getString(cursor.getColumnIndex(colFloorBearing))),
				cursor.getInt(cursor.getColumnIndex(colFloorNumber))
				);*/
		return null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////	
	
}

