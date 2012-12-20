package it.inav.database;


import it.inav.base_objects.Building;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Buildings {

	private SQLiteDatabase mDb;

	protected static final String[] allParameters = new String[] {Building.ID,
		Building.NOME, Building.POSIZIONE, Building.DESCRIZIONE, Building.DATA_C,
		Building.DATA_U, Building.LINK, Building.NUMERO_DI_PIANI, Building.VERSIONE,
		Building.FOTO, Building.GEOMETRIA};

	// Stringa creazione Tabella
	protected static final String Buildings =
			"CREATE TABLE " 
					+ Building.BUILDING_TAG + " ( " 
					+ Building.ID + " LONG PRIMARY KEY UNIQUE, "
					+ Building.NOME + " TEXT,"
					+ Building.POSIZIONE + " TEXT, " 
					+ Building.DESCRIZIONE + " TEXT, "
					+ Building.DATA_C + " LONG, "
					+ Building.DATA_U + " LONG, "
					+ Building.LINK + " TEXT, "
					+ Building.NUMERO_DI_PIANI + " INTEGER NOT NULL, "
					+ Building.VERSIONE + " INTEGER, "
					+ Building.FOTO + " TEXT, "
					+ Building.GEOMETRIA + " TEXT "
					+");";


	protected Buildings(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}


	// CREA EDIFICIO
	protected long createBuilding(Building b) {

		Log.i(Building.BUILDING_TAG, "Inserting record...");

		ContentValues initialValues = new ContentValues();

		initialValues.put(Building.ID, b.id);
		initialValues.put(Building.NOME, b.nome);
		initialValues.put(Building.POSIZIONE, "["+b.posizione.getLatitudeE6()+","+b.posizione.getLongitudeE6()+"]");
		initialValues.put(Building.DESCRIZIONE, b.descrizione);
		initialValues.put(Building.DATA_C, b.data_creazione.getTime());
		initialValues.put(Building.DATA_U, b.data_update.getTime());
		initialValues.put(Building.VERSIONE, b.versione);
		initialValues.put(Building.NUMERO_DI_PIANI, b.numero_di_piani);
		
		String slink = "";
		if (b.link != null)
			slink = b.link.toString();
		initialValues.put(Building.LINK, slink);
		
		String foto = "";
		if (b.foto_link != null)
			foto = b.foto_link.toString();
		initialValues.put(Building.FOTO, foto);

		String out = "[";
		for(GeoPoint gp : b.geometria)
			out += "["+gp.getLatitudeE6()+","+gp.getLongitudeE6()+"],";
		out = out.substring(0, out.length() - 1) + ']';
		
		initialValues.put(Building.GEOMETRIA, out);

		return mDb.insert(Building.BUILDING_TAG, null, initialValues);
	}

	// CANCELLA EDIFICIO
	protected boolean deleteBuilding(long id) {
		return mDb.delete(Building.BUILDING_TAG, Building.ID + "=" + id, null) > 0;
	}

	// RECUPERO TUTTI GLI EDIFICI
	protected List<Building> fetchBuilding() 
			throws SQLException, MalformedURLException, URISyntaxException {
		Cursor mCursor = mDb.query(true, Building.BUILDING_TAG, allParameters, null, null,
				null, null, Building.NOME+" ASC", null);
		if (mCursor != null)  
			mCursor.moveToFirst();
		return cursorTobuildingList(mCursor);
	}

	// RECUPERO UN EDIFICIO TRAMITE L'ID
	protected Building fetchBuilding(long id) 
			throws SQLException, MalformedURLException, URISyntaxException {
		Cursor mCursor = mDb.query(true, Building.BUILDING_TAG, allParameters, id + "=" + Building.ID, null,
				null, null, Building.NOME+" ASC", null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		return cursorTobuilding(mCursor);
	}

	// RECUPERO UN/ALCUNI EDIFICI TRAMITE IL NOME
	protected List<Building> fetchBuilding(String nome) 
			throws SQLException, MalformedURLException, URISyntaxException {
		Cursor mCursor = mDb.query(true, Building.BUILDING_TAG, allParameters, nome + "=" + Building.NOME, null,
				null, null, Building.NOME+" ASC", null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		return cursorTobuildingList(mCursor);
	}


	// CREAZIONE DI UNA LISTA DI BUILDINGS
	private List<Building> cursorTobuildingList(Cursor cursor) 
			throws MalformedURLException, URISyntaxException {

		List <Building> output = new ArrayList<Building>();

		if (cursor!=null) 
			do 
				output.add(cursorTobuilding(cursor));
			while (cursor.moveToNext());

		return output;
	}


	// CREAZIONE DI UN SOLO BUILDING
	private Building cursorTobuilding(Cursor cursor) throws MalformedURLException, URISyntaxException {

		return new Building(
				cursor.getLong(cursor.getColumnIndex(Building.ID)),
				cursor.getString(cursor.getColumnIndex(Building.NOME)),
				cursor.getString(cursor.getColumnIndex(Building.DESCRIZIONE)),
				cursor.getString(cursor.getColumnIndex(Building.LINK)),
				cursor.getString(cursor.getColumnIndex(Building.FOTO)),
				cursor.getInt(cursor.getColumnIndex(Building.NUMERO_DI_PIANI)),
				cursor.getInt(cursor.getColumnIndex(Building.VERSIONE)),
				cursor.getLong(cursor.getColumnIndex(Building.DATA_C)),
				cursor.getLong(cursor.getColumnIndex(Building.DATA_U)),
				cursor.getString(cursor.getColumnIndex(Building.POSIZIONE)),
				cursor.getString(cursor.getColumnIndex(Building.GEOMETRIA))
				);
	}
}
