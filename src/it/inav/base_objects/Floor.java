package it.inav.base_objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class Floor {

	public long id;
	public int numero_di_piano;
	public String descrizione;
	
	public double bearing;
	public Bitmap immagine;
	public URL link_immagine;
	
	public List<Point> punti;
	
	
	
	public Floor(long id, String link, double bearing, int numero_di_piano, String descrizione, List<Point> punti) throws MalformedURLException {
		this.id = id;
		this.link_immagine = new URL(link);
		this.bearing = bearing;
		this.numero_di_piano = numero_di_piano;
		this.descrizione = descrizione;
		
		this.punti = new ArrayList<Point>();
		for(Point p : punti)
			if (p.piano == this.numero_di_piano)
				this.punti.add(p);
	}
	
	/*
	 			'numero_di_piano' : f.numero_di_piano,
                'bearing' : str(f.bearing),
                'id' : f.pk,
                'immagine' : f.immagine.url,
                'descrizione' : f.descrizione
	 */
	
	
	private static final String ID = "id";
	private static final String NUMERO_DI_PIANO = "numero_di_piano";
	private static final String BEARING = "bearing";
	private static final String IMMAGINE = "immagine";
	private static final String DESCRIZIONE = "descrizione";
	
	
	public static Floor parse(JSONObject f, String baseLink, List<Point> punti) throws JSONException, MalformedURLException {
		
		long id = f.getLong(ID);
		int numero_di_piano = f.getInt(NUMERO_DI_PIANO);
		double bearing = Double.parseDouble(f.getString(BEARING));
		String link_immagine = baseLink + f.getString(IMMAGINE);
		String descrizione = f.getString(DESCRIZIONE);
		
		return new Floor(id, link_immagine, bearing, numero_di_piano, descrizione, punti);
	}
	
	
	
	
	
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
	
	// CARICAMENTO DELL'IMMAGINE DEL PIANO
	public boolean loadImage() {
		//immagine= BitmapFactory.decodeFile(link_immagine);
		//return immagine_piano != null;
		return true;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public static Floor parseFloor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}
}
