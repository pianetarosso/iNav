package it.inav.communications;

import it.inav.base_objects.Building;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class Initialize {

	private static final String HOME = "http://10.0.2.2:8000/";
	private static final String GET_BUILDING = "buildings/get/building=ID&LATITUDE&LONGITUDE&RADIUS";	 
	

	// METODI ESPOSTI PER IL GET DEL/DEI BUILDING/S
	public static void getBuildingFromId(long id) throws ClientProtocolException, IOException, JSONException, ParseException {
		
		String url = getBuildingURL(id, "0", "0", -1);
		Log.i("url", url);
		JSONObject json = JSONParser.getJSONFromUrl(url);
		new Building(json, HOME);
	}
	
	public static void getBuildingsInRadius(String latitude, String longitude, int radius) throws ClientProtocolException, IOException, JSONException {
		JSONParser.getJSONFromUrl(getBuildingURL(-1, latitude, longitude, radius));
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
	
	
	
	
	
}


