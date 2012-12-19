package it.inav.base_objects;

import it.inav.database.InitializeDB;

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
import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;

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
	///////////////////////////////////////////////////////

	// VARIABILI /////////////////////
	public long id;

	public String nome;
	public String descrizione;

	public URL link = null;
	public URI foto_link = null;

	public Bitmap foto;

	public int numero_di_piani;
	public int versione;

	public Date data_creazione;
	public Date data_update;

	public GeoPoint posizione;
	public GeoPoint[] geometria;
	////////////////////////////////////


	// OGGETTI /////////////////////////////////////////////
	protected List<Floor> piani = new ArrayList<Floor>();
	protected List<Point> punti = new ArrayList<Point>();
	protected List<Room> stanze = new ArrayList<Room>();
	protected List<Path> paths = new ArrayList<Path>();
	////////////////////////////////////////////////////////

	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

	// COSTRUTTORE STANDARD
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

	// Metodo per recuperare il building dal database
	public static Building getBuilding(Context context, long id) 
			throws SQLException, MalformedURLException, URISyntaxException {
		
		Building b = null;
		
		InitializeDB idb = new InitializeDB(context);
        idb.open();
        
        
        if (idb.existBuilding(id));
        	 b = idb.fetchBuilding(id);
        	
        idb.close();
		
        return b;
	}
	
	// Metodo per recuperare TUTTI i building dal database
	public static List<Building> getBuildings(Context context) 
			throws SQLException, MalformedURLException, URISyntaxException, 
			InterruptedException, ExecutionException {
		
		loadAllBuildings lab = new loadAllBuildings();
		lab.execute(context);
		
        return lab.get();	
	}
	
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
	
	// Costruttore che esegue il parsing di un JSON
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

	private static final String LIST = "lista";

	public static List<Building> Buildings(JSONObject json, String baseLink) 
			throws MalformedURLException, JSONException, ParseException, URISyntaxException {

		List <Building> b = new ArrayList<Building>();
		JSONArray array = (JSONArray) json.get(LIST);

		for(int i=0; i < json.length(); i++)
			b.add(new Building(array.getJSONObject(i), baseLink));

		return b;
	}

	
	// PARSING DEL JSON
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


	// ToSTRING
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



