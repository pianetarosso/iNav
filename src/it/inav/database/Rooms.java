package it.inav.database;

import it.inav.base_objects.Room;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Rooms {

	private SQLiteDatabase mDb;
	private static final String separator = "--";
	
	// Tabella per la gestione delle stanze
	protected static final String roomTable = "Stanze";							// nome della tabella
	protected static final String colRoomId = "Id";								// chiave, LONG autoincrementale
	protected static final String colRoomReferenceBuilding = "Id_edificio";		// chiave esterna, indica l'edificio a cui appartiene questa stanza, LONG
	protected static final String colRoomReferencePoint = "Id_punto";			// chiave esterna, indica il punto a cui appartiene questa stanza, LONG
	private static final String colRoomFloor = "Piano";							// piano dove si trova la stanza, INT
	private static final String colRoomLink = "Link";							// se la stanza ha qualche contenuto in rete, STRING
	private static final String colRoomRoomName = "Nome_stanza";				// se la stanza si trova in una stanza con qualche "nome" (es. "Ufficio 42"), STRING
	private static final String colRoomPeople = "Persone";						// se la stanza è associato ad una o più persone (separate da "-"), STRING
	private static final String colRoomOther = "Altro";							// altri possibili dati sulla stanza, String
	
	private static final String[] allParameters = new String[] {colRoomId,
		colRoomFloor, colRoomLink, colRoomReferencePoint,
		colRoomRoomName, colRoomPeople, colRoomOther};
	
	// Stringa creazione Tabella
	protected static final String Rooms =
			"CREATE TABLE " +roomTable+" ( " 
					+colRoomId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+colRoomFloor+ " INT, "
					+colRoomLink+ " TEXT, "
					+colRoomRoomName+ " TEXT, "
					+colRoomPeople+ " TEXT, "
					+colRoomOther+ " TEXT, "
					+colRoomReferenceBuilding+ " INTEGER NOT NULL, "
					+colRoomReferencePoint+ " INTEGER NOT NULL, "
					+"FOREIGN KEY ( "+colRoomReferenceBuilding+" ) "
					+"REFERENCES " +Buildings.buildingTable+ " ( " +Buildings.colBuildingId+ " )"
					+");";
		
		
	protected Rooms(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA Stanza
	protected long createRooms(
			long edificio,
			long punto,
			int piano,
			String link,
			String nome_stanza,
			String personale,
			String altro
			) { 

		Log.i(roomTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colRoomFloor, piano);
		initialValues.put(colRoomLink, link);
		initialValues.put(colRoomRoomName, nome_stanza);
		initialValues.put(colRoomPeople, personale);
		initialValues.put(colRoomOther, altro);
		initialValues.put(colRoomReferenceBuilding, edificio);
		initialValues.put(colRoomReferencePoint, punto);
		return mDb.insert(roomTable, null, initialValues);
	}
	
		
	// CANCELLA UNA STANZA
	protected boolean deleteRooms(long id) {
		return mDb.delete(roomTable, colRoomId + "=" + id, null) > 0;
	}
	
	// CANCELLO TUTTI LE STANZE DI UN EDIFICIO
	protected boolean deleteAllRooms(long id) {
		return mDb.delete(roomTable, colRoomReferenceBuilding + "=" + id, null) > 0;
	}
	
	// RECUPERO TUTTI LE STANZE DI UN EDIFICIO (ordinate per piano)
	protected List<Room> fetchRooms(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, roomTable, allParameters, 
				colRoomReferenceBuilding + "=" + id, null,	null, null, colRoomFloor+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorToRoomList(mCursor);
	}
		
	
	// METODI PER RESTITUIRE UNA LISTA (O PUNTI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Room> cursorToRoomList(Cursor cursor) {
		
		List <Room> output = new ArrayList<Room>();
		
		if (cursor!=null) {
			do {
				output.add(cursorToRoom(cursor));
			} while (cursor.moveToNext());
		}
		return output;
	}
	
	private Room cursorToRoom(Cursor cursor) {
		return null/*new Room(
				cursor.getLong(cursor.getColumnIndex(colRoomId)),
				cursor.getLong(cursor.getColumnIndex(colRoomReferencePoint)),
				cursor.getInt(cursor.getColumnIndex(colRoomFloor)),
				cursor.getString(cursor.getColumnIndex(colRoomLink)),
				cursor.getString(cursor.getColumnIndex(colRoomRoomName)),
				convertArray(cursor.getString(cursor.getColumnIndex(colRoomPeople))),
				cursor.getString(cursor.getColumnIndex(colRoomOther))
				)*/;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	
		
	
	
	
	// METODI VARI PER RENDERE I VALORI ACCETTABILI DA DATABASE E IN USCITA //////////////////////////////
		
	public String convertArray(String[] a) {
		String o = "";
		for(int i=0; i<a.length; i++)
			o += separator+a[i];
		return o;
	}
	
	public String convertArray(List<String> a) {
		String o = "";
		for(int i=0; i<a.size(); i++)
			o += separator+a.get(i);
		return o;
	}
		
	private String[] convertArray(String s) {
		String[] o; 
		if (s.length()>0) {
			if (s.contains(separator)) 
				o = s.split(separator);
			else {
				o = new String[1];
				o[0] = s;
			}
			return o;
		}
		else
			return new String[0];
	}
	/////////////////////////////////////////////////////////////////////////////////////////////		
		
}
