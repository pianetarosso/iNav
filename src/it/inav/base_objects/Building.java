package it.inav.base_objects;

import java.util.List;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class Building {

	public long id;
	public String nome;
	public int latitudine, longitudine;
	public Location location;
	public int numero_di_piani;
	
	public List<Floor> piani;
	public List<Point> punti;
	public List<Room> stanze;
	public List<Path> percorsi;

	public int piano_offset; // necessario per tenere conto dei piani "negativi" o "interrati"
	public int versione;
	
	
	
	// COSTRUTTORE
	public Building(long id, String nome, int latitudine, int longitudine,
			int numero_di_piani, int versione) {
		
		this.id = id;
		this.nome = nome;
		this.latitudine = latitudine;
		this.longitudine = longitudine;
		this.numero_di_piani = numero_di_piani;
		this.versione = versione;
		
		setLocation();
	}

	public void setPoints(List<Point> punti) {
		this.punti = punti;
	}

	public void setRooms(List<Room> stanze) {
		this.stanze = stanze;
	}

	public void setPaths(List<Path> percorsi) {
		this.percorsi = percorsi;
	}
	
	// IMPOSTO LA LISTA DEI PIANI
	public void setFloors(List<Floor> f) {
		this.piani = f;
		piano_offset = 0;
		for(int i=0; i < numero_di_piani; i++) {
			int n = piani.get(i).numero_di_piano;
			if(n < piano_offset)
				piano_offset = n;
		}
		piano_offset = Math.abs(piano_offset);
	}
	
	// METODO PER LA CREAZIONE DEI RIFERIMENTI TRA I VARI OGGETTI, DA CHIAMARE SOLO A 
	// CARICAMENTO ULTIMATO
	// DA CHIAMARE SOLO SU THREAD SEPARATO!!!
	public void buildReferences() {
		
		// creo per ogni punto la lista di figli, basandomi sulla lista di PATH
		punti = BuildAndFind.buildSons(punti, percorsi);
		
		// creo per ogni piano una lista dei suoi punti
		for(int i=0; i < punti.size(); i++) {
			Point p = punti.get(i);
			piani.get(piano_offset + p.piano).punti_del_piano.add(p);
		}
		
		// faccio il binding tra i punti e le stanze corrispondenti
		for(int i=0; i < stanze.size(); i++) {
			Room r1 = stanze.get(i);
			List<Point> p = piani.get(r1.piano + piano_offset).punti_del_piano;
			
			// scansiono i punti del piano per trovare quello corrispondente alla stanza
			for(int v=0; v < p.size(); v++) {
				if (r1.point_id == p.get(v).id) {
					r1.setPoint(punti.get(v));
					punti.get(v).setRoom(r1);
					break;
				}
			}							
		}
		
	}
	
	// azzero tutte le liste di questo oggetto
	public void clear() {
		punti.clear();
		piani.clear();
		stanze.clear();
		percorsi.clear();
	}
	
	// GESTIONE COORDINATE /////////////////////////////////////////////////////////////////////////////////
	private void setLocation() {
		location = new Location(""+id);
		location.setLatitude(latitudine/1E6);
		location.setLongitude(longitudine/1E6);
	}
	
	public GeoPoint getGeopoint() {
		return new GeoPoint(latitudine, longitudine);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////
}
