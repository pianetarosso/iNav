package it.inav.base_objects;


public class Sons {

	public int costo;
	public boolean ascensore, scala;
	
	public Point figlio;

	public Sons(Path path, Point figlio) {
		this.costo = path.costo;
		this.ascensore = path.ascensore;
		this.scala = path.scala;
		this.figlio = figlio;
	}
	
	public Path returnPath(long id_point_A) {
		return new Path(costo, ascensore, scala, id_point_A, figlio.id);
	}
}
