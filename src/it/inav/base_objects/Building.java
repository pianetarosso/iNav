package it.inav.base_objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Building {

	public long id;
	
	public String nome;
	public String descrizione;
	
	public URL link;
	public URL foto_link;
	
	public int numero_di_piani;
	public int versione;
	
	public Date data_creazione;
	public Date data_update;
	
	public GeoPoint posizione;
	public GeoPoint[] geometria;
	
	public List<Floor> piani = new ArrayList<Floor>();
	public List<Point> punti = new ArrayList<Point>();
	public List<Room> stanze = new ArrayList<Room>();
	public List<Path> paths = new ArrayList<Path>();

	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	// COSTRUTTORE
	public Building (long id, String nome, String descrizione, String latitudine, String longitudine,
			String link, String foto_link, int numero_di_piani, int versione, String data_creazione,
			String data_update, String posizione, String geometria) throws MalformedURLException {
		
		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;
		
		this.link = new URL(link);
		this.foto_link = new URL(foto_link);
		
		this.numero_di_piani = numero_di_piani;
		this.versione = versione;
		
		this.data_creazione = new Date();
		this.data_creazione.setTime(Date.parse(data_creazione));
		
		this.data_update = new Date();
		this.data_update.setTime(Date.parse(data_update));
		
		
		// mancano posizione e geometria
		
	}
	 
	
	/*
	'nome' : b.nome,
    'descrizione' : b.descrizione,
    'link' : b.link,
    
    'numero_di_piani' : b.numero_di_piani,
    'foto' : "",
    
    'versione' : b.versione,
    'data_creazione' : str(b.data_creazione),
    'data_update' : str(b.data_update),
    
    'posizione' : b.posizione.coords,
    'geometria' : b.geometria.coords
	*/
	
	private static final String BUILDING_TAG = "building";
	private static final String FLOORS_TAG = "piani";
	private static final String POINTS_TAG = "punti";
	private static final String PATHS_TAG = "paths";
	private static final String ROOMS_TAG = "stanze";
	
	// Costruttore che fa il parsing
		public Building (JSONObject json, String baseLink) throws JSONException, MalformedURLException, ParseException {
			
			JSONObject b = json.getJSONObject(BUILDING_TAG);
			parseBuilding(b, baseLink);
			
			JSONArray points = json.getJSONArray(POINTS_TAG);
			for (int i=0; i < points.length(); i++)
				this.punti.add(Point.parse((JSONObject) points.get(i)));
			
			JSONArray floors = json.getJSONArray(FLOORS_TAG);
			for (int i=0; i < floors.length(); i++) 
				this.piani.add(Floor.parse((JSONObject) floors.get(i), baseLink, this.punti));
			
			JSONArray paths = json.getJSONArray(PATHS_TAG);
			for (int i=0; i < paths.length(); i++) 
				this.paths.add(Path.parse((JSONObject) paths.get(i), this.punti));
			
			JSONArray rooms = json.getJSONArray(ROOMS_TAG);
			for (int i=0; i < rooms.length(); i++) 
				this.stanze.add(Room.parse((JSONObject) rooms.get(i), this.punti));
			
			Log.i("building", this.toString());
		}
		
		
		private static final String ID = "id";
		private static final String NOME = "nome";
		private static final String DESCRIZIONE = "descrizione";
		private static final String LINK = "link";
		private static final String NUMERO_DI_PIANI = "numero_di_piani";
		private static final String FOTO = "foto";
		private static final String VERSIONE = "versione";
		private static final String DATA_C = "data_creazione";
		private static final String DATA_U = "data_update";
		private static final String POSIZIONE = "posizione";
		private static final String GEOMETRIA = "geometria";
		
		private void parseBuilding(JSONObject b, String baseLink) throws JSONException, MalformedURLException, ParseException {
			
			this.id = b.getLong(ID);
			this.nome = b.getString(NOME);
			this.descrizione = b.getString(DESCRIZIONE);
			
			String link = b.getString(LINK);
			if (link.length() > 0)
				this.link = new URL(link);
			
			String foto_link = b.getString(FOTO);
			if (foto_link.length() > 0)
			this.foto_link = new URL(baseLink + link);
			
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

	
	
	