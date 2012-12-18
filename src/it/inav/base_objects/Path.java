package it.inav.base_objects;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Path {

	public int costo;
	
	public String ascensore, scala;
	public Point a = null, b = null;

	public Path(long a, long b, String ascensore, String scala, List<Point> punti) {
		
		this.ascensore = ascensore;
		this.scala = scala;
		
		for (Point p : punti) {
			
			if (p.id == a) {
				this.a = p;
				p.addPath(this);
			}
			else if (p.id == b) {
				this.b = p;
				p.addPath(this);
			}
			
			if ((this.a != null) && (this.b != null))
				break;
		}
		
		// funzione per il calcolo del costo
	}
/*
	'a' : p.a.pk,
    'b' : p.b.pk,
    'ascensore' : p.ascensore,
    'scala' : p.scala
    
   */
	
	private static final String A = "a";
	private static final String B = "b";
	private static final String ASCENSORE = "ascensore";
	private static final String SCALA = "scala";
	
	
	public static Path parse(JSONObject p, List<Point> punti) throws JSONException {
		
		long a = p.getLong(A);
		long b = p.getLong(B);
		
		String ascensore = p.getString(ASCENSORE);
		String scala = p.getString(SCALA);
		
		return new Path(a, b, ascensore, scala, punti);
	}	
	
	public String toString() {
		
		String out = "Path:\n";
		
		out += "Point a:" + a.id;
		out += ", Point b:" + b.id;
		out += ", ascensore:" + ascensore;
		out += ", scala:" + scala;
		out += ", costo:" + costo;
		
		out += "\n";
		
		return out;
	}
}
