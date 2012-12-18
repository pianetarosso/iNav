package it.inav.base_objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class Room {

	public Point punto;
	public String nome_stanza;
	public List<String> persone;
	public String altro;
	public URL link = null;
	
	private static final String peopleExpression ="[\\s[a-z][A-Z]]\\,[\\s[a-z][A-Z]]";
	
	public Room(long punto, String nome_stanza, String persone, 
				String altro, String link, List<Point> points) throws MalformedURLException {
		
		if (link.length() > 0)
			this.link = new URL(link);
		
		this.nome_stanza = nome_stanza;
		this.altro = altro;
		
		this.persone = new ArrayList<String>();
		String[] p_temp = persone.split(peopleExpression);
		for(String s : p_temp)
			this.persone.add(s);
		
		for(Point p : points)
			if (p.id == punto) {
				this.punto = p;
				p.stanza = this;
				break;
			}
		
	}	
	
	/*
	 			'punto' : r.punto.pk,
                'nome_stanza' : r.nome_stanza,
                'persone' : r.persone,
                'altro' : r.altro,
                'link' : r.link
	 */

	private static final String PUNTO = "punto";
	private static final String NOME_STANZA = "nome_stanza";
	private static final String PERSONE = "persone";
	private static final String ALTRO = "altro";
	private static final String LINK = "link";
	
	public static Room parse(JSONObject r, List<Point> punti) throws JSONException, MalformedURLException {
		
		long punto = r.getLong(PUNTO);
		String nome_stanza = r.getString(NOME_STANZA);
		String persone = r.getString(PERSONE);
		String altro = r.getString(ALTRO);
		String link = r.getString(LINK);
		
		return new Room(punto, nome_stanza, persone, altro, link, punti);
	}
	
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
