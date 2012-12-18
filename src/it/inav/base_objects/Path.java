package it.inav.base_objects;

public class Path {

	public int costo;
	public boolean ascensore, scala;
	
	public long id_point_A, id_point_B;

	public Path(int costo, boolean ascensore, boolean scala,
			long id_point_A, long id_point_B) {
		
		this.costo = costo;
		this.ascensore = ascensore;
		this.scala = scala;
		this.id_point_A = id_point_A;
		this.id_point_B = id_point_B;
	}	
}
