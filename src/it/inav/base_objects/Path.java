package it.inav.base_objects;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;



public class Path {


	// COSTANTI PER PARSING /////////////////////
	public static final String ID = "id";
	public static final String A = "a";
	public static final String B = "b";
	public static final String ASCENSORE = "ascensore";
	public static final String SCALA = "scala";
	public static final String COSTO = "costo";
	////////////////////////////////////////////


	// VARIABILI ///////////////////////
	public int costo;

	public String ascensore, scala;
	public Point a = null, b = null;
	///////////////////////////////////


	// COSTRUTTORE
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

		// funzione per il calcolo del costo MANCANTE!!!!!!!!!!!
	}

	// COSTRUTTORE CON COSTO
	public Path(long a, long b, String ascensore, String scala, int costo, List<Point> punti) {

		Path p =new Path(a, b, ascensore, scala, punti);
		this.costo = costo;
		this.a = p.a;
		this.b = p.b;
		this.ascensore = p.ascensore;
		this.scala = p.scala;
	}



	// PARSER DEL JSON
	public static Path parse(JSONObject p, List<Point> punti) throws JSONException {

		long a = p.getLong(A);
		long b = p.getLong(B);

		String ascensore = p.getString(ASCENSORE);
		String scala = p.getString(SCALA);

		return new Path(a, b, ascensore, scala, punti);
	}	



	// ToSTRING
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
