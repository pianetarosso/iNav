package it.inav.base_objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class Floor {

	
	// COSTANTI PER IL PARSING ///////////////////
	public static final String ID = "id";
	public static final String NUMERO_DI_PIANO = "numero_di_piano";
	public static final String BEARING = "bearing";
	public static final String IMMAGINE = "immagine";
	public static final String DESCRIZIONE = "descrizione";
	//////////////////////////////////////////////
	
	// VARIABILI ///////////////
	public long id;
	public int numero_di_piano;
	public String descrizione;

	public double bearing;

	public Bitmap immagine;
	public URL link_immagine;
	////////////////////////////
	
	// OGGETTI ////////////////
	public List<Point> punti;



	// COSTRUTTORE
	public Floor(long id, String link_immagine, double bearing, int numero_di_piano, 
			String descrizione, List<Point> punti) throws MalformedURLException {
		this.id = id;
		this.link_immagine = new URL(link_immagine);
		this.bearing = bearing;
		this.numero_di_piano = numero_di_piano;
		this.descrizione = descrizione;

		this.punti = new ArrayList<Point>();
		for(Point p : punti)
			if (p.piano == this.numero_di_piano)
				this.punti.add(p);
	}



	
	// PARSING DA JSON
	public static Floor parse(JSONObject f, String baseLink, List<Point> punti) 
			throws JSONException, MalformedURLException {

		long id = f.getLong(ID);
		int numero_di_piano = f.getInt(NUMERO_DI_PIANO);
		double bearing = Double.parseDouble(f.getString(BEARING));

		String link = f.getString(IMMAGINE);
		if (link.startsWith("/"))
			link = link.substring(1, link.length());
		String link_immagine = baseLink + link;

		String descrizione = f.getString(DESCRIZIONE);

		return new Floor(id, link_immagine, bearing, numero_di_piano, descrizione, punti);
	}





	// ToSTRING
	public String toString() {

		String out = "";

		out += "Piano:\n";
		out += "id:"+id;
		out += ", numero di piano:"+numero_di_piano;
		out += ", immagine:"+link_immagine.toString();
		out += ", descrizione:"+descrizione;
		out += "\n";

		for (Point p : punti)
			out += p.toString();

		return out;
	}
}
