package it.inav.base_objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class Room {


	// COSTANTI PER IL PARSER //////////////////////
	public static final String ID = "id";
	public static final String PUNTO = "punto";
	public static final String NOME_STANZA = "nome_stanza";
	public static final String PERSONE = "persone";
	public static final String ALTRO = "altro";
	public static final String LINK = "link";
	////////////////////////////////////////////////


	// VARIABILI ///////////////////
	public Point punto;
	public String nome_stanza;
	public List<String> persone;
	public String altro;
	public URL link = null;
	/////////////////////////////////

	private static final String peopleExpression ="[\\s[a-z][A-Z]]\\,[\\s[a-z][A-Z]]";



	// COSTRUTTORE
	public Room(long punto, String nome_stanza, String persone, 
			String altro, String link, List<Point> points) 
					throws MalformedURLException {

		setRoom(nome_stanza, persone, altro, link);

		for(Point p : points)
			if (p.id == punto) {
				this.punto = p;
				p.stanza = this;
				break;
			}
	}	
	
	public Room(String nome_stanza, String persone, 
			String altro, String link, Point punto) 
					throws MalformedURLException {
		
		setRoom(nome_stanza, persone, altro, link);
		this.punto = punto;
	}

	// METODI COMUNI DEI COSTRUTTORI
	private void setRoom(String nome_stanza, String persone, 
			String altro, String link) throws MalformedURLException {
		
		if (link.length() > 0)
			this.link = new URL(link);

		this.nome_stanza = nome_stanza;
		this.altro = altro;

		this.persone = new ArrayList<String>();
		String[] p_temp = persone.split(peopleExpression);
		for(String s : p_temp)
			this.persone.add(s);
	}

	// PARSER DEL JSON
	public static Room parse(JSONObject r, List<Point> punti) 
			throws JSONException, MalformedURLException {

		long punto = r.getLong(PUNTO);
		String nome_stanza = r.getString(NOME_STANZA);
		String persone = r.getString(PERSONE);
		String altro = r.getString(ALTRO);
		String link = r.getString(LINK);

		return new Room(punto, nome_stanza, persone, altro, link, punti);
	}


	public String getPersone() {
		
		String out = "";
		
		for(String p : persone)
			out += p + ", ";
		
		return out.substring(0, out.length()-2);
	}

	// ToSTRING
	public String toString() {

		String out = "Room:\n";

		out += "punto:"+punto.id;
		out += ", nome della stanza:"+nome_stanza;
		out += ", altro:"+altro;

		if (link != null)
			out += ", link:"+link.toString();

		out += ", persone: {";
		for(String p : persone)
			out += p +", ";

		if (persone.size() > 0)
			out = out.substring(0, out.length() -2);

		out += "}\n";

		return out;
	}
}
