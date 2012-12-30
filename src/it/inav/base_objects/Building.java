package it.inav.base_objects;

import it.inav.Memory.SP;
import it.inav.database.InitializeDB;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;



/** Classe per la gestione completa dell'edificio. 
 * 
 * <br />
 * 
 * Sono presenti inoltre i metodi per il parsing del JSON proveniente dal server e
 * per il caricamento dell'edificio dal database
 * 
 * @author Marco Fedele
 *
 */
public class Building {


	// COSTANTI PER IL PARSING ////////////////////////////
	public static final String BUILDING_TAG = "building";
	public static final String FLOORS_TAG = "piani";
	public static final String POINTS_TAG = "punti";
	public static final String PATHS_TAG = "paths";
	public static final String ROOMS_TAG = "stanze";
	
	public static final String ID = "id";
	public static final String NOME = "nome";
	public static final String DESCRIZIONE = "descrizione";
	public static final String LINK = "link";
	public static final String NUMERO_DI_PIANI = "numero_di_piani";
	public static final String FOTO = "foto";
	public static final String VERSIONE = "versione";
	public static final String DATA_C = "data_creazione";
	public static final String DATA_U = "data_update";
	public static final String POSIZIONE = "posizione";
	public static final String GEOMETRIA = "geometria";
	
	/** Identificativo usato per quando viene effettuato il recupero di una "lista" di edifici */
	private static final String LIST = "lista";
	///////////////////////////////////////////////////////

	// VARIABILI /////////////////////
	
	/** id dell'edificio, è lo stesso presente sul server */
	public long id;

	/** nome dell'edificio */
	public String nome;
	
	/** descrizione */
	public String descrizione;

	/** eventuale link alla web page dell'edificio */
	public URL link = null;
	
	/** link della foto, può essere sia locale che remoto */
	public URI foto_link = null;

	/** contenitore della foto */
	public Bitmap foto;

	/** numero dei piani mappati */
	public int numero_di_piani;
	
	/** versione della mappa, usata in fase di update */
	public int versione;

	/** data di creazione dell'edificio (sul server) */
	public Date data_creazione;
	
	/** data di update dell'edificio (sul server) */
	public Date data_update;

	/** posizione dell'edificio sulla mappa (Google Maps) */
	public GeoPoint posizione;
	
	/** geometria dell'edificio sulla mappa (Google Maps) */
	public GeoPoint[] geometria;
	////////////////////////////////////


	// OGGETTI /////////////////////////////////////////////
	
	/** Lista dei piani */
	protected List<Floor> piani = new ArrayList<Floor>();
	
	/** lista dei punti */
	protected List<Point> punti = new ArrayList<Point>();
	
	/** lista delle stanze */
	protected List<Room> stanze = new ArrayList<Room>();
	
	/** lista dei percorsi */
	protected List<Path> paths = new ArrayList<Path>();
	////////////////////////////////////////////////////////

	@SuppressLint("SimpleDateFormat")
	/** Parserer della data proveniente dal server */
	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

	
	/** Costruttore dell'edificio, specifico per il caricamento dei dati dal database, 
	 * effettua il parsing di alcuni campi più complessi
	 * 
	 * @param id long id dell'edificio (lo stesso presente sul server)
	 * @param nome String nome dell'edificio
	 * @param descrizione String (eventuale) descrizione
	 * @param link String eventuale link ad una web page
	 * @param foto_link String link (locale o remoto) dell'immagine dell'edificio
	 * @param numero_di_piani int numero di piani mappati
	 * @param versione int versione dell'edificio
	 * @param data_creazione long data di creazione dell'edificio, in ms
	 * @param data_update long data di update dell'edificio, in ms
	 * @param posizione String posizione dell'edificio nel formato [latitudine,longitudine]
	 * @param geometria String gemetria dell'edificio sulla mappa nel formato [[lat,lng,lat,lng,..]]
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public Building (long id, String nome, String descrizione, String link, String foto_link,
			int numero_di_piani, int versione, long data_creazione,
			long data_update, String posizione, String geometria) 
					throws MalformedURLException, URISyntaxException {

		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;

		if (link.length() > 0)
			this.link = new URL(link);
		
		if (link.length() > 0)
			this.foto_link = new URI(foto_link);

		this.numero_di_piani = numero_di_piani;
		this.versione = versione;

		this.data_creazione = new Date();
		this.data_creazione.setTime(data_creazione);

		this.data_update = new Date();
		this.data_update.setTime(data_update);

		String[] p = posizione.substring(1, posizione.length() -1).split(",");
		this.posizione = new GeoPoint(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
		
		String[] g = geometria.substring(1, geometria.length() -1).split("\\[|\\],\\[|\\]");
		
		int counter = 0;
		
		for (String gi : g)
			if (gi.length() > 5)
				counter++;
		
		this.geometria = new GeoPoint[counter];
		
		counter = 0;
		for(String gi : g) {
			if (gi.length() > 10) {
				p = gi.split(",");
				this.geometria[counter] = new GeoPoint(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
				counter++;
			}
		}
	}

	/** Metodo per caricare il building dal database
	 * 
	 * @param context Context "ambiente" necessario per accedere al database
	 * @param id long identificatore dell'edificio da caricare 
	 * @param progress ProgressDialog progresso del caricamento dell'edificio in 
	 * 6 parti: edificio, punti, piani, percorsi, stanze e immagini
	 * @return NULL se l'edificio NON è stato trovato, inoltre chiama {@link SP#addDamagedBuilding(Context, long)}
	 * @return Building se l'edificio è presente nel database
	 */
	public static Building getBuilding(Context context, long id, ProgressDialog progress) {
		
		try {
			
			Building b = null;
			
			// apro il database
			InitializeDB idb = new InitializeDB(context);
	        idb.open();
	        
	        
	        if (idb.existBuilding(id));
	        	 b = idb.fetchBuilding(id, progress);
	        	
	        idb.close();
			
	        // carico le immagini
	        loadImages(b);
	        progress.setProgress(6);

	        return b;
	        
		} catch (Exception e) {
			
			e.printStackTrace();
			SP.addDamagedBuilding(context, id);
			return null;
		}
	}
	
	
	/** Caricamento delle immagini dell'edificio dalla memoria (SD)
	 * 
	 * @param b Building edificio di cui recuperare le immagini
	 * @throws FileNotFoundException
	 */
	private static void loadImages(Building b) 
			throws FileNotFoundException {
		
		if (b.foto_link != null) {
			b.foto = BitmapFactory.decodeFile(b.foto_link.getPath());
			if (b.foto == null)
				throw new FileNotFoundException();
		}
		
		for(Floor f : b.getPiani()) {
			f.immagine = BitmapFactory.decodeFile(f.link_immagine.getPath());
			if (f.immagine == null)
				throw new FileNotFoundException();
		}
		
	}
	
	
	/** Metodo per recuperare TUTTI gli edifici dal database, viene caricato SOLO il campo BUILDING, 
	 * senza nient'altro. Per il recupero viene utilizzata un'AsyncTask {@link loadAllBuildings}
	 * @param context Context necessario per "aprire" il database
	 * @return List(Building)
	 * @throws SQLException
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static List<Building> getBuildings(Context context) 
			throws SQLException, MalformedURLException, URISyntaxException, 
			InterruptedException, ExecutionException {
		
		loadAllBuildings lab = new loadAllBuildings();
		lab.execute(context);
		
        return lab.get();	
	}
	
	/** AsyncTask per caricare dal database tutti gli edifici dal database. 
	 * Viene caricato SOLO il campo BUILDING
	 * 
	 *@author Marco Fedele
	 */
	public static class loadAllBuildings extends AsyncTask <Context, Void, List<Building>> {

		@Override
		protected List<Building> doInBackground(Context... params) {
			List<Building> b = new ArrayList<Building>();
			
			InitializeDB idb = new InitializeDB(params[0]);
	        idb.open();
	        
	        try {
				b = idb.fetchBuildings();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
	        	
	        idb.close();
	        
			return b;
		}
		
	}
	
	/** Costruttore, crea un edificio effettuando il parsing di un JSON. 
	 * Si occupa anche di separare dal JSON i vari campi (punti, path, room, ..) e chiamare le funzioni
	 * idonee degli altri oggetti per parsarli.
	 * 
	 * @param json JSONObject da parsare
	 * @param baseLink String link di base del sito, necessario per ricostruire i link alle immagini
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public Building (JSONObject json, String baseLink) 
			throws JSONException, MalformedURLException, ParseException, URISyntaxException {

		// carico i valori del building
		JSONObject b = json.getJSONObject(BUILDING_TAG);
		parseBuilding(b, baseLink);

		// carico i punti
		JSONArray points = json.getJSONArray(POINTS_TAG);
		for (int i=0; i < points.length(); i++)
			this.punti.add(Point.parse((JSONObject) points.get(i)));

		// carico i piani
		JSONArray floors = json.getJSONArray(FLOORS_TAG);
		for (int i=0; i < floors.length(); i++) 
			this.piani.add(Floor.parse((JSONObject) floors.get(i), baseLink, this.punti));

		// carico i percorsi
		JSONArray paths = json.getJSONArray(PATHS_TAG);
		for (int i=0; i < paths.length(); i++) 
			this.paths.add(Path.parse((JSONObject) paths.get(i), this.punti));

		// carico le stanze
		JSONArray rooms = json.getJSONArray(ROOMS_TAG);
		for (int i=0; i < rooms.length(); i++) 
			this.stanze.add(Room.parse((JSONObject) rooms.get(i), this.punti));
	}

	

	/** Costruttore, chiamato quando il JSONObject contiene una lista di edifici
	 * 
	 * @param json JSONObject da parsare
	 * @param baseLink String link di base del sito, necessario per ricostruire i link alle immagini
	 * @return List(Building)
	 * @throws MalformedURLException
	 * @throws JSONException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public static List<Building> Buildings(JSONObject json, String baseLink) 
			throws MalformedURLException, JSONException, ParseException, URISyntaxException {

		List <Building> b = new ArrayList<Building>();
		JSONArray array = (JSONArray) json.get(LIST);

		for(int i=0; i < json.length(); i++)
			b.add(new Building(array.getJSONObject(i), baseLink));

		return b;
	}

	
	/** Metodo per il parsing del JSON dell'edificio. SOLO del BUILDING, di nessun'altro oggetto.
	 * 
	 * @param b JSONObject oggetto da parsare
	 * @param baseLink String link di base del sito, necessario per ricostruire i link alle immagini
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	private void parseBuilding(JSONObject b, String baseLink) 
			throws JSONException, MalformedURLException, ParseException, URISyntaxException {

		this.id = b.getLong(ID);
		this.nome = b.getString(NOME);
		this.descrizione = b.getString(DESCRIZIONE);

		String link = b.getString(LINK);
		if (link.length() > 0)
			this.link = new URL(link);

		String foto_link = b.getString(FOTO);
		if (foto_link.length() > 0)
			this.foto_link = new URI(baseLink.substring(0, baseLink.length() - 1) + foto_link);

		this.numero_di_piani = b.getInt(NUMERO_DI_PIANI);
		this.versione = b.getInt(VERSIONE);


		this.data_creazione = format.parse(b.getString(DATA_C));
		this.data_update = format.parse(b.getString(DATA_U));


		JSONArray posizione = (JSONArray) b.get(POSIZIONE);
		this.posizione = new GeoPoint((int)(posizione.getDouble(1) * 1E6), 
				(int)(posizione.getDouble(0) * 1E6)); 

		JSONArray geometrie =  (JSONArray)(b.get(GEOMETRIA));
		geometrie = (JSONArray) geometrie.get(0);
		this.geometria = new GeoPoint[geometrie.length()];

		for (int i=0; i < geometrie.length(); i++) {
			JSONArray geometria = geometrie.getJSONArray(i);
			GeoPoint t_g = new GeoPoint((int)(geometria.getDouble(1) * 1E6), 
					(int)(geometria.getDouble(0) * 1E6)); 
			this.geometria[i] = t_g;
		}
	}

	
	/** Metodo per impostare i pesi delle path, chiamato solo dopo
	 * aver caricato le immagini dal server.
	 */
	public void setWeigths() {
		
		// imposto la dimensione delle immagini per ogni piano
		for(Floor f : piani) {
			
			int w = f.immagine.getWidth();
			int h = f.immagine.getHeight();
			
			// scansiono tutti i punti del piano
			for (Point p : f.punti) 
				
				// scansiono tutte le path di ogni punto
				for (Path path: p.paths) 
					
					// se il costo è -1 vuol dire che non è ancora stato impostato
					if (path.costo < 0)
						path.setWeight(w, h);
				
		}
	}

	/** Metodo per liberare la memoria dalle immagini, usato per eliminare problemi di carico della memoria*/
	public void freeMemory() {
		foto.recycle();
		
		for(Floor f : piani)
			f.immagine.recycle();
	}

	
	public GeoPoint getPosizione() {
		return posizione;
	}


	public void setPosizione(GeoPoint posizione) {
		this.posizione = posizione;
	}


	public List<Floor> getPiani() {
		return piani;
	}


	public void setPiani(List<Floor> piani) {
		this.piani = piani;
	}


	public List<Point> getPunti() {
		return punti;
	}


	public void setPunti(List<Point> punti) {
		this.punti = punti;
	}


	public List<Room> getStanze() {
		return stanze;
	}


	public void setStanze(List<Room> stanze) {
		this.stanze = stanze;
	}


	public List<Path> getPaths() {
		return paths;
	}


	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}


	/** Metodo toString */
	public String toString() {

		String out = "Building:\n";
		out += "id:"+id;
		out += ", nome:"+nome;
		out += ", descrizione:"+descrizione;

		if (link != null)
			out += ", link:"+link.toString();

		if (foto_link != null)
			out += ", foto:"+foto_link.toString();

		out += ", numero di piani:"+numero_di_piani;
		out += ", versione:"+versione;

		out += ", data creazione:"+data_creazione.toString();
		out += ", data update:"+data_update.toString();

		out += ", posizione:"+posizione.toString();
		out += ", geometria:[";

		for (GeoPoint g : geometria)
			out += g.toString() + ", ";
		if (out.endsWith(", "))
			out = out.substring(0, out.length() - 2);
		out += "]\n";

		for (Floor f : piani)
			out += f.toString();

		for (Path p : paths)
			out += p.toString();

		for (Room r : stanze)
			out += r.toString();

		return out;
	}
}



