package it.inav.database;

import it.inav.base_objects.Path;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Paths {

private SQLiteDatabase mDb;
	
	// Tabella per la gestione dei percorsi
	protected static final String pathTable = "Percorsi";							// nome della tabella
	private static final String colPathId = "Id";								// chiave, LONG autoincrementale
	private static final String colPathCost = "Costo";							// costo del persorso, INT
	private static final String colPathElevator = "Ascensore";					// ascensore, INT
	private static final String colPathStair = "Scala";							// Scala, INT
	protected static final String colPathReferenceBuilding = "Id_edificio";		// chiave esterna, indica l'edificio a cui appartiene questo percorso, LONG
	protected static final String colPathReferencePointA = "Id_punto_A";		// chiave esterna, indica il punto A, LONG
	protected static final String colPathReferencePointB = "Id_punto_B";		// chiave esterna, indica il punto B, LONG
	
	
	private static final String[] allParameters = new String[] {
		colPathCost, colPathElevator, colPathStair, colPathReferencePointA, colPathReferencePointB};
	
	// Stringa creazione Tabella
	protected static final String Paths =
			"CREATE TABLE " +pathTable+" ( " 
					+colPathId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+colPathCost+ " INT, "
					+colPathElevator+ " INT, " 
					+colPathStair+ " INT, "
					+colPathReferencePointA+ " INTEGER NOT NULL, "
					+colPathReferencePointB+ " INTEGER NOT NULL, "
					+colPathReferenceBuilding+ " INTEGER NOT NULL, "
					
					+"FOREIGN KEY ( "+colPathReferencePointA+" ) "
					+"REFERENCES " +Points.pointTable+ " ( " +Points.colPointId+ " ), "
					
					+"FOREIGN KEY ( "+colPathReferencePointB+" ) "
					+"REFERENCES " +Points.pointTable+ " ( " +Points.colPointId+ " ), "
					
					+"FOREIGN KEY ( "+colPathReferenceBuilding+" ) "
					+"REFERENCES " +Buildings.buildingTable+ " ( " +Buildings.colBuildingId+ " )"
					+");";
		
		
	protected Paths(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA UN PERCORSO
	protected long createPath(
			long edificio,
			int costo,
			boolean ascensore,
			boolean scale,
			long A,
			long B
			) { 

		Log.i(pathTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colPathCost, costo);
		initialValues.put(colPathElevator, convertBoolean(ascensore));
		initialValues.put(colPathStair, convertBoolean(scale));
		initialValues.put(colPathReferencePointA, A);
		initialValues.put(colPathReferencePointB, B);
		initialValues.put(colPathReferenceBuilding, edificio);
		return mDb.insert(pathTable, null, initialValues);
	}
		
	// CANCELLA UN PERCORSO
	protected boolean deletePath(long id) {
		return mDb.delete(pathTable, colPathId + "=" + id, null) > 0;
	}
	
	// CANCELLO TUTTI I PERCORSI DI UN EDIFICIO
	protected boolean deleteAllPaths(long id) {
		return mDb.delete(pathTable, colPathReferenceBuilding + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI I PERCORSI DI UN EDIFICIO (ordinati per piano)
	protected List<Path> fetchAllPaths(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, pathTable, allParameters, 
				colPathReferenceBuilding + "=" + id, null,	null, null, colPathReferencePointA+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTopathList(mCursor);
	}
		
		
		
	
	// METODI PER RESTITUIRE UNA LISTA (O PIANI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Path> cursorTopathList(Cursor cursor) {
		
		List <Path> output = new ArrayList<Path>();
		
		if (cursor!=null) {
			do {
				output.add(cursorTopath(cursor));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	private Path cursorTopath(Cursor cursor) {
		return new Path(
				cursor.getInt(cursor.getColumnIndex(colPathCost)),
				convertBoolean(cursor.getInt(cursor.getColumnIndex(colPathElevator))),
				convertBoolean(cursor.getInt(cursor.getColumnIndex(colPathStair))),
				cursor.getLong(cursor.getColumnIndex(colPathReferencePointA)),
				cursor.getLong(cursor.getColumnIndex(colPathReferencePointB))
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
	//////////////////////////////////////////////////////////////////////////////////////////////////////
}

