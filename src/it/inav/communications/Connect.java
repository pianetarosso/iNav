package it.inav.communications;

import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Connect {

	private static final String HOME = "http://10.0.2.2:8000/";
	private static final String GET_BUILDING = "buildings/get/building=ID&LATITUDE&LONGITUDE&RADIUS";	 
	

	// METODI ESPOSTI PER IL GET DEL/DEI BUILDING/S
	public static Building getBuildingFromId(long id) 
			throws ClientProtocolException, IOException, JSONException, ParseException, URISyntaxException {
		
		String url = getBuildingURL(id, "0", "0", -1);
		JSONObject json = JSONParser.getJSONFromUrl(url);
		return new Building(json, HOME);
	}
	
	public static List<Building> getBuildingsInRadius(String latitude, String longitude, int radius) 
			throws ClientProtocolException, IOException, JSONException, ParseException, URISyntaxException {
		
		String url = getBuildingURL(-1, latitude, longitude, radius);
		JSONObject json = JSONParser.getJSONFromUrl(url);
		return Building.Buildings(json, HOME);
	}
	
	// funzione che costruisce l'url per recuperare un edificio, in base all'id, 
	// o alla posizione e raggio
	private static String getBuildingURL(long id, String latitude, String longitude, int radius) {
		String out = HOME;
		out += GET_BUILDING.replace("ID", ""+id);
		out = out.replace("LATITUDE", latitude);
		out = out.replace("LONGITUDE", longitude);
		out = out.replace("RADIUS", ""+radius);
	
		return out;
	}
	
	// Salva le immagini del building
	public static void getImages(Building building) throws IOException {
		
		if (building.foto_link != null)
			building.foto = downloadImage(building.foto_link);
		
		for (Floor f : building.getPiani()) 
			f.immagine = downloadImage(f.link_immagine);		
	}
	
	
	// funzione per scaricare le immagini da un link
	private static Bitmap downloadImage(URI foto_link) throws IOException {
		
		
		// Open a connection to that URL. 
		URLConnection connection = foto_link.toURL().openConnection();
		
		connection.connect();
		
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		
		Bitmap b = BitmapFactory.decodeStream(bis);
		
		bis.close();
		
        return b;
	}
	
	
	
	
}


