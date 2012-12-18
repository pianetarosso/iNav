package it.inav.base_objects;

import java.util.ArrayList;
import java.util.List;

public class Point {

	public long id;
	public String RFID;
	public Pixel posizione;
	public int piano;
	public boolean via_accesso;
	public Room stanza;

	
	public List<Sons> figli = new ArrayList<Sons>();
	
	
	public Point(long id, String RFID, int x, int y, int piano,
			boolean via_accesso) {
		
		this.id = id;
		this.RFID = RFID;
		this.posizione = new Pixel(x,y);
		this.piano = piano;
		this.via_accesso = via_accesso;
	}
	
	public void addSon(Path path, Point p) {
		Sons s = new Sons(path, p);
		if (!figli.contains(s))
			figli.add(s);
	}

	public void setRoom(Room room) {
		this.stanza = room;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public boolean testRFID(String RFID) {
		return this.RFID.contentEquals(RFID);
	}
	
	public List<Path> buildPathList(List<Long> justused) {
		
		List<Path> pt = new ArrayList<Path>();
		
		for(int i=0; i< figli.size(); i++) {
			Path temp = figli.get(i).returnPath(id);
			if (!justused.contains(temp.id_point_B))
				pt.add(temp);
		}
		return pt;
	}
}
