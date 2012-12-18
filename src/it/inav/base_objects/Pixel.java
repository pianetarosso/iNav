package it.inav.base_objects;

// CLASSE PER LA GESTIONE IN UN OGGETTO UNICO DEI PIXEL SULL'IMMAGINE
public class Pixel {

	public int x;
	public int y;

	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	} 

	public void setY(int y) {
		this.y = y;
	}	
}
