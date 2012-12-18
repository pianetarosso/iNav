package it.inav.base_objects;

import java.util.ArrayList;
import java.util.List;


public class Room {

	public long id;
	public Point punto;
	public int piano;
	public String link;
	public String nome_stanza;
	public List<String> persone;
	public String altro;
	public long point_id;
	
	public Room(long id, long id_punto, int piano,
			String link, String nome_stanza, 
			String[] persone, String altro) {
		
		this.id = id;
		this.piano = piano;
		this.link = link;
		this.nome_stanza = nome_stanza;
		this.persone = createListOfPeople(persone);
		this.altro = altro;
		this.point_id = id_punto;
	}	
	
	public void setPoint(Point p) {
		this.punto = p;
	}
	
	private List<String> createListOfPeople(String[] lista) {
		
		List <String> p = new ArrayList<String>();
		
		for(int i=0; i< lista.length; i++) 
			p.add(lista[i]);
		
		return p;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
