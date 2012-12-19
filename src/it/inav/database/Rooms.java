package it.inav.database;

import it.inav.base_objects.Building;
import it.inav.base_objects.Point;
import it.inav.base_objects.Room;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Rooms {

	private SQLiteDatabase mDb;

	private static final String[] allParameters = new String[] {Room.ID,
		Room.LINK, Room.PUNTO, Room.NOME_STANZA, Room.PERSONE, Room.ALTRO};

	// Stringa creazione Tabella
	protected static final String Rooms =
			"CREATE TABLE " 
					+ Building.ROOMS_TAG + " ( " 
					+ Room.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Room.LINK + " TEXT, "
					+ Room.NOME_STANZA + " TEXT, "
					+ Room.PERSONE + " TEXT, "
					+ Room.ALTRO + " TEXT, "
					
					+ Building.BUILDING_TAG + " INTEGER NOT NULL, "
					+ Room.PUNTO + " INTEGER NOT NULL, "
					
					+ "FOREIGN KEY ( " + Building.BUILDING_TAG + " ) "
					+ "REFERENCES " + Building.BUILDING_TAG + " ( " + Building.ID + " ), "
					
					+ "FOREIGN KEY ( " + Room.PUNTO + " ) "
					+ "REFERENCES " + Building.POINTS_TAG + " ( " + Point.ID + " )"
					+ ");";


	protected Rooms(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}


	// CREA Stanza
	protected long createRooms(
			long edificio,
			long punto,
			String link,
			String nome_stanza,
			String personale,
			String altro
			) { 

		Log.i(Building.ROOMS_TAG, "Inserting record...");

		ContentValues initialValues = new ContentValues();
		initialValues.put(Room.LINK, link);
		initialValues.put(Room.NOME_STANZA, nome_stanza);
		initialValues.put(Room.PERSONE, personale);
		initialValues.put(Room.ALTRO, altro);
		initialValues.put(Building.BUILDING_TAG, edificio);
		initialValues.put(Room.PUNTO, punto);

		return mDb.insert(Building.ROOMS_TAG, null, initialValues);
	}

	// CANCELLO TUTTI LE STANZE DI UN EDIFICIO
	protected boolean deleteAllRooms(long id) {
		return mDb.delete(Building.ROOMS_TAG, Building.BUILDING_TAG + "=" + id, null) > 0;
	}

	// RECUPERO TUTTI LE STANZE DI UN EDIFICIO (ordinate per piano)
	protected List<Room> fetchRooms(long id, List<Point> points) throws SQLException, MalformedURLException {
		Cursor mCursor = mDb.query(true, Building.ROOMS_TAG, allParameters, 
				Building.BUILDING_TAG + "=" + id, null,	null, null, Room.NOME_STANZA + " ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorToRoomList(mCursor, points);
	}


	// METODI PER RESTITUIRE UNA LISTA (O PUNTI SINGOLI) DAI CURSORI /////////////////////////////////////////
	private List<Room> cursorToRoomList(Cursor cursor, List<Point> points) throws MalformedURLException {

		List <Room> output = new ArrayList<Room>();

		if (cursor!=null) {
			do {
				output.add(cursorToRoom(cursor, points));
			} while (cursor.moveToNext());
		}
		return output;
	}

	private Room cursorToRoom(Cursor cursor, List<Point> points) throws MalformedURLException {

		long id = cursor.getLong(cursor.getColumnIndex(Room.PUNTO));
		Point point = null;
		for(Point p : points)
			if (p.id == id) {
				point = p;
				break;
			}

		return new Room(
				cursor.getString(cursor.getColumnIndex(Room.NOME_STANZA)),
				cursor.getString(cursor.getColumnIndex(Room.PERSONE)),
				cursor.getString(cursor.getColumnIndex(Room.ALTRO)),
				cursor.getString(cursor.getColumnIndex(Room.LINK)),
				point
				);
	}


}
