package it.inav.base_objects;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;



/** Classe per la gestione delle PATH dell'edificio
 * 
 * @author Marco Fedele
 *
 */
public class Path {


	// COSTANTI PER PARSING /////////////////////

	/** id della path */
	public static final String ID = "id";

	/** id del primo punto */
	public static final String A = "a";

	/** id del secondo punto */
	public static final String B = "b";

	/** stringa identificativa dell'ascensore */
	public static final String ASCENSORE = "ascensore";

	/** stringa identificativa delle scale */
	public static final String SCALA = "scala";

	/** "costo" della path */
	public static final String COSTO = "costo";
	////////////////////////////////////////////



	// COSTI //////////////////////////////////

	/** costo di default dell'ascensore (per 1 piano) */
	private static final int COSTO_ASCENSORE = 400;

	/** costo di default delle scale (per 1 piano) */
	private static final int COSTO_SCALA = 500;
	
	/** width dell'immagine di default */
	private static final int FIXED_WIDTH = 1024;
	
	/** heigth dell'immagine di default */
	private static final int FIXED_HEIGTH = 768;

	///////////////////////////////////////////

	// VARIABILI ///////////////////////

	/** costo del percorso */
	public int costo = -1;

	/** stringa identificativa dell'ascensore */
	public String ascensore;

	/** stringa identificativa della scala */
	public String scala;

	/** punto "di partenza" della path */
	public Point a = null;

	/** punto "di arrivo" della path */
	public Point b = null;
	///////////////////////////////////


	/** Costruttore
	 * 
	 * @param a long identificativo del punto
	 * @param b long identificativo del punto
	 * @param ascensore String identificativo dell'ascensore
	 * @param scala String identificativo della scala
	 * @param punti List(Point) lista di punti su cui effettuare la ricerca degli id
	 */
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
	}


	/** Costruttore con "costo" 
	 * 
	 * @param a long identificativo del punto
	 * @param b long identificativo del punto
	 * @param ascensore String identificativo dell'ascensore
	 * @param scala String identificativo della scala
	 * @param punti List(Point) lista di punti su cui effettuare la ricerca degli id
	 * @param costo int peso del percorso
	 */
	public Path(long a, long b, String ascensore, String scala, int costo, List<Point> punti) {

		Path p = new Path(a, b, ascensore, scala, punti);
		this.costo = costo;
		this.a = p.a;
		this.b = p.b;
		this.ascensore = p.ascensore;
		this.scala = p.scala;
	}
	
	
	/** Metodo per calcolare il "peso" della distanza tra due punti. 
	 * Se si tratta di un ascensore o di una scala imposto i valori predefiniti, altrimenti 
	 * provvedo a scalare le dimensioni passate (per uniformare i pesi indipendentemente dalla
	 * dimensione delle immagini) e a calcolare la distanza lineare tra i due punti, che diventa
	 * il nuovo peso.
	 * 
	 * @param w int larghezza della mappa in pixel
	 * @param h int altezza della mappa in pixel
	 * 
	 */
	public void setWeight(int w, int h) {
		
		if (this.ascensore.length() > 0)
			this.costo = COSTO_ASCENSORE;
		
		else if (this.scala.length() > 0)
			this.costo = COSTO_SCALA;
		
		else {
			
			// calcolo i fattori di scala
			double factor_w = FIXED_WIDTH / w;
			double factor_h = FIXED_HEIGTH / w;
			
			// scalo le distanze 
			double dx = Math.abs(a.posizione.x - b.posizione.x) * factor_w;
			double dy = Math.abs(a.posizione.y - b.posizione.y) * factor_h;
			
			// calcolo la lunghezza
			double length = Math.sqrt(dx * dx - dy * dy);
			
			// imposto il costo
			this.costo = (int)length;
		}
	}


	/** Metodo per il parsing del JSON 
	 * 
	 * @param p JSONObject oggetto json contenente le path scaricato dal server
	 * @param punti List(Point) lista di punti, necessaria per trovare una corrispondenza tra gli
	 * id dei punti della path e gli oggetti {@link Point} salvati
	 * @return new Path
	 * @throws JSONException
	 */
	public static Path parse(JSONObject p, List<Point> punti) throws JSONException {

		long a = p.getLong(A);
		long b = p.getLong(B);

		String ascensore = p.getString(ASCENSORE);
		String scala = p.getString(SCALA);

		return new Path(a, b, ascensore, scala, punti);
	}	


	/** Metodo toString, converte l'oggetto in una stringa human-redeable */
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
