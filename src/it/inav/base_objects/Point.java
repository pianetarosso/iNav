package it.inav.base_objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Point {

	public long id;
	public String RFID;
	public Pixel posizione;
	public int piano;
	public boolean ingresso;
	
	public Room stanza;
	public List<Path> paths;
	
	
	public Point(long id, String RFID, int x, int y, int piano, boolean ingresso) {
		
		this.id = id;
		this.RFID = RFID;
		this.posizione = new Pixel(x,y);
		this.piano = piano;
		this.ingresso = ingresso;
		
		this.paths = new ArrayList<Path>();
	}
	
	/*
	  			'id' : p.pk,
                'piano' : p.piano.pk,
                'RFID' : p.RFID,
                'x' : p.x,
                'y' : p.y,
                'ingresso' : p.ingresso
	 */
	
	private static final String ID = "id";
	private static final String PIANO = "piano";
	private static final String RFID_ = "RFID";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String INGRESSO = "ingresso";
	
	
	public static Point parse(JSONObject p) throws JSONException {
		
		long id = p.getLong(ID);
		int piano = p.getInt(PIANO);
		String RFID = p.getString(RFID_);
		int x = p.getInt(X);
		int y = p.getInt(Y);
		boolean ingresso = p.getBoolean(INGRESSO);
		
		return new Point(id, RFID, x, y, piano, ingresso);
	}
	
	public void addPath(Path p) {
		paths.add(p);
	}
	
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
	
	

	public void setRoom(Room room) {
		this.stanza = room;
	}
	
	
	
	
}
