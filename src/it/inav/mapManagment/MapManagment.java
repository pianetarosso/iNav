package it.inav.mapManagment;

import it.inav.base_objects.Building;
import it.inav.base_objects.Path;
import it.inav.base_objects.Point;
import it.inav.graphics.MapView;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.util.Log;
import android.widget.TextView;

/**Classe per la gestione della comunicazione della mappa con l'utente, e il calcolo dei percorsi.
 * 
 * @author Marco Fedele
 *
 */
public class MapManagment {

	/** MapView su cui si opera */
	private MapView cv;

	/** TextView della MapView che mostra i messaggi all'utente */
	private TextView tv;

	/** Edificio su mostrato nella MapView */ 
	private Building building;

	/** Posizione corrente dell'utente */
	private Point user_point = null;

	/** Punto termine della navigazione */
	private Point end_point = null;

	/** Percorso della navigazione sul piano */
	private List<Point> percorso;


	/** Costruttore
	 * 
	 * @param cv MapView su cui si opera 
	 * @param tv TextView che visualizza i messaggi all'utente
	 */
	public MapManagment(MapView cv, TextView tv, Building building) {

		this.cv = cv;
		this.tv = tv;
		this.building = building;
	}
	

	/** Metodo per mostrare all'utente un determinato punto (di solito una stanza) 
	 *  
	 * @param punto Point posizione su cui muoversi
	 */
	public void moveToPoint(PointF punto) {

		moveToPoint(getPointFromPosition(punto));
	}
	
	
	/** Metodo per mostrare all'utente un determinato punto (di solito una stanza) 
	 * 
	 * @param punto Point posizione su cui muoversi
	 */
	public void moveToPoint(Point punto) {
		
		// imposto il punto sulla mappa
		cv.goToPoint(punto);
	}

	/** Recupero il rispettivo punto dalla posizione
	 * 
	 * @param point PointF coordinate del punto sulla mappa
	 * @return Point corrispondente alle coordinate dal Building 
	 */
	private Point getPointFromPosition(PointF point) {
		
		List<Point> b_p = building.getPunti();
		
		for(Point p : b_p) 
			if (point == p.posizione)
				return p;
		
		return null;
	}

	/** Metodo che calcola il percorso dalla posizione dell'utente a quella cercata
	 * e la manda sulla mappa
	 *  
	 * @param punto Point punto di arrivo della navigazione
	 */
	public void navigateTo(Point punto) {

	
		// calcolo il percorso
		Coppia percorso = calculatePath(user_point, punto, new Coppia(user_point));

		Log.i("Costo del percorso", ""+percorso.peso);

		// testo che la lunghezza del percorso sia "almeno" di 2 punti
		if (percorso.points.size() < 2)
			return;

		// imposto il punto finale 
		end_point = punto;

		// imposto il percorso
		this.percorso = percorso.points;

		// piano di partenza
		int piano_partenza = this.percorso.get(0).piano;

		// posizione dell'ultimo punto su questo piano
		int last_point_position = 0;

		for (Point p : this.percorso) {
			if (p.piano != piano_partenza)
				break;
			else
				last_point_position++;
		}

		// costruisco la prima lista di punti sul piano dell'utente
		Point[] list = this.percorso.subList(0, last_point_position).toArray(new Point[0]);

		cv.setNavigation(list, user_point.piano);	
	}


	/** Imposto la posizione dell'utente, tenendo conto se l'utente sta facendo una navigazione
	 * oppure no
	 * 
	 * @param point Point punto in cui si trova l'utente
	 */
	public void setUserPosition(PointF pointF) {

		// recupero il punto dalla posizione
		Point point = getPointFromPosition(pointF);
		
		// se è in corso una navigazione 
		if (end_point != null) {

			// verifico innanzitutto che il punto sia nella lista del percorso.
			// se così non è reimposto la ricerca del percorso
			if (!percorso.contains(point)) {
				this.user_point = point;
				navigateTo(end_point);
			}

			// sono arrivato alla fine, termino la navigazione
			// e informo l'utente che è arrivato
			else if (point == end_point) {
				cv.stopNavigation();
				// metto il messaggio
			}
			
			// verifico che il piano del punto successivo sia sempre lo stesso, 
			// altrimenti costruisco una nuova lista 
			// di punti e la passo alla MapView, cambiando anche il piano
			else if (user_point.piano != percorso.get(percorso.indexOf(user_point) + 1).piano) {
				
				int position = percorso.indexOf(user_point);
				
				int start = position + 1;
				int stop = 0;
				
				for (int i = start; i < percorso.size(); i++) {
					
					// In questa condizione mi trovo in un "punto di transito" tra i piani,
					// quindi non lo considerò. Provvederò ad avvisare gli utenti di quanti 
					// piani salire o scendere
					if ((stop < 1) && (percorso.get(i-1) != percorso.get(i))) {
						start = i;
						stop = 0;
					}
					// caso base, sto tenendo traccia dei punti sul piano successivo
					else if (percorso.get(i-1) == percorso.get(i))
						stop = i;
					
					// altro cambio di piano 
					else
						break;
				}
				
				// creo la sottolista di punti del piano
				Point[] list = percorso.subList(start, stop).toArray(new Point[0]);
				
				// imposto la nuova navigazione
				cv.setNavigation(list, point.piano);
			}

		}

		// imposto la nuova posizione dell'utente
		this.user_point = point;

		// passo il punto alla MapView
		cv.setUserPosition(point);
	}



	/** Metodo di ricerca del percorso più breve all'interno dell'edificio
	 * 
	 * @param punto Point punto in cui ci si trova durante la ricerca
	 * @param punto_arrivo Point punto a cui si deve arrivare
	 * @param points Coppia vedi {@link Coppia} 
	 * @return Null se nessun percorso viene trovato. Altrimenti un oggetto Coppia 
	 * con il peso e il percorso più breve dal punto in cui si trova l'utente a quello cercato.
	 */
	private Coppia calculatePath(Point punto, Point punto_arrivo, Coppia points) {

		// recupero la lista di paths
		List<Path> paths = punto.paths;

		//  coppia di output
		Coppia out = null;

		// scansiono tutte le path
		for(Path path : paths) {

			// estrapolo il punto "differente" dalla path
			Point p = path.a;
			if (p == punto)
				p = path.b;

			// nel caso il punto trovato sia uguale al "punto precedente" nella lista
			// vado alla path successiva, per evitare il "torno indietro"
			if (p != points.points.get(points.points.size() - 2)) {

				// calcolo il nuovo peso
				int peso = points.peso + path.costo;

				// creo un nuovo oggetto Coppia con i nuovi parametri
				Coppia new_coppia = new Coppia(peso, points.points, p);

				// se sono arrivato all'arrivo, restituisco l'oggetto e esco
				if (p == punto_arrivo)
					return new_coppia;

				else {
					// calcolo un nuovo oggetto coppia con i nuovi parametri
					Coppia new_out = calculatePath(p, punto_arrivo, new_coppia);

					// inizializzo la variabile se è null
					if (out == null)
						out = new_out;

					else if (new_out != null) 
						// se il peso è minore, cambio l'out
						if (out.peso > new_out.peso)
							out = new_out;
				}

			}
		}

		// restituisco l'out (sperando che sia giusto :P )
		return out;

	}



	/** Classe "Coppia" per la gestione di un oggetto peso - lista di Point*/
	public class Coppia {

		public List<Point> points;
		public int peso;

		/** Inizializzazione dell'oggetto con la posizione dell'utente e il peso a zero
		 * 
		 * @param user Point punto in cui si trova l'utente in quel momento
		 */
		public Coppia(Point user) {
			points = new ArrayList<Point>();
			points.add(user);
			peso = 0;
		}

		/** Inizializzazione "in corsa" dell'oggetto, copiando la lista di punti
		 * 
		 * @param peso int peso del percorso
		 * @param points List<Point> lista dei punti
		 * @param last_point Point ultimo punto scansionato, va ad aggiungersi alla lista qui sopra
		 */
		public Coppia(int peso, List<Point> points, Point last_point) {
			this.peso = peso;
			this.points = new ArrayList<Point>(points);
			this.points.add(last_point);
		}
	}

}
