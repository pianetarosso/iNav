package it.inav.base_objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Point {

	// COSTANTI PER PARSING ///////////////
	public static final String ID = "id";
	public static final String PIANO = "piano";
	public static final String RFID_ = "RFID";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String INGRESSO = "ingresso";
	//////////////////////////////////////


	// VARIABILI ///////////////
	public long id;
	public String RFID;
	public Pixel posizione;
	public int piano;
	public boolean ingresso;
	////////////////////////////


	// OGGETTI ////////////////
	public Room stanza;
	public List<Path> paths;
	///////////////////////////


	// COSTRUTTORE
	public Point(long id, String RFID, int x, int y, int piano, boolean ingresso) {

		this.id = id;
		this.RFID = RFID;
		this.posizione = new Pixel(x,y);
		this.piano = piano;
		this.ingresso = ingresso;

		this.paths = new ArrayList<Path>();
	}



	// PARSING DEL JSON
	public static Point parse(JSONObject p) throws JSONException {

		long id = p.getLong(ID);
		int piano = p.getInt(PIANO);
		String RFID = p.getString(RFID_);
		int x = p.getInt(X);
		int y = p.getInt(Y);
		boolean ingresso = p.getBoolean(INGRESSO);

		return new Point(id, RFID, x, y, piano, ingresso);
	}


	// AGGIUNGO RIFERIMENTI ALLE PATH DEL PUNTO
	public void addPath(Path p) {
		paths.add(p);
	}

	// IMPOSTO LA STANZA
	public void setRoom(Room room) {
		this.stanza = room;
	}






	// ToSTRING
	public String toString() {

		String out = "Point:\n";

		out += "id:" + id;
		out += ", piano:" + piano;
		out += ", RFID:" + RFID;
		out += ", posizione:" + posizione.toString();
		out += ", ingresso:" + ingresso;

		out += "\n";

		return out;
	}
}
