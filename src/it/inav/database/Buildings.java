package it.inav.database;


import it.inav.base_objects.Building;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class Buildings {

	private SQLiteDatabase mDb;
	
	// Tabella per la gestione degli edifici
	protected static final String buildingTable = "Edifici";						// nome della tabella
	protected static final String colBuildingId = "Id";								// chiave, scaricata dal sito
	private static final String colBuildingName = "Nome";							// nome dell'edificio
	private static final String colBuildingLatitude = "Latitudine";					// latitudine, INT
	private static final String colBuildingLongitude = "Longitudine";				// longitudine, INT
	private static final String colBuildingNumberOfFloors = "Numero_di_piani";		// numero di piani
	private static final String colBuildingVersion = "Versione";					// versione della "mappa"
	
	private static final String[] allParameters = new String[] {colBuildingId,
		colBuildingName, colBuildingLatitude, colBuildingLongitude, 
		colBuildingNumberOfFloors, colBuildingVersion};
	
	// Stringa creazione Tabella
	protected static final String Buildings =
			"CREATE TABLE " +buildingTable+" ( " 
					+colBuildingId+ " LONG PRIMARY KEY, "
					+colBuildingName+ " TEXT"
					+colBuildingLatitude+ " INT, " 
					+colBuildingLongitude+ " INT, "
					+colBuildingNumberOfFloors+ " INTEGER NOT NULL, "
					+colBuildingVersion+ " INTEGER NOT NULL"
					+");";
		
		
	protected Buildings(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA EDIFICIO
	protected long createBuilding(
			long id,
			String nome,
			int latitudine, 
			int longitudine, 
			int piani,
			int versione
			) {
		
		Log.i(buildingTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colBuildingId, id);
		initialValues.put(colBuildingName, nome);
		initialValues.put(colBuildingLatitude, latitudine);
		initialValues.put(colBuildingLongitude, longitudine);
		initialValues.put(colBuildingNumberOfFloors, piani);
		initialValues.put(colBuildingVersion, versione);
		return mDb.insert(buildingTable, null, initialValues);
	}
	
	// CANCELLA EDIFICIO
	protected boolean deleteBuilding(long id) {
		return mDb.delete(buildingTable, colBuildingId + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI GLI EDIFICI
	protected List<Building> fetchBuilding() throws SQLException {
		Cursor mCursor = mDb.query(true, buildingTable, allParameters, null, null,
					null, null, colBuildingName+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTobuildingList(mCursor);
	}
	
	// RECUPERO UN EDIFICIO TRAMITE L'ID
	protected Building fetchBuilding(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, buildingTable, allParameters, id + "=" + colBuildingId, null,
					null, null, colBuildingName+" ASC", null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		return cursorTobuilding(mCursor);
	}
	
	// RECUPERO UN/ALCUNI EDIFICI TRAMITE IL NOME
	protected List<Building> fetchBuilding(String nome) throws SQLException {
		Cursor mCursor = mDb.query(true, buildingTable, allParameters, nome + "=" + colBuildingName, null,
				null, null, colBuildingName+" ASC", null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		return cursorTobuildingList(mCursor);
	}
	
	// RECUPERO GLI EDIFICI IN UNA DATA AREA (DISTANZA IN KM DA UN PUNTO DATO)
	protected List<Building> fetchBuilding(int latitude, int longitude, int distance) throws SQLException {
		
		List<Building> buildings = fetchBuilding();
		
		Location location = new Location(buildingTable);
		location.setLatitude(latitude/1E6);
		location.setLongitude(longitude/1E6);
	
		/*for (int i=0; i < buildings.size(); i++) {
			if (Math.abs(location.distanceTo(buildings.get(i).location)) > distance * 1000) 
				buildings.remove(i);
				i--;
		}
		*/
		return buildings;
	}
	
	// ESEGUO L'UPDATE DI UN EDIFICIO
	protected boolean updateBuilding(long id, String nome, int lat, int lng, int piani, int versione) {
		ContentValues args = new ContentValues();
		args.put(colBuildingName, nome);
		args.put(colBuildingLatitude, lat);
		args.put(colBuildingLongitude, lng);
		args.put(colBuildingNumberOfFloors, piani);
		args.put(colBuildingVersion, versione);
        return mDb.update(buildingTable, args, id + "=" + colBuildingId, null) > 0;  	
	}
		
	
	
	// METODI ACCESSORI/////////////////////////////////////////////////////////////////////////////
	
	
	// CREAZIONE DI UNA LISTA DI BUILDINGS
	private List<Building> cursorTobuildingList(Cursor cursor) {
		
		List <Building> output = new ArrayList<Building>();
		
		if (cursor!=null) {
			do {
				output.add(cursorTobuilding(cursor));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	// CREAZIONE DI UN SOLO BUILDING
	private Building cursorTobuilding(Cursor cursor) {
/*		return new Building(
				cursor.getLong(cursor.getColumnIndex(colBuildingId)),
				cursor.getString(cursor.getColumnIndex(colBuildingName)),
				cursor.getInt(cursor.getColumnIndex(colBuildingLatitude)),
				cursor.getInt(cursor.getColumnIndex(colBuildingLongitude)),
				cursor.getInt(cursor.getColumnIndex(colBuildingNumberOfFloors)),
				cursor.getInt(cursor.getColumnIndex(colBuildingVersion))
				);*/ return null;
	}
	
}
