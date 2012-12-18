package it.inav.base_objects;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Floor {

	public long id;
	public String link;
	public double bearing;
	public int numero_di_piano;
	public Bitmap immagine_piano;
	public List<Point> punti_del_piano;
	
	
	public Floor(long id, String link, double bearing, int numero_di_piano) {
		this.id = id;
		this.link = link;
		this.bearing = bearing;
		this.numero_di_piano = numero_di_piano;
		punti_del_piano = new ArrayList<Point>();
	}
	
	// CARICAMENTO DELL'IMMAGINE DEL PIANO
	public boolean loadImage() {
		immagine_piano = BitmapFactory.decodeFile(link);
		return immagine_piano != null;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
